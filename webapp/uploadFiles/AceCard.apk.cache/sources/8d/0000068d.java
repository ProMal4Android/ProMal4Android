package org.spongycastle.crypto.modes.gcm;

/* loaded from: classes.dex */
public interface GCMMultiplier {
    void init(byte[] bArr);

    void multiplyH(byte[] bArr);
}