package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseFactory;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.impl.SocketHttpClientConnection;
import ch.boye.httpclientandroidlib.io.HttpMessageParser;
import ch.boye.httpclientandroidlib.io.SessionInputBuffer;
import ch.boye.httpclientandroidlib.io.SessionOutputBuffer;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@NotThreadSafe
/* loaded from: classes.dex */
public class DefaultClientConnection extends SocketHttpClientConnection implements OperatedClientConnection, HttpContext {
    private boolean connSecure;
    private volatile boolean shutdown;
    private volatile Socket socket;
    private HttpHost targetHost;
    public HttpClientAndroidLog log = new HttpClientAndroidLog(getClass());
    public HttpClientAndroidLog headerLog = new HttpClientAndroidLog("ch.boye.httpclientandroidlib.headers");
    public HttpClientAndroidLog wireLog = new HttpClientAndroidLog("ch.boye.httpclientandroidlib.wire");
    private final Map<String, Object> attributes = new HashMap();

    @Override // ch.boye.httpclientandroidlib.conn.OperatedClientConnection
    public final HttpHost getTargetHost() {
        return this.targetHost;
    }

    @Override // ch.boye.httpclientandroidlib.conn.OperatedClientConnection
    public final boolean isSecure() {
        return this.connSecure;
    }

    @Override // ch.boye.httpclientandroidlib.impl.SocketHttpClientConnection
    public final Socket getSocket() {
        return this.socket;
    }

    @Override // ch.boye.httpclientandroidlib.conn.OperatedClientConnection
    public void opening(Socket sock, HttpHost target) throws IOException {
        assertNotOpen();
        this.socket = sock;
        this.targetHost = target;
        if (this.shutdown) {
            sock.close();
            throw new InterruptedIOException("Connection already shutdown");
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.OperatedClientConnection
    public void openCompleted(boolean secure, HttpParams params) throws IOException {
        assertNotOpen();
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        this.connSecure = secure;
        bind(this.socket, params);
    }

    @Override // ch.boye.httpclientandroidlib.impl.SocketHttpClientConnection, ch.boye.httpclientandroidlib.HttpConnection
    public void shutdown() throws IOException {
        this.shutdown = true;
        try {
            super.shutdown();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " shut down");
            }
            Socket sock = this.socket;
            if (sock != null) {
                sock.close();
            }
        } catch (IOException ex) {
            this.log.debug("I/O error shutting down connection", ex);
        }
    }

    @Override // ch.boye.httpclientandroidlib.impl.SocketHttpClientConnection, ch.boye.httpclientandroidlib.HttpConnection, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            super.close();
            if (this.log.isDebugEnabled()) {
                this.log.debug("Connection " + this + " closed");
            }
        } catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.SocketHttpClientConnection
    public SessionInputBuffer createSessionInputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
        if (buffersize == -1) {
            buffersize = 8192;
        }
        SessionInputBuffer inbuffer = super.createSessionInputBuffer(socket, buffersize, params);
        if (this.wireLog.isDebugEnabled()) {
            return new LoggingSessionInputBuffer(inbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return inbuffer;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.SocketHttpClientConnection
    public SessionOutputBuffer createSessionOutputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
        if (buffersize == -1) {
            buffersize = 8192;
        }
        SessionOutputBuffer outbuffer = super.createSessionOutputBuffer(socket, buffersize, params);
        if (this.wireLog.isDebugEnabled()) {
            return new LoggingSessionOutputBuffer(outbuffer, new Wire(this.wireLog), HttpProtocolParams.getHttpElementCharset(params));
        }
        return outbuffer;
    }

    @Override // ch.boye.httpclientandroidlib.impl.AbstractHttpClientConnection
    protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.OperatedClientConnection
    public void update(Socket sock, HttpHost target, boolean secure, HttpParams params) throws IOException {
        assertOpen();
        if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        }
        if (sock != null) {
            this.socket = sock;
            bind(sock, params);
        }
        this.targetHost = target;
        this.connSecure = secure;
    }

    @Override // ch.boye.httpclientandroidlib.impl.AbstractHttpClientConnection, ch.boye.httpclientandroidlib.HttpClientConnection
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        HttpResponse response = super.receiveResponseHeader();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Receiving response: " + response.getStatusLine());
        }
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug("<< " + response.getStatusLine().toString());
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                this.headerLog.debug("<< " + header.toString());
            }
        }
        return response;
    }

    @Override // ch.boye.httpclientandroidlib.impl.AbstractHttpClientConnection, ch.boye.httpclientandroidlib.HttpClientConnection
    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("Sending request: " + request.getRequestLine());
        }
        super.sendRequestHeader(request);
        if (this.headerLog.isDebugEnabled()) {
            this.headerLog.debug(">> " + request.getRequestLine().toString());
            Header[] headers = request.getAllHeaders();
            for (Header header : headers) {
                this.headerLog.debug(">> " + header.toString());
            }
        }
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public Object getAttribute(String id) {
        return this.attributes.get(id);
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public Object removeAttribute(String id) {
        return this.attributes.remove(id);
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public void setAttribute(String id, Object obj) {
        this.attributes.put(id, obj);
    }
}