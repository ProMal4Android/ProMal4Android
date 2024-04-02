package org.spongycastle.asn1.cms;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* loaded from: classes.dex */
public class AuthenticatedData extends ASN1Object {
    private ASN1Set authAttrs;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapsulatedContentInfo;
    private ASN1OctetString mac;
    private AlgorithmIdentifier macAlgorithm;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private ASN1Set unauthAttrs;
    private ASN1Integer version;

    public AuthenticatedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, AlgorithmIdentifier macAlgorithm, AlgorithmIdentifier digestAlgorithm, ContentInfo encapsulatedContent, ASN1Set authAttrs, ASN1OctetString mac, ASN1Set unauthAttrs) {
        if ((digestAlgorithm != null || authAttrs != null) && (digestAlgorithm == null || authAttrs == null)) {
            throw new IllegalArgumentException("digestAlgorithm and authAttrs must be set together");
        }
        this.version = new ASN1Integer(calculateVersion(originatorInfo));
        this.originatorInfo = originatorInfo;
        this.macAlgorithm = macAlgorithm;
        this.digestAlgorithm = digestAlgorithm;
        this.recipientInfos = recipientInfos;
        this.encapsulatedContentInfo = encapsulatedContent;
        this.authAttrs = authAttrs;
        this.mac = mac;
        this.unauthAttrs = unauthAttrs;
    }

    public AuthenticatedData(ASN1Sequence seq) {
        int index;
        int index2 = 0 + 1;
        this.version = (ASN1Integer) seq.getObjectAt(0);
        int index3 = index2 + 1;
        ASN1Encodable tmp = seq.getObjectAt(index2);
        if (tmp instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject) tmp, false);
            tmp = seq.getObjectAt(index3);
            index3++;
        }
        this.recipientInfos = ASN1Set.getInstance(tmp);
        int index4 = index3 + 1;
        this.macAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(index3));
        int index5 = index4 + 1;
        ASN1Encodable tmp2 = seq.getObjectAt(index4);
        if (tmp2 instanceof ASN1TaggedObject) {
            this.digestAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject) tmp2, false);
            tmp2 = seq.getObjectAt(index5);
            index5++;
        }
        this.encapsulatedContentInfo = ContentInfo.getInstance(tmp2);
        int index6 = index5 + 1;
        ASN1Encodable tmp3 = seq.getObjectAt(index5);
        if (tmp3 instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject) tmp3, false);
            index = index6 + 1;
            tmp3 = seq.getObjectAt(index6);
        } else {
            index = index6;
        }
        this.mac = ASN1OctetString.getInstance(tmp3);
        if (seq.size() > index) {
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject) seq.getObjectAt(index), false);
        }
    }

    public static AuthenticatedData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static AuthenticatedData getInstance(Object obj) {
        if (obj == null || (obj instanceof AuthenticatedData)) {
            return (AuthenticatedData) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new AuthenticatedData((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid AuthenticatedData: " + obj.getClass().getName());
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorInfo getOriginatorInfo() {
        return this.originatorInfo;
    }

    public ASN1Set getRecipientInfos() {
        return this.recipientInfos;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ContentInfo getEncapsulatedContentInfo() {
        return this.encapsulatedContentInfo;
    }

    public ASN1Set getAuthAttrs() {
        return this.authAttrs;
    }

    public ASN1OctetString getMac() {
        return this.mac;
    }

    public ASN1Set getUnauthAttrs() {
        return this.unauthAttrs;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        if (this.originatorInfo != null) {
            v.add(new DERTaggedObject(false, 0, this.originatorInfo));
        }
        v.add(this.recipientInfos);
        v.add(this.macAlgorithm);
        if (this.digestAlgorithm != null) {
            v.add(new DERTaggedObject(false, 1, this.digestAlgorithm));
        }
        v.add(this.encapsulatedContentInfo);
        if (this.authAttrs != null) {
            v.add(new DERTaggedObject(false, 2, this.authAttrs));
        }
        v.add(this.mac);
        if (this.unauthAttrs != null) {
            v.add(new DERTaggedObject(false, 3, this.unauthAttrs));
        }
        return new BERSequence(v);
    }

    public static int calculateVersion(OriginatorInfo origInfo) {
        if (origInfo == null) {
            return 0;
        }
        int ver = 0;
        Enumeration e = origInfo.getCertificates().getObjects();
        while (true) {
            if (!e.hasMoreElements()) {
                break;
            }
            Object obj = e.nextElement();
            if (obj instanceof ASN1TaggedObject) {
                ASN1TaggedObject tag = (ASN1TaggedObject) obj;
                if (tag.getTagNo() == 2) {
                    ver = 1;
                } else if (tag.getTagNo() == 3) {
                    ver = 3;
                    break;
                }
            }
        }
        if (origInfo.getCRLs() != null) {
            Enumeration e2 = origInfo.getCRLs().getObjects();
            while (e2.hasMoreElements()) {
                Object obj2 = e2.nextElement();
                if ((obj2 instanceof ASN1TaggedObject) && ((ASN1TaggedObject) obj2).getTagNo() == 1) {
                    return 3;
                }
            }
            return ver;
        }
        return ver;
    }
}