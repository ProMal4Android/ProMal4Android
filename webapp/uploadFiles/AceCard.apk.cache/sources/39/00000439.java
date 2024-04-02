package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* loaded from: classes.dex */
public class PollRepContent extends ASN1Object {
    private ASN1Integer certReqId;
    private ASN1Integer checkAfter;
    private PKIFreeText reason;

    private PollRepContent(ASN1Sequence seq) {
        this.certReqId = ASN1Integer.getInstance(seq.getObjectAt(0));
        this.checkAfter = ASN1Integer.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.reason = PKIFreeText.getInstance(seq.getObjectAt(2));
        }
    }

    public static PollRepContent getInstance(Object o) {
        if (o instanceof PollRepContent) {
            return (PollRepContent) o;
        }
        if (o != null) {
            return new PollRepContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }

    public ASN1Integer getCheckAfter() {
        return this.checkAfter;
    }

    public PKIFreeText getReason() {
        return this.reason;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certReqId);
        v.add(this.checkAfter);
        if (this.reason != null) {
            v.add(this.reason);
        }
        return new DERSequence(v);
    }
}