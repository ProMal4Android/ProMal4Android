package ch.boye.httpclientandroidlib.conn.scheme;

import ch.boye.httpclientandroidlib.conn.ConnectTimeoutException;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

@Deprecated
/* loaded from: classes.dex */
class SocketFactoryAdaptor implements SocketFactory {
    private final SchemeSocketFactory factory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SocketFactoryAdaptor(SchemeSocketFactory factory) {
        this.factory = factory;
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    public Socket createSocket() throws IOException {
        HttpParams params = new BasicHttpParams();
        return this.factory.createSocket(params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        InetSocketAddress local = null;
        if (localAddress != null || localPort > 0) {
            if (localPort < 0) {
                localPort = 0;
            }
            local = new InetSocketAddress(localAddress, localPort);
        }
        InetAddress remoteAddress = InetAddress.getByName(host);
        InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
        return this.factory.connectSocket(socket, remote, local, params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    public boolean isSecure(Socket socket) throws IllegalArgumentException {
        return this.factory.isSecure(socket);
    }

    public SchemeSocketFactory getFactory() {
        return this.factory;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof SocketFactoryAdaptor) {
            return this.factory.equals(((SocketFactoryAdaptor) obj).factory);
        }
        return this.factory.equals(obj);
    }

    public int hashCode() {
        return this.factory.hashCode();
    }
}