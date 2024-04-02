package org.spongycastle.crypto.params;

import org.spongycastle.crypto.CipherParameters;

/* loaded from: classes.dex */
public class AsymmetricKeyParameter implements CipherParameters {
    boolean privateKey;

    public AsymmetricKeyParameter(boolean privateKey) {
        this.privateKey = privateKey;
    }

    public boolean isPrivate() {
        return this.privateKey;
    }
}