package org.spongycastle.asn1.ocsp;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.X509Extensions;

/* loaded from: classes.dex */
public class SingleResponse extends ASN1Object {
    private CertID certID;
    private CertStatus certStatus;
    private DERGeneralizedTime nextUpdate;
    private Extensions singleExtensions;
    private DERGeneralizedTime thisUpdate;

    public SingleResponse(CertID certID, CertStatus certStatus, DERGeneralizedTime thisUpdate, DERGeneralizedTime nextUpdate, X509Extensions singleExtensions) {
        this(certID, certStatus, thisUpdate, nextUpdate, Extensions.getInstance(singleExtensions));
    }

    public SingleResponse(CertID certID, CertStatus certStatus, DERGeneralizedTime thisUpdate, DERGeneralizedTime nextUpdate, Extensions singleExtensions) {
        this.certID = certID;
        this.certStatus = certStatus;
        this.thisUpdate = thisUpdate;
        this.nextUpdate = nextUpdate;
        this.singleExtensions = singleExtensions;
    }

    private SingleResponse(ASN1Sequence seq) {
        this.certID = CertID.getInstance(seq.getObjectAt(0));
        this.certStatus = CertStatus.getInstance(seq.getObjectAt(1));
        this.thisUpdate = (DERGeneralizedTime) seq.getObjectAt(2);
        if (seq.size() > 4) {
            this.nextUpdate = DERGeneralizedTime.getInstance((ASN1TaggedObject) seq.getObjectAt(3), true);
            this.singleExtensions = Extensions.getInstance((ASN1TaggedObject) seq.getObjectAt(4), true);
        } else if (seq.size() > 3) {
            ASN1TaggedObject o = (ASN1TaggedObject) seq.getObjectAt(3);
            if (o.getTagNo() == 0) {
                this.nextUpdate = DERGeneralizedTime.getInstance(o, true);
            } else {
                this.singleExtensions = Extensions.getInstance(o, true);
            }
        }
    }

    public static SingleResponse getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static SingleResponse getInstance(Object obj) {
        if (obj instanceof SingleResponse) {
            return (SingleResponse) obj;
        }
        if (obj != null) {
            return new SingleResponse(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public CertID getCertID() {
        return this.certID;
    }

    public CertStatus getCertStatus() {
        return this.certStatus;
    }

    public DERGeneralizedTime getThisUpdate() {
        return this.thisUpdate;
    }

    public DERGeneralizedTime getNextUpdate() {
        return this.nextUpdate;
    }

    public Extensions getSingleExtensions() {
        return this.singleExtensions;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certID);
        v.add(this.certStatus);
        v.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            v.add(new DERTaggedObject(true, 0, this.nextUpdate));
        }
        if (this.singleExtensions != null) {
            v.add(new DERTaggedObject(true, 1, this.singleExtensions));
        }
        return new DERSequence(v);
    }
}