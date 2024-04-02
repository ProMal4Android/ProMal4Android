package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* loaded from: classes.dex */
public class PrivateKeyUsagePeriod extends ASN1Object {
    private DERGeneralizedTime _notAfter;
    private DERGeneralizedTime _notBefore;

    public static PrivateKeyUsagePeriod getInstance(Object obj) {
        if (obj instanceof PrivateKeyUsagePeriod) {
            return (PrivateKeyUsagePeriod) obj;
        }
        if (obj != null) {
            return new PrivateKeyUsagePeriod(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PrivateKeyUsagePeriod(ASN1Sequence seq) {
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            ASN1TaggedObject tObj = (ASN1TaggedObject) en.nextElement();
            if (tObj.getTagNo() == 0) {
                this._notBefore = DERGeneralizedTime.getInstance(tObj, false);
            } else if (tObj.getTagNo() == 1) {
                this._notAfter = DERGeneralizedTime.getInstance(tObj, false);
            }
        }
    }

    public DERGeneralizedTime getNotBefore() {
        return this._notBefore;
    }

    public DERGeneralizedTime getNotAfter() {
        return this._notAfter;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this._notBefore != null) {
            v.add(new DERTaggedObject(false, 0, this._notBefore));
        }
        if (this._notAfter != null) {
            v.add(new DERTaggedObject(false, 1, this._notAfter));
        }
        return new DERSequence(v);
    }
}