package org.spongycastle.crypto.prng;

/* loaded from: classes.dex */
public interface RandomGenerator {
    void addSeedMaterial(long j);

    void addSeedMaterial(byte[] bArr);

    void nextBytes(byte[] bArr);

    void nextBytes(byte[] bArr, int i, int i2);
}