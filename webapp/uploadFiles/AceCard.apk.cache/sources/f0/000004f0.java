package org.spongycastle.asn1.pkcs;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DLSequence;

/* loaded from: classes.dex */
public class AuthenticatedSafe extends ASN1Object {

    /* renamed from: info  reason: collision with root package name */
    private ContentInfo[] f1info;
    private boolean isBer;

    private AuthenticatedSafe(ASN1Sequence seq) {
        this.isBer = true;
        this.f1info = new ContentInfo[seq.size()];
        for (int i = 0; i != this.f1info.length; i++) {
            this.f1info[i] = ContentInfo.getInstance(seq.getObjectAt(i));
        }
        this.isBer = seq instanceof BERSequence;
    }

    public static AuthenticatedSafe getInstance(Object o) {
        if (o instanceof AuthenticatedSafe) {
            return (AuthenticatedSafe) o;
        }
        if (o != null) {
            return new AuthenticatedSafe(ASN1Sequence.getInstance(o));
        }
        return null;
    }

    public AuthenticatedSafe(ContentInfo[] info2) {
        this.isBer = true;
        this.f1info = info2;
    }

    public ContentInfo[] getContentInfo() {
        return this.f1info;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.f1info.length; i++) {
            v.add(this.f1info[i]);
        }
        return this.isBer ? new BERSequence(v) : new DLSequence(v);
    }
}