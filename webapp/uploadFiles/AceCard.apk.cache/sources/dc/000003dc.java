package org.spongycastle.asn1;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class BERFactory {
    static final BERSequence EMPTY_SEQUENCE = new BERSequence();
    static final BERSet EMPTY_SET = new BERSet();

    BERFactory() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BERSequence createSequence(ASN1EncodableVector v) {
        return v.size() < 1 ? EMPTY_SEQUENCE : new BERSequence(v);
    }

    static BERSet createSet(ASN1EncodableVector v) {
        return v.size() < 1 ? EMPTY_SET : new BERSet(v);
    }
}