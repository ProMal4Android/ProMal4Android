package org.spongycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

/* loaded from: classes.dex */
public class BERSet extends ASN1Set {
    public BERSet() {
    }

    public BERSet(ASN1Encodable obj) {
        super(obj);
    }

    public BERSet(ASN1EncodableVector v) {
        super(v, false);
    }

    public BERSet(ASN1Encodable[] a) {
        super(a, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int length = 0;
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            length += ((ASN1Encodable) e.nextElement()).toASN1Primitive().encodedLength();
        }
        return length + 2 + 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Set, org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        out.write(49);
        out.write(128);
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            out.writeObject((ASN1Encodable) e.nextElement());
        }
        out.write(0);
        out.write(0);
    }
}