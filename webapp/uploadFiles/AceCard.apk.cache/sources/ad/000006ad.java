package org.spongycastle.crypto.params;

/* loaded from: classes.dex */
public class ECKeyParameters extends AsymmetricKeyParameter {
    ECDomainParameters params;

    /* JADX INFO: Access modifiers changed from: protected */
    public ECKeyParameters(boolean isPrivate, ECDomainParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public ECDomainParameters getParameters() {
        return this.params;
    }
}