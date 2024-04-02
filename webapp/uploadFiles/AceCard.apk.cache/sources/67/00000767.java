package org.spongycastle.util;

/* loaded from: classes.dex */
public class StoreException extends RuntimeException {
    private Throwable _e;

    public StoreException(String s, Throwable e) {
        super(s);
        this._e = e;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this._e;
    }
}