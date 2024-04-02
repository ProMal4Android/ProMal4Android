package org.spongycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class DEREnumerated extends ASN1Primitive {
    private static ASN1Enumerated[] cache = new ASN1Enumerated[12];
    byte[] bytes;

    public static ASN1Enumerated getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Enumerated)) {
            return (ASN1Enumerated) obj;
        }
        if (obj instanceof DEREnumerated) {
            return new ASN1Enumerated(((DEREnumerated) obj).getValue());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DEREnumerated getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        return (explicit || (o instanceof DEREnumerated)) ? getInstance(o) : fromOctetString(((ASN1OctetString) o).getOctets());
    }

    public DEREnumerated(int value) {
        this.bytes = BigInteger.valueOf(value).toByteArray();
    }

    public DEREnumerated(BigInteger value) {
        this.bytes = value.toByteArray();
    }

    public DEREnumerated(byte[] bytes) {
        this.bytes = bytes;
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.bytes.length) + 1 + this.bytes.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        out.writeEncoded(10, this.bytes);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof DEREnumerated)) {
            return false;
        }
        DEREnumerated other = (DEREnumerated) o;
        return Arrays.areEqual(this.bytes, other.bytes);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Enumerated fromOctetString(byte[] enc) {
        if (enc.length > 1) {
            return new ASN1Enumerated(Arrays.clone(enc));
        }
        if (enc.length == 0) {
            throw new IllegalArgumentException("ENUMERATED has zero length");
        }
        int value = enc[0] & 255;
        if (value >= cache.length) {
            return new ASN1Enumerated(Arrays.clone(enc));
        }
        ASN1Enumerated possibleMatch = cache[value];
        if (possibleMatch == null) {
            ASN1Enumerated[] aSN1EnumeratedArr = cache;
            ASN1Enumerated aSN1Enumerated = new ASN1Enumerated(Arrays.clone(enc));
            aSN1EnumeratedArr[value] = aSN1Enumerated;
            return aSN1Enumerated;
        }
        return possibleMatch;
    }
}