package org.spongycastle.crypto.tls;

/* loaded from: classes.dex */
public class TlsRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1928023487348344086L;
    Throwable e;

    public TlsRuntimeException(String message, Throwable e) {
        super(message);
        this.e = e;
    }

    public TlsRuntimeException(String message) {
        super(message);
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.e;
    }
}