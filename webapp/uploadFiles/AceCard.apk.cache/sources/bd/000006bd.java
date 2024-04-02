package org.spongycastle.crypto.params;

import org.spongycastle.crypto.DerivationParameters;

/* loaded from: classes.dex */
public class ISO18033KDFParameters implements DerivationParameters {
    byte[] seed;

    public ISO18033KDFParameters(byte[] seed) {
        this.seed = seed;
    }

    public byte[] getSeed() {
        return this.seed;
    }
}