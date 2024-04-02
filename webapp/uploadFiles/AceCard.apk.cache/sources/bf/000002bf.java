package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.conn.routing.RouteTracker;
import ch.boye.httpclientandroidlib.pool.PoolEntry;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/* loaded from: classes.dex */
class HttpPoolEntry extends PoolEntry<HttpRoute, OperatedClientConnection> {
    public HttpClientAndroidLog log;
    private final RouteTracker tracker;

    public HttpPoolEntry(HttpClientAndroidLog log, String id, HttpRoute route, OperatedClientConnection conn, long timeToLive, TimeUnit tunit) {
        super(id, route, conn, timeToLive, tunit);
        this.log = log;
        this.tracker = new RouteTracker(route);
    }

    @Override // ch.boye.httpclientandroidlib.pool.PoolEntry
    public boolean isExpired(long now) {
        boolean expired = super.isExpired(now);
        if (expired && this.log.isDebugEnabled()) {
            this.log.debug("Connection " + this + " expired @ " + new Date(getExpiry()));
        }
        return expired;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RouteTracker getTracker() {
        return this.tracker;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpRoute getPlannedRoute() {
        return getRoute();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpRoute getEffectiveRoute() {
        return this.tracker.toRoute();
    }

    @Override // ch.boye.httpclientandroidlib.pool.PoolEntry
    public boolean isClosed() {
        OperatedClientConnection conn = getConnection();
        return !conn.isOpen();
    }

    @Override // ch.boye.httpclientandroidlib.pool.PoolEntry
    public void close() {
        OperatedClientConnection conn = getConnection();
        try {
            conn.close();
        } catch (IOException ex) {
            this.log.debug("I/O error closing connection", ex);
        }
    }
}