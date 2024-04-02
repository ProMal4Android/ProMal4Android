package ch.boye.httpclientandroidlib.protocol;

@Deprecated
/* loaded from: classes.dex */
public class SyncBasicHttpContext extends BasicHttpContext {
    public SyncBasicHttpContext(HttpContext parentContext) {
        super(parentContext);
    }

    public SyncBasicHttpContext() {
    }

    @Override // ch.boye.httpclientandroidlib.protocol.BasicHttpContext, ch.boye.httpclientandroidlib.protocol.HttpContext
    public synchronized Object getAttribute(String id) {
        return super.getAttribute(id);
    }

    @Override // ch.boye.httpclientandroidlib.protocol.BasicHttpContext, ch.boye.httpclientandroidlib.protocol.HttpContext
    public synchronized void setAttribute(String id, Object obj) {
        super.setAttribute(id, obj);
    }

    @Override // ch.boye.httpclientandroidlib.protocol.BasicHttpContext, ch.boye.httpclientandroidlib.protocol.HttpContext
    public synchronized Object removeAttribute(String id) {
        return super.removeAttribute(id);
    }

    @Override // ch.boye.httpclientandroidlib.protocol.BasicHttpContext
    public synchronized void clear() {
        super.clear();
    }
}