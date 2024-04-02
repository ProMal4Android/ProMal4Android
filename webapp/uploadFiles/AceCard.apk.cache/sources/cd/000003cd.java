package org.spongycastle.asn1;

import java.io.IOException;

/* loaded from: classes.dex */
public abstract class ASN1Primitive extends ASN1Object {
    abstract boolean asn1Equals(ASN1Primitive aSN1Primitive);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract int encodedLength() throws IOException;

    @Override // org.spongycastle.asn1.ASN1Object
    public abstract int hashCode();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean isConstructed();

    public static ASN1Primitive fromByteArray(byte[] data) throws IOException {
        ASN1InputStream aIn = new ASN1InputStream(data);
        try {
            return aIn.readObject();
        } catch (ClassCastException e) {
            throw new IOException("cannot recognise object in stream");
        }
    }

    @Override // org.spongycastle.asn1.ASN1Object
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return (o instanceof ASN1Encodable) && asn1Equals(((ASN1Encodable) o).toASN1Primitive());
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Primitive toDERObject() {
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1Primitive toDLObject() {
        return this;
    }
}