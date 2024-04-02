package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* loaded from: classes.dex */
public class KEKRecipientInfo extends ASN1Object {
    private ASN1OctetString encryptedKey;
    private KEKIdentifier kekid;
    private AlgorithmIdentifier keyEncryptionAlgorithm;
    private ASN1Integer version;

    public KEKRecipientInfo(KEKIdentifier kekid, AlgorithmIdentifier keyEncryptionAlgorithm, ASN1OctetString encryptedKey) {
        this.version = new ASN1Integer(4);
        this.kekid = kekid;
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.encryptedKey = encryptedKey;
    }

    public KEKRecipientInfo(ASN1Sequence seq) {
        this.version = (ASN1Integer) seq.getObjectAt(0);
        this.kekid = KEKIdentifier.getInstance(seq.getObjectAt(1));
        this.keyEncryptionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(2));
        this.encryptedKey = (ASN1OctetString) seq.getObjectAt(3);
    }

    public static KEKRecipientInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static KEKRecipientInfo getInstance(Object obj) {
        if (obj == null || (obj instanceof KEKRecipientInfo)) {
            return (KEKRecipientInfo) obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new KEKRecipientInfo((ASN1Sequence) obj);
        }
        throw new IllegalArgumentException("Invalid KEKRecipientInfo: " + obj.getClass().getName());
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public KEKIdentifier getKekid() {
        return this.kekid;
    }

    public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
        return this.keyEncryptionAlgorithm;
    }

    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.kekid);
        v.add(this.keyEncryptionAlgorithm);
        v.add(this.encryptedKey);
        return new DERSequence(v);
    }
}