package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERSequence;

/* loaded from: classes.dex */
public class AttCertValidityPeriod extends ASN1Object {
    DERGeneralizedTime notAfterTime;
    DERGeneralizedTime notBeforeTime;

    public static AttCertValidityPeriod getInstance(Object obj) {
        if (obj instanceof AttCertValidityPeriod) {
            return (AttCertValidityPeriod) obj;
        }
        if (obj != null) {
            return new AttCertValidityPeriod(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private AttCertValidityPeriod(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.notBeforeTime = DERGeneralizedTime.getInstance(seq.getObjectAt(0));
        this.notAfterTime = DERGeneralizedTime.getInstance(seq.getObjectAt(1));
    }

    public AttCertValidityPeriod(DERGeneralizedTime notBeforeTime, DERGeneralizedTime notAfterTime) {
        this.notBeforeTime = notBeforeTime;
        this.notAfterTime = notAfterTime;
    }

    public DERGeneralizedTime getNotBeforeTime() {
        return this.notBeforeTime;
    }

    public DERGeneralizedTime getNotAfterTime() {
        return this.notAfterTime;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.notBeforeTime);
        v.add(this.notAfterTime);
        return new DERSequence(v);
    }
}