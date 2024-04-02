package org.spongycastle.asn1.oiw;

import java.math.BigInteger;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* loaded from: classes.dex */
public class ElGamalParameter extends ASN1Object {
    ASN1Integer g;
    ASN1Integer p;

    public ElGamalParameter(BigInteger p, BigInteger g) {
        this.p = new ASN1Integer(p);
        this.g = new ASN1Integer(g);
    }

    public ElGamalParameter(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.p = (ASN1Integer) e.nextElement();
        this.g = (ASN1Integer) e.nextElement();
    }

    public BigInteger getP() {
        return this.p.getPositiveValue();
    }

    public BigInteger getG() {
        return this.g.getPositiveValue();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.p);
        v.add(this.g);
        return new DERSequence(v);
    }
}