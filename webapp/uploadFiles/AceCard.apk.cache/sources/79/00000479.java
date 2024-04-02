package org.spongycastle.asn1.crmf;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cms.EnvelopedData;

/* loaded from: classes.dex */
public class EncryptedKey extends ASN1Object implements ASN1Choice {
    private EncryptedValue encryptedValue;
    private EnvelopedData envelopedData;

    public static EncryptedKey getInstance(Object o) {
        if (o instanceof EncryptedKey) {
            return (EncryptedKey) o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new EncryptedKey(EnvelopedData.getInstance((ASN1TaggedObject) o, false));
        }
        if (o instanceof EncryptedValue) {
            return new EncryptedKey((EncryptedValue) o);
        }
        return new EncryptedKey(EncryptedValue.getInstance(o));
    }

    public EncryptedKey(EnvelopedData envelopedData) {
        this.envelopedData = envelopedData;
    }

    public EncryptedKey(EncryptedValue encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public boolean isEncryptedValue() {
        return this.encryptedValue != null;
    }

    public ASN1Encodable getValue() {
        return this.encryptedValue != null ? this.encryptedValue : this.envelopedData;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.encryptedValue != null ? this.encryptedValue.toASN1Primitive() : new DERTaggedObject(false, 0, this.envelopedData);
    }
}