package ch.boye.httpclientandroidlib.impl.conn.tsccm;

import ch.boye.httpclientandroidlib.conn.ClientConnectionOperator;
import ch.boye.httpclientandroidlib.conn.OperatedClientConnection;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.impl.conn.AbstractPoolEntry;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.TimeUnit;

@Deprecated
/* loaded from: classes.dex */
public class BasicPoolEntry extends AbstractPoolEntry {
    private final long created;
    private long expiry;
    private long updated;
    private long validUntil;

    public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, ReferenceQueue<Object> queue) {
        super(op, route);
        if (route == null) {
            throw new IllegalArgumentException("HTTP route may not be null");
        }
        this.created = System.currentTimeMillis();
        this.validUntil = Long.MAX_VALUE;
        this.expiry = this.validUntil;
    }

    public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route) {
        this(op, route, -1L, TimeUnit.MILLISECONDS);
    }

    public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, long connTTL, TimeUnit timeunit) {
        super(op, route);
        if (route == null) {
            throw new IllegalArgumentException("HTTP route may not be null");
        }
        this.created = System.currentTimeMillis();
        if (connTTL > 0) {
            this.validUntil = this.created + timeunit.toMillis(connTTL);
        } else {
            this.validUntil = Long.MAX_VALUE;
        }
        this.expiry = this.validUntil;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final OperatedClientConnection getConnection() {
        return this.connection;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final HttpRoute getPlannedRoute() {
        return this.route;
    }

    protected final BasicPoolEntryRef getWeakRef() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.conn.AbstractPoolEntry
    public void shutdownEntry() {
        super.shutdownEntry();
    }

    public long getCreated() {
        return this.created;
    }

    public long getUpdated() {
        return this.updated;
    }

    public long getExpiry() {
        return this.expiry;
    }

    public long getValidUntil() {
        return this.validUntil;
    }

    public void updateExpiry(long time, TimeUnit timeunit) {
        long newExpiry;
        this.updated = System.currentTimeMillis();
        if (time > 0) {
            newExpiry = this.updated + timeunit.toMillis(time);
        } else {
            newExpiry = Long.MAX_VALUE;
        }
        this.expiry = Math.min(this.validUntil, newExpiry);
    }

    public boolean isExpired(long now) {
        return now >= this.expiry;
    }
}