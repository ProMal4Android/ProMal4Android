package org.spongycastle.asn1;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DERFactory {
    static final ASN1Sequence EMPTY_SEQUENCE = new DERSequence();
    static final ASN1Set EMPTY_SET = new DERSet();

    DERFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Sequence createSequence(ASN1EncodableVector v) {
        return v.size() < 1 ? EMPTY_SEQUENCE : new DLSequence(v);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1Set createSet(ASN1EncodableVector v) {
        return v.size() < 1 ? EMPTY_SET : new DLSet(v);
    }
}