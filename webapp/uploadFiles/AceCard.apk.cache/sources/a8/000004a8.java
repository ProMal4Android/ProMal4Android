package org.spongycastle.asn1.esf;

import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* loaded from: classes.dex */
public class OtherHash extends ASN1Object implements ASN1Choice {
    private OtherHashAlgAndValue otherHash;
    private ASN1OctetString sha1Hash;

    public static OtherHash getInstance(Object obj) {
        if (obj instanceof OtherHash) {
            return (OtherHash) obj;
        }
        if (obj instanceof ASN1OctetString) {
            return new OtherHash((ASN1OctetString) obj);
        }
        return new OtherHash(OtherHashAlgAndValue.getInstance(obj));
    }

    private OtherHash(ASN1OctetString sha1Hash) {
        this.sha1Hash = sha1Hash;
    }

    public OtherHash(OtherHashAlgAndValue otherHash) {
        this.otherHash = otherHash;
    }

    public OtherHash(byte[] sha1Hash) {
        this.sha1Hash = new DEROctetString(sha1Hash);
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.otherHash == null ? new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1) : this.otherHash.getHashAlgorithm();
    }

    public byte[] getHashValue() {
        return this.otherHash == null ? this.sha1Hash.getOctets() : this.otherHash.getHashValue().getOctets();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.otherHash == null ? this.sha1Hash : this.otherHash.toASN1Primitive();
    }
}