package org.spongycastle.asn1.x509;

import java.io.IOException;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTCTime;
import org.spongycastle.asn1.x500.X500Name;

/* loaded from: classes.dex */
public class V2TBSCertListGenerator {
    private static final ASN1Sequence[] reasons = new ASN1Sequence[11];
    private X500Name issuer;
    private AlgorithmIdentifier signature;
    private Time thisUpdate;
    private ASN1Integer version = new ASN1Integer(1);
    private Time nextUpdate = null;
    private Extensions extensions = null;
    private ASN1EncodableVector crlentries = new ASN1EncodableVector();

    static {
        reasons[0] = createReasonExtension(0);
        reasons[1] = createReasonExtension(1);
        reasons[2] = createReasonExtension(2);
        reasons[3] = createReasonExtension(3);
        reasons[4] = createReasonExtension(4);
        reasons[5] = createReasonExtension(5);
        reasons[6] = createReasonExtension(6);
        reasons[7] = createReasonExtension(7);
        reasons[8] = createReasonExtension(8);
        reasons[9] = createReasonExtension(9);
        reasons[10] = createReasonExtension(10);
    }

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(X509Name issuer) {
        this.issuer = X500Name.getInstance(issuer.toASN1Primitive());
    }

    public void setIssuer(X500Name issuer) {
        this.issuer = issuer;
    }

    public void setThisUpdate(DERUTCTime thisUpdate) {
        this.thisUpdate = new Time(thisUpdate);
    }

    public void setNextUpdate(DERUTCTime nextUpdate) {
        this.nextUpdate = new Time(nextUpdate);
    }

    public void setThisUpdate(Time thisUpdate) {
        this.thisUpdate = thisUpdate;
    }

    public void setNextUpdate(Time nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    public void addCRLEntry(ASN1Sequence crlEntry) {
        this.crlentries.add(crlEntry);
    }

    public void addCRLEntry(ASN1Integer userCertificate, DERUTCTime revocationDate, int reason) {
        addCRLEntry(userCertificate, new Time(revocationDate), reason);
    }

    public void addCRLEntry(ASN1Integer userCertificate, Time revocationDate, int reason) {
        addCRLEntry(userCertificate, revocationDate, reason, null);
    }

    public void addCRLEntry(ASN1Integer userCertificate, Time revocationDate, int reason, DERGeneralizedTime invalidityDate) {
        if (reason != 0) {
            ASN1EncodableVector v = new ASN1EncodableVector();
            if (reason < reasons.length) {
                if (reason < 0) {
                    throw new IllegalArgumentException("invalid reason value: " + reason);
                }
                v.add(reasons[reason]);
            } else {
                v.add(createReasonExtension(reason));
            }
            if (invalidityDate != null) {
                v.add(createInvalidityDateExtension(revocationDate));
            }
            internalAddCRLEntry(userCertificate, revocationDate, new DERSequence(v));
        } else if (invalidityDate != null) {
            ASN1EncodableVector v2 = new ASN1EncodableVector();
            v2.add(createInvalidityDateExtension(revocationDate));
            internalAddCRLEntry(userCertificate, revocationDate, new DERSequence(v2));
        } else {
            addCRLEntry(userCertificate, revocationDate, (Extensions) null);
        }
    }

    private void internalAddCRLEntry(ASN1Integer userCertificate, Time revocationDate, ASN1Sequence extensions) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(userCertificate);
        v.add(revocationDate);
        if (extensions != null) {
            v.add(extensions);
        }
        addCRLEntry(new DERSequence(v));
    }

    public void addCRLEntry(ASN1Integer userCertificate, Time revocationDate, Extensions extensions) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(userCertificate);
        v.add(revocationDate);
        if (extensions != null) {
            v.add(extensions);
        }
        addCRLEntry(new DERSequence(v));
    }

    public void setExtensions(X509Extensions extensions) {
        setExtensions(Extensions.getInstance(extensions));
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public TBSCertList generateTBSCertList() {
        if (this.signature == null || this.issuer == null || this.thisUpdate == null) {
            throw new IllegalStateException("Not all mandatory fields set in V2 TBSCertList generator.");
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.signature);
        v.add(this.issuer);
        v.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            v.add(this.nextUpdate);
        }
        if (this.crlentries.size() != 0) {
            v.add(new DERSequence(this.crlentries));
        }
        if (this.extensions != null) {
            v.add(new DERTaggedObject(0, this.extensions));
        }
        return new TBSCertList(new DERSequence(v));
    }

    private static ASN1Sequence createReasonExtension(int reasonCode) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        CRLReason crlReason = CRLReason.lookup(reasonCode);
        try {
            v.add(X509Extension.reasonCode);
            v.add(new DEROctetString(crlReason.getEncoded()));
            return new DERSequence(v);
        } catch (IOException e) {
            throw new IllegalArgumentException("error encoding reason: " + e);
        }
    }

    private static ASN1Sequence createInvalidityDateExtension(Time invalidityDate) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        try {
            v.add(X509Extension.invalidityDate);
            v.add(new DEROctetString(invalidityDate.getEncoded()));
            return new DERSequence(v);
        } catch (IOException e) {
            throw new IllegalArgumentException("error encoding reason: " + e);
        }
    }
}