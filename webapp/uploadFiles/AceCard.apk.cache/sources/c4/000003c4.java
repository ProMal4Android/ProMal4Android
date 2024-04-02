package org.spongycastle.asn1;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class ASN1Integer extends DERInteger {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Integer(byte[] bytes) {
        super(bytes);
    }

    public ASN1Integer(BigInteger value) {
        super(value);
    }

    public ASN1Integer(int value) {
        super(value);
    }
}