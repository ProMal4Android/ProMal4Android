package info.guardianproject.onionkit.proxy;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.conn.HttpHostConnectException;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.scheme.SocketFactory;
import ch.boye.httpclientandroidlib.impl.conn.DefaultClientConnectionOperator;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import info.guardianproject.onionkit.trust.StrongSSLSocketFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

/* loaded from: classes.dex */
public class SocksProxyClientConnOperator extends DefaultClientConnectionOperator {
    private String mProxyHost;
    private int mProxyPort;

    public SocksProxyClientConnOperator(SchemeRegistry schemes, String proxyHost, int proxyPort) {
        super(schemes);
        this.mProxyHost = proxyHost;
        this.mProxyPort = proxyPort;
    }

    @Override // ch.boye.httpclientandroidlib.impl.conn.DefaultClientConnectionOperator, ch.boye.httpclientandroidlib.conn.ClientConnectionOperator
    public void openConnection(OperatedClientConnection conn, HttpHost target, InetAddress local, HttpContext context, HttpParams params) throws IOException {
        Socket connsock;
        if (conn == null) {
            throw new IllegalArgumentException("Connection may not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target host may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        if (conn.isOpen()) {
            throw new IllegalStateException("Connection must not be open");
        }
        Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
        SocketFactory sf = schm.getSocketFactory();
        InetAddress[] addresses = InetAddress.getAllByName(target.getHostName());
        int port = schm.resolvePort(target.getPort());
        InetSocketAddress socksAddr = new InetSocketAddress(this.mProxyHost, this.mProxyPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksAddr);
        Socket sock = new Socket(proxy);
        if (sf instanceof StrongSSLSocketFactory) {
            sock.connect(new InetSocketAddress(target.getHostName(), port));
            sock = ((StrongSSLSocketFactory) sf).createSocket(sock, target.getHostName(), port, true);
        }
        conn.opening(sock, target);
        int i = 0;
        while (i < addresses.length) {
            InetAddress address = addresses[i];
            boolean last = i == addresses.length + (-1);
            try {
                if (!sock.isConnected() && sock != (connsock = sf.connectSocket(sock, address.getHostAddress(), port, local, 0, params))) {
                    sock = connsock;
                    conn.opening(sock, target);
                }
                prepareSocket(sock, context, params);
                conn.openCompleted(sf.isSecure(sock), params);
                return;
            } catch (ConnectTimeoutException ex) {
                if (!last) {
                    i++;
                } else {
                    throw ex;
                }
            } catch (ConnectException ex2) {
                if (!last) {
                    i++;
                } else {
                    throw new HttpHostConnectException(target, ex2);
                }
            }
        }
    }
}