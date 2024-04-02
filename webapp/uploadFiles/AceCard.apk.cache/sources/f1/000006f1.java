package org.spongycastle.crypto.tls;

import org.spongycastle.asn1.x509.X509CertificateStructure;

/* loaded from: classes.dex */
public class AlwaysValidVerifyer implements CertificateVerifyer {
    @Override // org.spongycastle.crypto.tls.CertificateVerifyer
    public boolean isValid(X509CertificateStructure[] certs) {
        return true;
    }
}