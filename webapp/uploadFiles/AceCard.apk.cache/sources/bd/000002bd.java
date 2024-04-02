package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.pool.AbstractConnPool;
import ch.boye.httpclientandroidlib.pool.ConnFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
class HttpConnPool extends AbstractConnPool<HttpRoute, OperatedClientConnection, HttpPoolEntry> {
    private static AtomicLong COUNTER = new AtomicLong();
    public HttpClientAndroidLog log;
    private final long timeToLive;
    private final TimeUnit tunit;

    public HttpConnPool(HttpClientAndroidLog log, int defaultMaxPerRoute, int maxTotal, long timeToLive, TimeUnit tunit) {
        super(new InternalConnFactory(), defaultMaxPerRoute, maxTotal);
        this.log = log;
        this.timeToLive = timeToLive;
        this.tunit = tunit;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.pool.AbstractConnPool
    public HttpPoolEntry createEntry(HttpRoute route, OperatedClientConnection conn) {
        String id = Long.toString(COUNTER.getAndIncrement());
        return new HttpPoolEntry(this.log, id, route, conn, this.timeToLive, this.tunit);
    }

    /* loaded from: classes.dex */
    static class InternalConnFactory implements ConnFactory<HttpRoute, OperatedClientConnection> {
        InternalConnFactory() {
        }

        @Override // ch.boye.httpclientandroidlib.pool.ConnFactory
        public OperatedClientConnection create(HttpRoute route) throws IOException {
            return new DefaultClientConnection();
        }
    }
}