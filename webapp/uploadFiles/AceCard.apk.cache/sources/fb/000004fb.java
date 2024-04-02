package org.spongycastle.asn1.pkcs;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* loaded from: classes.dex */
public class KeyDerivationFunc extends AlgorithmIdentifier {
    /* JADX INFO: Access modifiers changed from: package-private */
    public KeyDerivationFunc(ASN1Sequence seq) {
        super(seq);
    }

    public KeyDerivationFunc(ASN1ObjectIdentifier id, ASN1Encodable params) {
        super(id, params);
    }
}