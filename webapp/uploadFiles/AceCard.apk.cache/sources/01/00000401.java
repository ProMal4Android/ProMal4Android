package org.spongycastle.asn1;

import java.io.IOException;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Strings;

/* loaded from: classes.dex */
public class DERPrintableString extends ASN1Primitive implements ASN1String {
    private byte[] string;

    public static DERPrintableString getInstance(Object obj) {
        if (obj == null || (obj instanceof DERPrintableString)) {
            return (DERPrintableString) obj;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DERPrintableString getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        return (explicit || (o instanceof DERPrintableString)) ? getInstance(o) : new DERPrintableString(ASN1OctetString.getInstance(o).getOctets());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DERPrintableString(byte[] string) {
        this.string = string;
    }

    public DERPrintableString(String string) {
        this(string, false);
    }

    public DERPrintableString(String string, boolean validate) {
        if (validate && !isPrintableString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        this.string = Strings.toByteArray(string);
    }

    @Override // org.spongycastle.asn1.ASN1String
    public String getString() {
        return Strings.fromByteArray(this.string);
    }

    public byte[] getOctets() {
        return Arrays.clone(this.string);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.string.length) + 1 + this.string.length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        out.writeEncoded(19, this.string);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.string);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof DERPrintableString)) {
            return false;
        }
        DERPrintableString s = (DERPrintableString) o;
        return Arrays.areEqual(this.string, s.string);
    }

    public String toString() {
        return getString();
    }

    public static boolean isPrintableString(String str) {
        for (int i = str.length() - 1; i >= 0; i--) {
            char ch2 = str.charAt(i);
            if (ch2 > 127) {
                return false;
            }
            if (('a' > ch2 || ch2 > 'z') && (('A' > ch2 || ch2 > 'Z') && ('0' > ch2 || ch2 > '9'))) {
                switch (ch2) {
                    case ' ':
                    case '\'':
                    case '(':
                    case EACTags.INTERCHANGE_PROFILE /* 41 */:
                    case '+':
                    case ',':
                    case '-':
                    case '.':
                    case CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA /* 47 */:
                    case CipherSuite.TLS_DH_anon_WITH_AES_256_CBC_SHA /* 58 */:
                    case '=':
                    case '?':
                        break;
                    default:
                        return false;
                }
            }
        }
        return true;
    }
}