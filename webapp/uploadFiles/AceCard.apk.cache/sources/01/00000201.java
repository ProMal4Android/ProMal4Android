package ch.boye.httpclientandroidlib.conn.scheme;

import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

@Deprecated
/* loaded from: classes.dex */
class SchemeLayeredSocketFactoryAdaptor2 implements SchemeLayeredSocketFactory {
    private final LayeredSchemeSocketFactory factory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SchemeLayeredSocketFactoryAdaptor2(LayeredSchemeSocketFactory factory) {
        this.factory = factory;
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory
    public Socket createSocket(HttpParams params) throws IOException {
        return this.factory.createSocket(params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory
    public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        return this.factory.connectSocket(sock, remoteAddress, localAddress, params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory
    public boolean isSecure(Socket sock) throws IllegalArgumentException {
        return this.factory.isSecure(sock);
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SchemeLayeredSocketFactory
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpParams params) throws IOException, UnknownHostException {
        return this.factory.createLayeredSocket(socket, target, port, true);
    }
}