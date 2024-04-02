package org.spongycastle.asn1;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class ASN1Enumerated extends DEREnumerated {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Enumerated(byte[] bytes) {
        super(bytes);
    }

    public ASN1Enumerated(BigInteger value) {
        super(value);
    }

    public ASN1Enumerated(int value) {
        super(value);
    }
}