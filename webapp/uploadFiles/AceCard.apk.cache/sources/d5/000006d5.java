package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;

/* loaded from: classes.dex */
public class RC2Parameters implements CipherParameters {
    private int bits;
    private byte[] key;

    public RC2Parameters(byte[] key) {
        this(key, key.length > 128 ? 1024 : key.length * 8);
    }

    public RC2Parameters(byte[] key, int bits) {
        this.key = new byte[key.length];
        this.bits = bits;
        System.arraycopy(key, 0, this.key, 0, key.length);
    }

    public byte[] getKey() {
        return this.key;
    }

    public int getEffectiveKeyBits() {
        return this.bits;
    }
}