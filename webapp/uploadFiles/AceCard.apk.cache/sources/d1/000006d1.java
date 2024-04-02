package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;

/* loaded from: classes.dex */
public class ParametersWithIV implements CipherParameters {
    private byte[] iv;
    private CipherParameters parameters;

    public ParametersWithIV(CipherParameters parameters, byte[] iv) {
        this(parameters, iv, 0, iv.length);
    }

    public ParametersWithIV(CipherParameters parameters, byte[] iv, int ivOff, int ivLen) {
        this.iv = new byte[ivLen];
        this.parameters = parameters;
        System.arraycopy(iv, ivOff, this.iv, 0, ivLen);
    }

    public byte[] getIV() {
        return this.iv;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}