package org.spongycastle.asn1;

import java.io.IOException;

/* loaded from: classes.dex */
public class DERTaggedObject extends ASN1TaggedObject {
    private static final byte[] ZERO_BYTES = new byte[0];

    public DERTaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        super(explicit, tagNo, obj);
    }

    public DERTaggedObject(int tagNo, ASN1Encodable encodable) {
        super(true, tagNo, encodable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        if (this.empty || this.explicit) {
            return true;
        }
        ASN1Primitive primitive = this.obj.toASN1Primitive().toDERObject();
        return primitive.isConstructed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        if (!this.empty) {
            ASN1Primitive primitive = this.obj.toASN1Primitive().toDERObject();
            int length = primitive.encodedLength();
            if (this.explicit) {
                return StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(length) + length;
            }
            return StreamUtil.calculateTagLength(this.tagNo) + (length - 1);
        }
        return StreamUtil.calculateTagLength(this.tagNo) + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1TaggedObject, org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        int flags;
        if (!this.empty) {
            ASN1Primitive primitive = this.obj.toASN1Primitive().toDERObject();
            if (this.explicit) {
                out.writeTag(160, this.tagNo);
                out.writeLength(primitive.encodedLength());
                out.writeObject(primitive);
                return;
            }
            if (primitive.isConstructed()) {
                flags = 160;
            } else {
                flags = 128;
            }
            out.writeTag(flags, this.tagNo);
            out.writeImplicitObject(primitive);
            return;
        }
        out.writeEncoded(160, this.tagNo, ZERO_BYTES);
    }
}