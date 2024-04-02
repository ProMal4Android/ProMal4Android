package ch.boye.httpclientandroidlib.impl.pool;

import ch.boye.httpclientandroidlib.HttpClientConnection;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.impl.DefaultHttpClientConnection;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.pool.ConnFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

@Immutable
/* loaded from: classes.dex */
public class BasicConnFactory implements ConnFactory<HttpHost, HttpClientConnection> {
    private final HttpParams params;
    private final SSLSocketFactory sslfactory;

    public BasicConnFactory(SSLSocketFactory sslfactory, HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP params may not be null");
        }
        this.sslfactory = sslfactory;
        this.params = params;
    }

    public BasicConnFactory(HttpParams params) {
        this(null, params);
    }

    protected HttpClientConnection create(Socket socket, HttpParams params) throws IOException {
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        conn.bind(socket, params);
        return conn;
    }

    @Override // ch.boye.httpclientandroidlib.pool.ConnFactory
    public HttpClientConnection create(HttpHost host) throws IOException {
        String scheme = host.getSchemeName();
        Socket socket = null;
        if (HttpHost.DEFAULT_SCHEME_NAME.equalsIgnoreCase(scheme)) {
            socket = new Socket();
        }
        if ("https".equalsIgnoreCase(scheme) && this.sslfactory != null) {
            socket = this.sslfactory.createSocket();
        }
        if (socket == null) {
            throw new IOException(scheme + " scheme is not supported");
        }
        int connectTimeout = HttpConnectionParams.getConnectionTimeout(this.params);
        int soTimeout = HttpConnectionParams.getSoTimeout(this.params);
        socket.setSoTimeout(soTimeout);
        socket.connect(new InetSocketAddress(host.getHostName(), host.getPort()), connectTimeout);
        return create(socket, this.params);
    }
}