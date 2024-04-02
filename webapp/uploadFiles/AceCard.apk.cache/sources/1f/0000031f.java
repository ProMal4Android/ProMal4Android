package ch.boye.httpclientandroidlib.impl.pool;

import ch.boye.httpclientandroidlib.HttpClientConnection;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.annotation.ThreadSafe;
import ch.boye.httpclientandroidlib.pool.PoolEntry;
import java.io.IOException;

@ThreadSafe
/* loaded from: classes.dex */
public class BasicPoolEntry extends PoolEntry<HttpHost, HttpClientConnection> {
    public BasicPoolEntry(String id, HttpHost route, HttpClientConnection conn) {
        super(id, route, conn);
    }

    @Override // ch.boye.httpclientandroidlib.pool.PoolEntry
    public void close() {
        try {
            getConnection().close();
        } catch (IOException e) {
        }
    }

    @Override // ch.boye.httpclientandroidlib.pool.PoolEntry
    public boolean isClosed() {
        return !getConnection().isOpen();
    }
}