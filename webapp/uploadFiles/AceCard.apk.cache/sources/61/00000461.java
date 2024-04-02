package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;

/* loaded from: classes.dex */
public class RecipientEncryptedKey extends ASN1Object {
    private ASN1OctetString encryptedKey;
    private KeyAgreeRecipientIdentifier identifier;

    private RecipientEncryptedKey(ASN1Sequence seq) {
        this.identifier = KeyAgreeRecipientIdentifier.getInstance(seq.getObjectAt(0));
        this.encryptedKey = (ASN1OctetString) seq.getObjectAt(1);
    }

    public static RecipientEncryptedKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static RecipientEncryptedKey getInstance(Object obj) {
        if (obj == null || (obj instanceof RecipientEncryptedKey)) {
            return (RecipientEncryptedKey) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new RecipientEncryptedKey((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid RecipientEncryptedKey: " + obj.getClass().getName());
    }

    public RecipientEncryptedKey(KeyAgreeRecipientIdentifier id, ASN1OctetString encryptedKey) {
        this.identifier = id;
        this.encryptedKey = encryptedKey;
    }

    public KeyAgreeRecipientIdentifier getIdentifier() {
        return this.identifier;
    }

    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.identifier);
        v.add(this.encryptedKey);
        return new DERSequence(v);
    }
}