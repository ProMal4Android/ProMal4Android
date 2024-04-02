package org.spongycastle.asn1.smime;

import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.cms.RecipientKeyIdentifier;

/* loaded from: classes.dex */
public class SMIMEEncryptionKeyPreferenceAttribute extends Attribute {
    public SMIMEEncryptionKeyPreferenceAttribute(IssuerAndSerialNumber issAndSer) {
        super(SMIMEAttributes.encrypKeyPref, (ASN1Set) new DERSet(new DERTaggedObject(false, 0, issAndSer)));
    }

    public SMIMEEncryptionKeyPreferenceAttribute(RecipientKeyIdentifier rKeyId) {
        super(SMIMEAttributes.encrypKeyPref, (ASN1Set) new DERSet(new DERTaggedObject(false, 1, rKeyId)));
    }

    public SMIMEEncryptionKeyPreferenceAttribute(ASN1OctetString sKeyId) {
        super(SMIMEAttributes.encrypKeyPref, (ASN1Set) new DERSet(new DERTaggedObject(false, 2, sKeyId)));
    }
}