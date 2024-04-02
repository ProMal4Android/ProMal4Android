package org.spongycastle.asn1.ocsp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEREnumerated;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.CRLReason;

/* loaded from: classes.dex */
public class RevokedInfo extends ASN1Object {
    private CRLReason revocationReason;
    private DERGeneralizedTime revocationTime;

    public RevokedInfo(DERGeneralizedTime revocationTime, CRLReason revocationReason) {
        this.revocationTime = revocationTime;
        this.revocationReason = revocationReason;
    }

    private RevokedInfo(ASN1Sequence seq) {
        this.revocationTime = (DERGeneralizedTime) seq.getObjectAt(0);
        if (seq.size() > 1) {
            this.revocationReason = CRLReason.getInstance(DEREnumerated.getInstance((ASN1TaggedObject) seq.getObjectAt(1), true));
        }
    }

    public static RevokedInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static RevokedInfo getInstance(Object obj) {
        if (obj instanceof RevokedInfo) {
            return (RevokedInfo) obj;
        }
        if (obj != null) {
            return new RevokedInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DERGeneralizedTime getRevocationTime() {
        return this.revocationTime;
    }

    public CRLReason getRevocationReason() {
        return this.revocationReason;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.revocationTime);
        if (this.revocationReason != null) {
            v.add(new DERTaggedObject(true, 0, this.revocationReason));
        }
        return new DERSequence(v);
    }
}