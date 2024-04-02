package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERTaggedObject;

/* loaded from: classes.dex */
public class RecipientInfo extends ASN1Object implements ASN1Choice {

    /* renamed from: info  reason: collision with root package name */
    ASN1Encodable f0info;

    public RecipientInfo(KeyTransRecipientInfo info2) {
        this.f0info = info2;
    }

    public RecipientInfo(KeyAgreeRecipientInfo info2) {
        this.f0info = new DERTaggedObject(false, 1, info2);
    }

    public RecipientInfo(KEKRecipientInfo info2) {
        this.f0info = new DERTaggedObject(false, 2, info2);
    }

    public RecipientInfo(PasswordRecipientInfo info2) {
        this.f0info = new DERTaggedObject(false, 3, info2);
    }

    public RecipientInfo(OtherRecipientInfo info2) {
        this.f0info = new DERTaggedObject(false, 4, info2);
    }

    public RecipientInfo(ASN1Primitive info2) {
        this.f0info = info2;
    }

    public static RecipientInfo getInstance(Object o) {
        if (o == null || (o instanceof RecipientInfo)) {
            return (RecipientInfo) o;
        }
        if (o instanceof ASN1Sequence) {
            return new RecipientInfo((ASN1Sequence) o);
        }
        if (o instanceof ASN1TaggedObject) {
            return new RecipientInfo((ASN1TaggedObject) o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public ASN1Integer getVersion() {
        if (this.f0info instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject) this.f0info;
            switch (o.getTagNo()) {
                case 1:
                    return KeyAgreeRecipientInfo.getInstance(o, false).getVersion();
                case 2:
                    return getKEKInfo(o).getVersion();
                case 3:
                    return PasswordRecipientInfo.getInstance(o, false).getVersion();
                case 4:
                    return new ASN1Integer(0);
                default:
                    throw new IllegalStateException("unknown tag");
            }
        }
        return KeyTransRecipientInfo.getInstance(this.f0info).getVersion();
    }

    public boolean isTagged() {
        return this.f0info instanceof ASN1TaggedObject;
    }

    public ASN1Encodable getInfo() {
        if (this.f0info instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject) this.f0info;
            switch (o.getTagNo()) {
                case 1:
                    return KeyAgreeRecipientInfo.getInstance(o, false);
                case 2:
                    return getKEKInfo(o);
                case 3:
                    return PasswordRecipientInfo.getInstance(o, false);
                case 4:
                    return OtherRecipientInfo.getInstance(o, false);
                default:
                    throw new IllegalStateException("unknown tag");
            }
        }
        return KeyTransRecipientInfo.getInstance(this.f0info);
    }

    private KEKRecipientInfo getKEKInfo(ASN1TaggedObject o) {
        return o.isExplicit() ? KEKRecipientInfo.getInstance(o, true) : KEKRecipientInfo.getInstance(o, false);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.f0info.toASN1Primitive();
    }
}