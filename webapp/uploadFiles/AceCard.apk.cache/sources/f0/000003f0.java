package org.spongycastle.asn1;

import java.io.IOException;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class DERBoolean extends ASN1Primitive {
    private byte[] value;
    private static final byte[] TRUE_VALUE = {-1};
    private static final byte[] FALSE_VALUE = {0};
    public static final ASN1Boolean FALSE = new ASN1Boolean(false);
    public static final ASN1Boolean TRUE = new ASN1Boolean(true);

    public static ASN1Boolean getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Boolean)) {
            return (ASN1Boolean) obj;
        }
        if (obj instanceof DERBoolean) {
            return ((DERBoolean) obj).isTrue() ? TRUE : FALSE;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Boolean getInstance(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static DERBoolean getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        return (explicit || (o instanceof DERBoolean)) ? getInstance(o) : ASN1Boolean.fromOctetString(((ASN1OctetString) o).getOctets());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DERBoolean(byte[] value) {
        if (value.length != 1) {
            throw new IllegalArgumentException("byte value should have 1 byte in it");
        }
        if (value[0] == 0) {
            this.value = FALSE_VALUE;
        } else if (value[0] == 255) {
            this.value = TRUE_VALUE;
        } else {
            this.value = Arrays.clone(value);
        }
    }

    public DERBoolean(boolean value) {
        this.value = value ? TRUE_VALUE : FALSE_VALUE;
    }

    public boolean isTrue() {
        return this.value[0] != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        out.writeEncoded(1, this.value);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    protected boolean asn1Equals(ASN1Primitive o) {
        return o != null && (o instanceof DERBoolean) && this.value[0] == ((DERBoolean) o).value[0];
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return this.value[0];
    }

    public String toString() {
        return this.value[0] != 0 ? "TRUE" : "FALSE";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Boolean fromOctetString(byte[] value) {
        if (value.length != 1) {
            throw new IllegalArgumentException("byte value should have 1 byte in it");
        }
        if (value[0] == 0) {
            return FALSE;
        }
        if (value[0] == 255) {
            return TRUE;
        }
        return new ASN1Boolean(value);
    }
}