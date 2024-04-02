package org.spongycastle.asn1.esf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* loaded from: classes.dex */
public class CrlOcspRef extends ASN1Object {
    private CrlListID crlids;
    private OcspListID ocspids;
    private OtherRevRefs otherRev;

    public static CrlOcspRef getInstance(Object obj) {
        if (obj instanceof CrlOcspRef) {
            return (CrlOcspRef) obj;
        }
        if (obj != null) {
            return new CrlOcspRef(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private CrlOcspRef(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            DERTaggedObject o = (DERTaggedObject) e.nextElement();
            switch (o.getTagNo()) {
                case 0:
                    this.crlids = CrlListID.getInstance(o.getObject());
                    break;
                case 1:
                    this.ocspids = OcspListID.getInstance(o.getObject());
                    break;
                case 2:
                    this.otherRev = OtherRevRefs.getInstance(o.getObject());
                    break;
                default:
                    throw new IllegalArgumentException("illegal tag");
            }
        }
    }

    public CrlOcspRef(CrlListID crlids, OcspListID ocspids, OtherRevRefs otherRev) {
        this.crlids = crlids;
        this.ocspids = ocspids;
        this.otherRev = otherRev;
    }

    public CrlListID getCrlids() {
        return this.crlids;
    }

    public OcspListID getOcspids() {
        return this.ocspids;
    }

    public OtherRevRefs getOtherRev() {
        return this.otherRev;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.crlids != null) {
            v.add(new DERTaggedObject(true, 0, this.crlids.toASN1Primitive()));
        }
        if (this.ocspids != null) {
            v.add(new DERTaggedObject(true, 1, this.ocspids.toASN1Primitive()));
        }
        if (this.otherRev != null) {
            v.add(new DERTaggedObject(true, 2, this.otherRev.toASN1Primitive()));
        }
        return new DERSequence(v);
    }
}