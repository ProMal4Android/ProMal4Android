package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* loaded from: classes.dex */
public abstract class ASN1Object implements ASN1Encodable {
    @Override // org.spongycastle.asn1.ASN1Encodable
    public abstract ASN1Primitive toASN1Primitive();

    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = new ASN1OutputStream(bOut);
        aOut.writeObject(this);
        return bOut.toByteArray();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        if (encoding.equals(ASN1Encoding.DER)) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            DEROutputStream dOut = new DEROutputStream(bOut);
            dOut.writeObject(this);
            return bOut.toByteArray();
        } else if (encoding.equals(ASN1Encoding.DL)) {
            ByteArrayOutputStream bOut2 = new ByteArrayOutputStream();
            DLOutputStream dOut2 = new DLOutputStream(bOut2);
            dOut2.writeObject(this);
            return bOut2.toByteArray();
        } else {
            return getEncoded();
        }
    }

    public int hashCode() {
        return toASN1Primitive().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ASN1Encodable)) {
            return false;
        }
        ASN1Encodable other = (ASN1Encodable) o;
        return toASN1Primitive().equals(other.toASN1Primitive());
    }

    public ASN1Primitive toASN1Object() {
        return toASN1Primitive();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean hasEncodedTagValue(Object obj, int tagValue) {
        return (obj instanceof byte[]) && ((byte[]) obj)[0] == tagValue;
    }
}