package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* loaded from: classes.dex */
public class OtherKeyAttribute extends ASN1Object {
    private ASN1Encodable keyAttr;
    private ASN1ObjectIdentifier keyAttrId;

    public static OtherKeyAttribute getInstance(Object o) {
        if (o == null || (o instanceof OtherKeyAttribute)) {
            return (OtherKeyAttribute) o;
        }
        if (o instanceof ASN1Sequence) {
            return new OtherKeyAttribute((ASN1Sequence) o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public OtherKeyAttribute(ASN1Sequence seq) {
        this.keyAttrId = (ASN1ObjectIdentifier) seq.getObjectAt(0);
        this.keyAttr = seq.getObjectAt(1);
    }

    public OtherKeyAttribute(ASN1ObjectIdentifier keyAttrId, ASN1Encodable keyAttr) {
        this.keyAttrId = keyAttrId;
        this.keyAttr = keyAttr;
    }

    public ASN1ObjectIdentifier getKeyAttrId() {
        return this.keyAttrId;
    }

    public ASN1Encodable getKeyAttr() {
        return this.keyAttr;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.keyAttrId);
        v.add(this.keyAttr);
        return new DERSequence(v);
    }
}