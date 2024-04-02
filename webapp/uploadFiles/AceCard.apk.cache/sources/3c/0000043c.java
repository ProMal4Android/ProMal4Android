package org.spongycastle.asn1.cmp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.crmf.CertId;
import org.spongycastle.asn1.x509.Extensions;

/* loaded from: classes.dex */
public class RevAnnContent extends ASN1Object {
    private DERGeneralizedTime badSinceDate;
    private CertId certId;
    private Extensions crlDetails;
    private PKIStatus status;
    private DERGeneralizedTime willBeRevokedAt;

    private RevAnnContent(ASN1Sequence seq) {
        this.status = PKIStatus.getInstance(seq.getObjectAt(0));
        this.certId = CertId.getInstance(seq.getObjectAt(1));
        this.willBeRevokedAt = DERGeneralizedTime.getInstance(seq.getObjectAt(2));
        this.badSinceDate = DERGeneralizedTime.getInstance(seq.getObjectAt(3));
        if (seq.size() > 4) {
            this.crlDetails = Extensions.getInstance(seq.getObjectAt(4));
        }
    }

    public static RevAnnContent getInstance(Object o) {
        if (o instanceof RevAnnContent) {
            return (RevAnnContent) o;
        }
        if (o != null) {
            return new RevAnnContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public PKIStatus getStatus() {
        return this.status;
    }

    public CertId getCertId() {
        return this.certId;
    }

    public DERGeneralizedTime getWillBeRevokedAt() {
        return this.willBeRevokedAt;
    }

    public DERGeneralizedTime getBadSinceDate() {
        return this.badSinceDate;
    }

    public Extensions getCrlDetails() {
        return this.crlDetails;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.status);
        v.add(this.certId);
        v.add(this.willBeRevokedAt);
        v.add(this.badSinceDate);
        if (this.crlDetails != null) {
            v.add(this.crlDetails);
        }
        return new DERSequence(v);
    }
}