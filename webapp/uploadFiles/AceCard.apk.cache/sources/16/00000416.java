package org.spongycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class LazyEncodedSequence extends ASN1Sequence {
    private byte[] encoded;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LazyEncodedSequence(byte[] encoded) throws IOException {
        this.encoded = encoded;
    }

    private void parse() {
        Enumeration en = new LazyConstructionEnumeration(this.encoded);
        while (en.hasMoreElements()) {
            this.seq.addElement(en.nextElement());
        }
        this.encoded = null;
    }

    @Override // org.spongycastle.asn1.ASN1Sequence
    public synchronized ASN1Encodable getObjectAt(int index) {
        if (this.encoded != null) {
            parse();
        }
        return super.getObjectAt(index);
    }

    @Override // org.spongycastle.asn1.ASN1Sequence
    public synchronized Enumeration getObjects() {
        return this.encoded == null ? super.getObjects() : new LazyConstructionEnumeration(this.encoded);
    }

    @Override // org.spongycastle.asn1.ASN1Sequence
    public synchronized int size() {
        if (this.encoded != null) {
            parse();
        }
        return super.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Sequence, org.spongycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        if (this.encoded != null) {
            parse();
        }
        return super.toDERObject();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Sequence, org.spongycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        if (this.encoded != null) {
            parse();
        }
        return super.toDLObject();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        return this.encoded != null ? StreamUtil.calculateBodyLength(this.encoded.length) + 1 + this.encoded.length : super.toDLObject().encodedLength();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Sequence, org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        if (this.encoded != null) {
            out.writeEncoded(48, this.encoded);
        } else {
            super.toDLObject().encode(out);
        }
    }
}