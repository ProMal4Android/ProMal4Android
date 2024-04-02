package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.HttpConnectionMetrics;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.ManagedClientConnection;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

@NotThreadSafe
@Deprecated
/* loaded from: classes.dex */
public abstract class AbstractClientConnAdapter implements ManagedClientConnection, HttpContext {
    private final ClientConnectionManager connManager;
    private volatile OperatedClientConnection wrappedConnection;
    private volatile boolean markedReusable = false;
    private volatile boolean released = false;
    private volatile long duration = Long.MAX_VALUE;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractClientConnAdapter(ClientConnectionManager mgr, OperatedClientConnection conn) {
        this.connManager = mgr;
        this.wrappedConnection = conn;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void detach() {
        this.wrappedConnection = null;
        this.duration = Long.MAX_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public OperatedClientConnection getWrappedConnection() {
        return this.wrappedConnection;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ClientConnectionManager getManager() {
        return this.connManager;
    }

    protected final void assertNotAborted() throws InterruptedIOException {
        if (isReleased()) {
            throw new InterruptedIOException("Connection has been shut down");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isReleased() {
        return this.released;
    }

    protected final void assertValid(OperatedClientConnection wrappedConn) throws ConnectionShutdownException {
        if (isReleased() || wrappedConn == null) {
            throw new ConnectionShutdownException();
        }
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public boolean isOpen() {
        OperatedClientConnection conn = getWrappedConnection();
        if (conn == null) {
            return false;
        }
        return conn.isOpen();
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public boolean isStale() {
        OperatedClientConnection conn;
        if (isReleased() || (conn = getWrappedConnection()) == null) {
            return true;
        }
        return conn.isStale();
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public void setSocketTimeout(int timeout) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        conn.setSocketTimeout(timeout);
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public int getSocketTimeout() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getSocketTimeout();
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public HttpConnectionMetrics getMetrics() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getMetrics();
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void flush() throws IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        conn.flush();
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public boolean isResponseAvailable(int timeout) throws IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.isResponseAvailable(timeout);
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        conn.receiveResponseEntity(response);
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        return conn.receiveResponseHeader();
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        conn.sendRequestEntity(request);
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        unmarkReusable();
        conn.sendRequestHeader(request);
    }

    @Override // ch.boye.httpclientandroidlib.HttpInetConnection
    public InetAddress getLocalAddress() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getLocalAddress();
    }

    @Override // ch.boye.httpclientandroidlib.HttpInetConnection
    public int getLocalPort() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getLocalPort();
    }

    @Override // ch.boye.httpclientandroidlib.HttpInetConnection
    public InetAddress getRemoteAddress() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getRemoteAddress();
    }

    @Override // ch.boye.httpclientandroidlib.HttpInetConnection
    public int getRemotePort() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.getRemotePort();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection, ch.boye.httpclientandroidlib.conn.HttpRoutedConnection
    public boolean isSecure() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        return conn.isSecure();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection, ch.boye.httpclientandroidlib.conn.HttpRoutedConnection
    public SSLSession getSSLSession() {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (!isOpen()) {
            return null;
        }
        Socket sock = conn.getSocket();
        if (!(sock instanceof SSLSocket)) {
            return null;
        }
        SSLSession result = ((SSLSocket) sock).getSession();
        return result;
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void markReusable() {
        this.markedReusable = true;
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void unmarkReusable() {
        this.markedReusable = false;
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public boolean isMarkedReusable() {
        return this.markedReusable;
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void setIdleDuration(long duration, TimeUnit unit) {
        if (duration > 0) {
            this.duration = unit.toMillis(duration);
        } else {
            this.duration = -1L;
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.ConnectionReleaseTrigger
    public synchronized void releaseConnection() {
        if (!this.released) {
            this.released = true;
            this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.ConnectionReleaseTrigger
    public synchronized void abortConnection() {
        if (!this.released) {
            this.released = true;
            unmarkReusable();
            try {
                shutdown();
            } catch (IOException e) {
            }
            this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
        }
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public Object getAttribute(String id) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (conn instanceof HttpContext) {
            return ((HttpContext) conn).getAttribute(id);
        }
        return null;
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public Object removeAttribute(String id) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (conn instanceof HttpContext) {
            return ((HttpContext) conn).removeAttribute(id);
        }
        return null;
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public void setAttribute(String id, Object obj) {
        OperatedClientConnection conn = getWrappedConnection();
        assertValid(conn);
        if (conn instanceof HttpContext) {
            ((HttpContext) conn).setAttribute(id, obj);
        }
    }
}