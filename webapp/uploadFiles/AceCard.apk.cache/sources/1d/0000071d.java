package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DSAPublicKeyParameters;
import org.spongycastle.crypto.signers.DSASigner;

/* loaded from: classes.dex */
class TlsDSSSigner extends TlsDSASigner {
    @Override // org.spongycastle.crypto.tls.TlsSigner
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey) {
        return publicKey instanceof DSAPublicKeyParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsDSASigner
    protected DSA createDSAImpl() {
        return new DSASigner();
    }
}