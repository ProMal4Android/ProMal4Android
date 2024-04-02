package org.spongycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class DERInteger extends ASN1Primitive {
    byte[] bytes;

    public static ASN1Integer getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Integer)) {
            return (ASN1Integer) obj;
        }
        if (obj instanceof DERInteger) {
            return new ASN1Integer(((DERInteger) obj).getValue());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Integer getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        return (explicit || (o instanceof DERInteger)) ? getInstance(o) : new ASN1Integer(ASN1OctetString.getInstance(obj.getObject()).getOctets());
    }

    public DERInteger(int value) {
        this.bytes = BigInteger.valueOf(value).toByteArray();
    }

    public DERInteger(BigInteger value) {
        this.bytes = value.toByteArray();
    }

    public DERInteger(byte[] bytes) {
        this.bytes = bytes;
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    public BigInteger getPositiveValue() {
        return new BigInteger(1, this.bytes);
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
        out.writeEncoded(2, this.bytes);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        int value = 0;
        for (int i = 0; i != this.bytes.length; i++) {
            value ^= (this.bytes[i] & 255) << (i % 4);
        }
        return value;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof DERInteger)) {
            return false;
        }
        DERInteger other = (DERInteger) o;
        return Arrays.areEqual(this.bytes, other.bytes);
    }

    public String toString() {
        return getValue().toString();
    }
}