package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.ThreadSafe;
import ch.boye.httpclientandroidlib.conn.ClientConnectionOperator;
import ch.boye.httpclientandroidlib.conn.DnsResolver;
import ch.boye.httpclientandroidlib.conn.HttpHostConnectException;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeLayeredSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

@ThreadSafe
/* loaded from: classes.dex */
public class DefaultClientConnectionOperator implements ClientConnectionOperator {
    protected final DnsResolver dnsResolver;
    public HttpClientAndroidLog log = new HttpClientAndroidLog(getClass());
    protected final SchemeRegistry schemeRegistry;

    public DefaultClientConnectionOperator(SchemeRegistry schemes) {
        if (schemes == null) {
            throw new IllegalArgumentException("Scheme registry amy not be null");
        }
        this.schemeRegistry = schemes;
        this.dnsResolver = new SystemDefaultDnsResolver();
    }

    public DefaultClientConnectionOperator(SchemeRegistry schemes, DnsResolver dnsResolver) {
        if (schemes == null) {
            throw new IllegalArgumentException("Scheme registry may not be null");
        }
        if (dnsResolver == null) {
            throw new IllegalArgumentException("DNS resolver may not be null");
        }
        this.schemeRegistry = schemes;
        this.dnsResolver = dnsResolver;
    }

    @Override // ch.boye.httpclientandroidlib.conn.ClientConnectionOperator
    public OperatedClientConnection createConnection() {
        return new DefaultClientConnection();
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x00df  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0107 A[SYNTHETIC] */
    @Override // ch.boye.httpclientandroidlib.conn.ClientConnectionOperator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void openConnection(ch.boye.httpclientandroidlib.conn.OperatedClientConnection r19, ch.boye.httpclientandroidlib.HttpHost r20, java.net.InetAddress r21, ch.boye.httpclientandroidlib.protocol.HttpContext r22, ch.boye.httpclientandroidlib.params.HttpParams r23) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 267
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.boye.httpclientandroidlib.impl.conn.DefaultClientConnectionOperator.openConnection(ch.boye.httpclientandroidlib.conn.OperatedClientConnection, ch.boye.httpclientandroidlib.HttpHost, java.net.InetAddress, ch.boye.httpclientandroidlib.protocol.HttpContext, ch.boye.httpclientandroidlib.params.HttpParams):void");
    }

    @Override // ch.boye.httpclientandroidlib.conn.ClientConnectionOperator
    public void updateSecureConnection(OperatedClientConnection conn, HttpHost target, HttpContext context, HttpParams params) throws IOException {
        if (conn == null) {
            throw new IllegalArgumentException("Connection may not be null");
        }
        if (target == null) {
            throw new IllegalArgumentException("Target host may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        if (!conn.isOpen()) {
            throw new IllegalStateException("Connection must be open");
        }
        Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
        if (!(schm.getSchemeSocketFactory() instanceof SchemeLayeredSocketFactory)) {
            throw new IllegalArgumentException("Target scheme (" + schm.getName() + ") must have layered socket factory.");
        }
        SchemeLayeredSocketFactory lsf = (SchemeLayeredSocketFactory) schm.getSchemeSocketFactory();
        try {
            Socket sock = lsf.createLayeredSocket(conn.getSocket(), target.getHostName(), schm.resolvePort(target.getPort()), params);
            prepareSocket(sock, context, params);
            conn.update(sock, target, lsf.isSecure(sock), params);
        } catch (ConnectException ex) {
            throw new HttpHostConnectException(target, ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareSocket(Socket sock, HttpContext context, HttpParams params) throws IOException {
        sock.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
        sock.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
        int linger = HttpConnectionParams.getLinger(params);
        if (linger >= 0) {
            sock.setSoLinger(linger > 0, linger);
        }
    }

    protected InetAddress[] resolveHostname(String host) throws UnknownHostException {
        return this.dnsResolver.resolve(host);
    }
}