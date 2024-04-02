package org.spongycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

/* loaded from: classes.dex */
public class DERSet extends ASN1Set {
    private int bodyLength;

    public DERSet() {
        this.bodyLength = -1;
    }

    public DERSet(ASN1Encodable obj) {
        super(obj);
        this.bodyLength = -1;
    }

    public DERSet(ASN1EncodableVector v) {
        super(v, true);
        this.bodyLength = -1;
    }

    public DERSet(ASN1Encodable[] a) {
        super(a, true);
        this.bodyLength = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DERSet(ASN1EncodableVector v, boolean doSort) {
        super(v, doSort);
        this.bodyLength = -1;
    }

    private int getBodyLength() throws IOException {
        if (this.bodyLength < 0) {
            int length = 0;
            Enumeration e = getObjects();
            while (e.hasMoreElements()) {
                Object obj = e.nextElement();
                length += ((ASN1Encodable) obj).toASN1Primitive().toDERObject().encodedLength();
            }
            this.bodyLength = length;
        }
        return this.bodyLength;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int length = getBodyLength();
        return StreamUtil.calculateBodyLength(length) + 1 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Set, org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        ASN1OutputStream dOut = out.getDERSubStream();
        int length = getBodyLength();
        out.write(49);
        out.writeLength(length);
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            Object obj = e.nextElement();
            dOut.writeObject((ASN1Encodable) obj);
        }
    }
}