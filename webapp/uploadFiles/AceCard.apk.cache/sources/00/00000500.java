package org.spongycastle.asn1.pkcs;

import java.math.BigInteger;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;

/* loaded from: classes.dex */
public class PBKDF2Params extends ASN1Object {
    private ASN1Integer iterationCount;
    private ASN1Integer keyLength;
    private ASN1OctetString octStr;

    public static PBKDF2Params getInstance(Object obj) {
        if (obj instanceof PBKDF2Params) {
            return (PBKDF2Params) obj;
        }
        if (obj != null) {
            return new PBKDF2Params(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PBKDF2Params(byte[] salt, int iterationCount) {
        this.octStr = new DEROctetString(salt);
        this.iterationCount = new ASN1Integer(iterationCount);
    }

    public PBKDF2Params(byte[] salt, int iterationCount, int keyLength) {
        this(salt, iterationCount);
        this.keyLength = new ASN1Integer(keyLength);
    }

    private PBKDF2Params(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.octStr = (ASN1OctetString) e.nextElement();
        this.iterationCount = (ASN1Integer) e.nextElement();
        if (e.hasMoreElements()) {
            this.keyLength = (ASN1Integer) e.nextElement();
        } else {
            this.keyLength = null;
        }
    }

    public byte[] getSalt() {
        return this.octStr.getOctets();
    }

    public BigInteger getIterationCount() {
        return this.iterationCount.getValue();
    }

    public BigInteger getKeyLength() {
        if (this.keyLength != null) {
            return this.keyLength.getValue();
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.octStr);
        v.add(this.iterationCount);
        if (this.keyLength != null) {
            v.add(this.keyLength);
        }
        return new DERSequence(v);
    }
}