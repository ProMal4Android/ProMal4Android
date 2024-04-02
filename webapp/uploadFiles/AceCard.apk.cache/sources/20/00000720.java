package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class TlsECDSASigner extends TlsDSASigner {
    @Override // org.spongycastle.crypto.tls.TlsSigner
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey) {
        return publicKey instanceof ECPublicKeyParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsDSASigner
    protected DSA createDSAImpl() {
        return new ECDSASigner();
    }
}