package org.spongycastle.crypto.params;

/* loaded from: classes.dex */
public class NTRUEncryptionKeyParameters extends AsymmetricKeyParameter {
    protected final NTRUEncryptionParameters params;

    public NTRUEncryptionKeyParameters(boolean privateKey, NTRUEncryptionParameters params) {
        super(privateKey);
        this.params = params;
    }

    public NTRUEncryptionParameters getParameters() {
        return this.params;
    }
}