package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* loaded from: classes.dex */
public class CompressedData extends ASN1Object {
    private AlgorithmIdentifier compressionAlgorithm;
    private ContentInfo encapContentInfo;
    private ASN1Integer version;

    public CompressedData(AlgorithmIdentifier compressionAlgorithm, ContentInfo encapContentInfo) {
        this.version = new ASN1Integer(0);
        this.compressionAlgorithm = compressionAlgorithm;
        this.encapContentInfo = encapContentInfo;
    }

    public CompressedData(ASN1Sequence seq) {
        this.version = (ASN1Integer) seq.getObjectAt(0);
        this.compressionAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
        this.encapContentInfo = ContentInfo.getInstance(seq.getObjectAt(2));
    }

    public static CompressedData getInstance(ASN1TaggedObject _ato, boolean _explicit) {
        return getInstance(ASN1Sequence.getInstance(_ato, _explicit));
    }

    public static CompressedData getInstance(Object _obj) {
        if (_obj == null || (_obj instanceof CompressedData)) {
            return (CompressedData) _obj;
        }
        if (_obj instanceof ASN1Sequence) {
            return new CompressedData((ASN1Sequence) _obj);
        }
        throw new IllegalArgumentException("Invalid CompressedData: " + _obj.getClass().getName());
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getCompressionAlgorithmIdentifier() {
        return this.compressionAlgorithm;
    }

    public ContentInfo getEncapContentInfo() {
        return this.encapContentInfo;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.compressionAlgorithm);
        v.add(this.encapContentInfo);
        return new BERSequence(v);
    }
}