package org.spongycastle.asn1.smime;

import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.Attribute;

/* loaded from: classes.dex */
public class SMIMECapabilitiesAttribute extends Attribute {
    public SMIMECapabilitiesAttribute(SMIMECapabilityVector capabilities) {
        super(SMIMEAttributes.smimeCapabilities, (ASN1Set) new DERSet(new DERSequence(capabilities.toASN1EncodableVector())));
    }
}