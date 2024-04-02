package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;

@Deprecated
/* loaded from: classes.dex */
public abstract class AbstractPooledConnAdapter extends AbstractClientConnAdapter {
    protected volatile AbstractPoolEntry poolEntry;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractPooledConnAdapter(ClientConnectionManager manager, AbstractPoolEntry entry) {
        super(manager, entry.connection);
        this.poolEntry = entry;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractPoolEntry getPoolEntry() {
        return this.poolEntry;
    }

    protected void assertValid(AbstractPoolEntry entry) {
        if (isReleased() || entry == null) {
            throw new ConnectionShutdownException();
        }
    }

    protected final void assertAttached() {
        if (this.poolEntry == null) {
            throw new ConnectionShutdownException();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.conn.AbstractClientConnAdapter
    public synchronized void detach() {
        this.poolEntry = null;
        super.detach();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection, ch.boye.httpclientandroidlib.conn.HttpRoutedConnection
    public HttpRoute getRoute() {
        AbstractPoolEntry entry = getPoolEntry();
        assertValid(entry);
        if (entry.tracker == null) {
            return null;
        }
        return entry.tracker.toRoute();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
        AbstractPoolEntry entry = getPoolEntry();
        assertValid(entry);
        entry.open(route, context, params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
        AbstractPoolEntry entry = getPoolEntry();
        assertValid(entry);
        entry.tunnelTarget(secure, params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
        AbstractPoolEntry entry = getPoolEntry();
        assertValid(entry);
        entry.tunnelProxy(next, secure, params);
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
        AbstractPoolEntry entry = getPoolEntry();
        assertValid(entry);
        entry.layerProtocol(context, params);
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        AbstractPoolEntry entry = getPoolEntry();
        if (entry != null) {
            entry.shutdownEntry();
        }
        OperatedClientConnection conn = getWrappedConnection();
        if (conn != null) {
            conn.close();
        }
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public void shutdown() throws IOException {
        AbstractPoolEntry entry = getPoolEntry();
        if (entry != null) {
            entry.shutdownEntry();
        }
        OperatedClientConnection conn = getWrappedConnection();
        if (conn != null) {
            conn.shutdown();
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public Object getState() {
        AbstractPoolEntry entry = getPoolEntry();
        assertValid(entry);
        return entry.getState();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ManagedClientConnection
    public void setState(Object state) {
        AbstractPoolEntry entry = getPoolEntry();
        assertValid(entry);
        entry.setState(state);
    }
}