package org.spongycastle.crypto.tls;

/* loaded from: classes.dex */
public interface TlsPSKIdentity {
    byte[] getPSK();

    byte[] getPSKIdentity();

    void notifyIdentityHint(byte[] bArr);

    void skipIdentityHint();
}