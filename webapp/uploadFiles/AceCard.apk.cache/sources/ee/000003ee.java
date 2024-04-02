package org.spongycastle.asn1;

import java.io.IOException;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class DERBMPString extends ASN1Primitive implements ASN1String {
    private char[] string;

    public static DERBMPString getInstance(Object obj) {
        if (obj == null || (obj instanceof DERBMPString)) {
            return (DERBMPString) obj;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DERBMPString getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        return (explicit || (o instanceof DERBMPString)) ? getInstance(o) : new DERBMPString(ASN1OctetString.getInstance(o).getOctets());
    }

    DERBMPString(byte[] string) {
        char[] cs = new char[string.length / 2];
        for (int i = 0; i != cs.length; i++) {
            cs[i] = (char) ((string[i * 2] << 8) | (string[(i * 2) + 1] & 255));
        }
        this.string = cs;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DERBMPString(char[] string) {
        this.string = string;
    }

    public DERBMPString(String string) {
        this.string = string.toCharArray();
    }

    @Override // org.spongycastle.asn1.ASN1String
    public String getString() {
        return new String(this.string);
    }

    public String toString() {
        return getString();
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    protected boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof DERBMPString)) {
            return false;
        }
        DERBMPString s = (DERBMPString) o;
        return Arrays.areEqual(this.string, s.string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.string.length * 2) + 1 + (this.string.length * 2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        out.write(30);
        out.writeLength(this.string.length * 2);
        for (int i = 0; i != this.string.length; i++) {
            char c = this.string[i];
            out.write((byte) (c >> '\b'));
            out.write((byte) c);
        }
    }
}