package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERBoolean;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;

/* loaded from: classes.dex */
public class MetaData extends ASN1Object {
    private DERUTF8String fileName;
    private DERBoolean hashProtected;
    private DERIA5String mediaType;
    private Attributes otherMetaData;

    public MetaData(DERBoolean hashProtected, DERUTF8String fileName, DERIA5String mediaType, Attributes otherMetaData) {
        this.hashProtected = hashProtected;
        this.fileName = fileName;
        this.mediaType = mediaType;
        this.otherMetaData = otherMetaData;
    }

    private MetaData(ASN1Sequence seq) {
        this.hashProtected = DERBoolean.getInstance(seq.getObjectAt(0));
        int index = 1;
        if (1 < seq.size() && (seq.getObjectAt(1) instanceof DERUTF8String)) {
            this.fileName = DERUTF8String.getInstance(seq.getObjectAt(1));
            index = 1 + 1;
        }
        if (index < seq.size() && (seq.getObjectAt(index) instanceof DERIA5String)) {
            this.mediaType = DERIA5String.getInstance(seq.getObjectAt(index));
            index++;
        }
        if (index < seq.size()) {
            int i = index + 1;
            this.otherMetaData = Attributes.getInstance(seq.getObjectAt(index));
        }
    }

    public static MetaData getInstance(Object obj) {
        if (obj instanceof MetaData) {
            return (MetaData) obj;
        }
        if (obj != null) {
            return new MetaData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.hashProtected);
        if (this.fileName != null) {
            v.add(this.fileName);
        }
        if (this.mediaType != null) {
            v.add(this.mediaType);
        }
        if (this.otherMetaData != null) {
            v.add(this.otherMetaData);
        }
        return new DERSequence(v);
    }

    public boolean isHashProtected() {
        return this.hashProtected.isTrue();
    }

    public DERUTF8String getFileName() {
        return this.fileName;
    }

    public DERIA5String getMediaType() {
        return this.mediaType;
    }

    public Attributes getOtherMetaData() {
        return this.otherMetaData;
    }
}