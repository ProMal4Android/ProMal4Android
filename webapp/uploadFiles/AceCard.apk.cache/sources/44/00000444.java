package org.spongycastle.asn1.cms;

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

/* loaded from: classes.dex */
public class AuthEnvelopedData extends ASN1Object {
    private ASN1Set authAttrs;
    private EncryptedContentInfo authEncryptedContentInfo;
    private ASN1OctetString mac;
    private OriginatorInfo originatorInfo;
    private ASN1Set recipientInfos;
    private ASN1Set unauthAttrs;
    private ASN1Integer version;

    public AuthEnvelopedData(OriginatorInfo originatorInfo, ASN1Set recipientInfos, EncryptedContentInfo authEncryptedContentInfo, ASN1Set authAttrs, ASN1OctetString mac, ASN1Set unauthAttrs) {
        this.version = new ASN1Integer(0);
        this.originatorInfo = originatorInfo;
        this.recipientInfos = recipientInfos;
        this.authEncryptedContentInfo = authEncryptedContentInfo;
        this.authAttrs = authAttrs;
        this.mac = mac;
        this.unauthAttrs = unauthAttrs;
    }

    public AuthEnvelopedData(ASN1Sequence seq) {
        int index = 0 + 1;
        this.version = (ASN1Integer) seq.getObjectAt(0).toASN1Primitive();
        int index2 = index + 1;
        ASN1Primitive tmp = seq.getObjectAt(index).toASN1Primitive();
        if (tmp instanceof ASN1TaggedObject) {
            this.originatorInfo = OriginatorInfo.getInstance((ASN1TaggedObject) tmp, false);
            tmp = seq.getObjectAt(index2).toASN1Primitive();
            index2++;
        }
        this.recipientInfos = ASN1Set.getInstance(tmp);
        int index3 = index2 + 1;
        this.authEncryptedContentInfo = EncryptedContentInfo.getInstance(seq.getObjectAt(index2).toASN1Primitive());
        int index4 = index3 + 1;
        ASN1Primitive tmp2 = seq.getObjectAt(index3).toASN1Primitive();
        if (tmp2 instanceof ASN1TaggedObject) {
            this.authAttrs = ASN1Set.getInstance((ASN1TaggedObject) tmp2, false);
            tmp2 = seq.getObjectAt(index4).toASN1Primitive();
            index4++;
        }
        this.mac = ASN1OctetString.getInstance(tmp2);
        if (seq.size() > index4) {
            int i = index4 + 1;
            this.unauthAttrs = ASN1Set.getInstance((ASN1TaggedObject) seq.getObjectAt(index4).toASN1Primitive(), false);
        }
    }

    public static AuthEnvelopedData getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static AuthEnvelopedData getInstance(Object obj) {
        if (obj == null || (obj instanceof AuthEnvelopedData)) {
            return (AuthEnvelopedData) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new AuthEnvelopedData((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid AuthEnvelopedData: " + obj.getClass().getName());
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

    public EncryptedContentInfo getAuthEncryptedContentInfo() {
        return this.authEncryptedContentInfo;
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
        v.add(this.authEncryptedContentInfo);
        if (this.authAttrs != null) {
            v.add(new DERTaggedObject(false, 1, this.authAttrs));
        }
        v.add(this.mac);
        if (this.unauthAttrs != null) {
            v.add(new DERTaggedObject(false, 2, this.unauthAttrs));
        }
        return new BERSequence(v);
    }
}