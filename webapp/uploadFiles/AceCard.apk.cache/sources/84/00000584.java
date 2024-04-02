package org.spongycastle.asn1.x509;

import java.util.Enumeration;
import java.util.Hashtable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;

/* loaded from: classes.dex */
public class PolicyMappings extends ASN1Object {
    ASN1Sequence seq;

    public static PolicyMappings getInstance(Object obj) {
        if (obj instanceof PolicyMappings) {
            return (PolicyMappings) obj;
        }
        if (obj != null) {
            return new PolicyMappings(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private PolicyMappings(ASN1Sequence seq) {
        this.seq = null;
        this.seq = seq;
    }

    public PolicyMappings(Hashtable mappings) {
        this.seq = null;
        ASN1EncodableVector dev = new ASN1EncodableVector();
        Enumeration it = mappings.keys();
        while (it.hasMoreElements()) {
            String idp = (String) it.nextElement();
            String sdp = (String) mappings.get(idp);
            ASN1EncodableVector dv = new ASN1EncodableVector();
            dv.add(new ASN1ObjectIdentifier(idp));
            dv.add(new ASN1ObjectIdentifier(sdp));
            dev.add(new DERSequence(dv));
        }
        this.seq = new DERSequence(dev);
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}