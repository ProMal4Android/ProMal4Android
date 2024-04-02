package org.spongycastle.asn1.x509;

import java.io.IOException;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERPrintableString;
import org.spongycastle.util.Strings;

/* loaded from: classes.dex */
public abstract class X509NameEntryConverter {
    public abstract ASN1Primitive getConvertedValue(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Primitive convertHexEncoded(String str, int off) throws IOException {
        String str2 = Strings.toLowerCase(str);
        byte[] data = new byte[(str2.length() - off) / 2];
        for (int index = 0; index != data.length; index++) {
            char left = str2.charAt((index * 2) + off);
            char right = str2.charAt((index * 2) + off + 1);
            if (left < 'a') {
                data[index] = (byte) ((left - '0') << 4);
            } else {
                data[index] = (byte) (((left - 'a') + 10) << 4);
            }
            if (right < 'a') {
                data[index] = (byte) (data[index] | ((byte) (right - '0')));
            } else {
                data[index] = (byte) (data[index] | ((byte) ((right - 'a') + 10)));
            }
        }
        ASN1InputStream aIn = new ASN1InputStream(data);
        return aIn.readObject();
    }

    protected boolean canBePrintable(String str) {
        return DERPrintableString.isPrintableString(str);
    }
}