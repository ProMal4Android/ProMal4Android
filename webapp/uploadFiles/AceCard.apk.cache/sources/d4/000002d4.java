package ch.boye.httpclientandroidlib.impl.conn.tsccm;

import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.impl.conn.AbstractPoolEntry;
import ch.boye.httpclientandroidlib.impl.conn.AbstractPooledConnAdapter;

@Deprecated
/* loaded from: classes.dex */
public class BasicPooledConnAdapter extends AbstractPooledConnAdapter {
    /* JADX INFO: Access modifiers changed from: protected */
    public BasicPooledConnAdapter(ThreadSafeClientConnManager tsccm, AbstractPoolEntry entry) {
        super(tsccm, entry);
        markReusable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.conn.AbstractClientConnAdapter
    public ClientConnectionManager getManager() {
        return super.getManager();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.conn.AbstractPooledConnAdapter
    public AbstractPoolEntry getPoolEntry() {
        return super.getPoolEntry();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.conn.AbstractPooledConnAdapter, ch.boye.httpclientandroidlib.impl.conn.AbstractClientConnAdapter
    public void detach() {
        super.detach();
    }
}