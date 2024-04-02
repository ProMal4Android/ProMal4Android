package ch.boye.httpclientandroidlib.params;

import ch.boye.httpclientandroidlib.annotation.ThreadSafe;

@ThreadSafe
/* loaded from: classes.dex */
public class SyncBasicHttpParams extends BasicHttpParams {
    private static final long serialVersionUID = 5387834869062660642L;

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams, ch.boye.httpclientandroidlib.params.HttpParams
    public synchronized boolean removeParameter(String name) {
        return super.removeParameter(name);
    }

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams, ch.boye.httpclientandroidlib.params.HttpParams
    public synchronized HttpParams setParameter(String name, Object value) {
        return super.setParameter(name, value);
    }

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams, ch.boye.httpclientandroidlib.params.HttpParams
    public synchronized Object getParameter(String name) {
        return super.getParameter(name);
    }

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams
    public synchronized boolean isParameterSet(String name) {
        return super.isParameterSet(name);
    }

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams
    public synchronized boolean isParameterSetLocally(String name) {
        return super.isParameterSetLocally(name);
    }

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams
    public synchronized void setParameters(String[] names, Object value) {
        super.setParameters(names, value);
    }

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams
    public synchronized void clear() {
        super.clear();
    }

    @Override // ch.boye.httpclientandroidlib.params.BasicHttpParams
    public synchronized Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}