package org.spongycastle.crypto.params;

import org.spongycastle.crypto.DerivationParameters;

/* loaded from: classes.dex */
public class MGFParameters implements DerivationParameters {
    byte[] seed;

    public MGFParameters(byte[] seed) {
        this(seed, 0, seed.length);
    }

    public MGFParameters(byte[] seed, int off, int len) {
        this.seed = new byte[len];
        System.arraycopy(seed, off, this.seed, 0, len);
    }

    public byte[] getSeed() {
        return this.seed;
    }
}