package org.spongycastle.crypto;

/* loaded from: classes.dex */
public class AsymmetricCipherKeyPair {
    private CipherParameters privateParam;
    private CipherParameters publicParam;

    public AsymmetricCipherKeyPair(CipherParameters publicParam, CipherParameters privateParam) {
        this.publicParam = publicParam;
        this.privateParam = privateParam;
    }

    public CipherParameters getPublic() {
        return this.publicParam;
    }

    public CipherParameters getPrivate() {
        return this.privateParam;
    }
}