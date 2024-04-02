package org.spongycastle.asn1.cms;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DERTaggedObject;

/* loaded from: classes.dex */
public class EnvelopedData extends ASN1Object {
    private EncryptedContentInfo encryptedContentInfo;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private ASN1Set unprotectedAttrs;
    private ASN1Integer version;

    public EnvelopedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, EncryptedContentInfo encryptedContentInfo, ASN1Set unprotectedAttrs) {
        this.version = new ASN1Integer(calculateVersion(originatorInfo, recipientInfos, unprotectedAttrs));
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        this.encryptedContentInfo = encryptedContentInfo;
        this.unprotectedAttrs = unprotectedAttrs;
    }

    public EnvelopedData(ASN1Sequence seq) {
        int index = 0 + 1;
        this.version = (ASN1Integer) seq.getObjectAt(0);
        int index2 = index + 1;
        ASN1Encodable tmp = seq.getObjectAt(index);
        if (tmp instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject) tmp, false);
            tmp = seq.getObjectAt(index2);
            index2++;
        }
        this.recipientInfos = ASN1Set.getInstance(tmp);
        int index3 = index2 + 1;
        this.encryptedContentInfo = EncryptedContentInfo.getInstance(seq.getObjectAt(index2));
        if (seq.size() > index3) {
            this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject) seq.getObjectAt(index3), false);
        }
    }

    public static EnvelopedData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static EnvelopedData getInstance(Object obj) {
        if (obj instanceof EnvelopedData) {
            return (EnvelopedData) obj;
        }
        if (obj != null) {
            return new EnvelopedData(ASN1Sequence.getInstance(obj));
        }
        return null;
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

    public EncryptedContentInfo getEncryptedContentInfo() {
        return this.encryptedContentInfo;
    }

    public ASN1Set getUnprotectedAttrs() {
        return this.unprotectedAttrs;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        if (this.originatorInfo != null) {
            v.add(new DERTaggedObject(false, 0, this.originatorInfo));
        }
        v.add(this.recipientInfos);
        v.add(this.encryptedContentInfo);
        if (this.unprotectedAttrs != null) {
            v.add(new DERTaggedObject(false, 1, this.unprotectedAttrs));
        }
        return new BERSequence(v);
    }

    public static int calculateVersion(OriginatorInfo originatorInfo, ASN1Set recipientInfos, ASN1Set unprotectedAttrs) {
        if (originatorInfo != null || unprotectedAttrs != null) {
            return 2;
        }
        Enumeration e = recipientInfos.getObjects();
        while (e.hasMoreElements()) {
            RecipientInfo ri = RecipientInfo.getInstance(e.nextElement());
            if (ri.getVersion().getValue().intValue() != 0) {
                return 2;
            }
        }
        return 0;
    }
}