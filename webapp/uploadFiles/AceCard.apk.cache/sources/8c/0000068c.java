package org.spongycastle.crypto.modes.gcm;

/* loaded from: classes.dex */
public interface GCMExponentiator {
    void exponentiateX(long j, byte[] bArr);

    void init(byte[] bArr);
}