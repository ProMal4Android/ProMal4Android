package org.spongycastle.crypto.tls;

import org.spongycastle.asn1.x509.X509CertificateStructure;

/* loaded from: classes.dex */
public interface CertificateVerifyer {
    boolean isValid(X509CertificateStructure[] x509CertificateStructureArr);
}