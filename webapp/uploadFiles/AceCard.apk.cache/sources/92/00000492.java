package org.spongycastle.asn1.eac;

/* loaded from: classes.dex */
public class CertificationAuthorityReference extends CertificateHolderReference {
    public CertificationAuthorityReference(String countryCode, String holderMnemonic, String sequenceNumber) {
        super(countryCode, holderMnemonic, sequenceNumber);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CertificationAuthorityReference(byte[] contents) {
        super(contents);
    }
}