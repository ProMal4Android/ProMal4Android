package ch.boye.httpclientandroidlib.conn.scheme;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

@Immutable
/* loaded from: classes.dex */
public class PlainSocketFactory implements SocketFactory, SchemeSocketFactory {
    private final HostNameResolver nameResolver;

    public static PlainSocketFactory getSocketFactory() {
        return new PlainSocketFactory();
    }

    @Deprecated
    public PlainSocketFactory(HostNameResolver nameResolver) {
        this.nameResolver = nameResolver;
    }

    public PlainSocketFactory() {
        this.nameResolver = null;
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory
    public Socket createSocket(HttpParams params) {
        return new Socket();
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    public Socket createSocket() {
        return new Socket();
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory
    public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, ConnectTimeoutException {
        if (remoteAddress == null) {
            throw new IllegalArgumentException("Remote address may not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        Socket sock = socket;
        if (sock == null) {
            sock = createSocket();
        }
        if (localAddress != null) {
            sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
            sock.bind(localAddress);
        }
        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);
        try {
            sock.setSoTimeout(soTimeout);
            sock.connect(remoteAddress, connTimeout);
            return sock;
        } catch (SocketTimeoutException e) {
            throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    public final boolean isSecure(Socket sock) throws IllegalArgumentException {
        if (sock == null) {
            throw new IllegalArgumentException("Socket may not be null.");
        }
        if (sock.isClosed()) {
            throw new IllegalArgumentException("Socket is closed.");
        }
        return false;
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    @Deprecated
    public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        InetAddress remoteAddress;
        InetSocketAddress local = null;
        if (localAddress != null || localPort > 0) {
            if (localPort < 0) {
                localPort = 0;
            }
            local = new InetSocketAddress(localAddress, localPort);
        }
        if (this.nameResolver != null) {
            remoteAddress = this.nameResolver.resolve(host);
        } else {
            remoteAddress = InetAddress.getByName(host);
        }
        InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
        return connectSocket(socket, remote, local, params);
    }
}