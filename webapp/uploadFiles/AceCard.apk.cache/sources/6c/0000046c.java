package org.spongycastle.asn1.cms;

import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.DERIA5String;

/* loaded from: classes.dex */
public class TimeStampedData extends ASN1Object {
    private ASN1OctetString content;
    private DERIA5String dataUri;
    private MetaData metaData;
    private Evidence temporalEvidence;
    private ASN1Integer version;

    public TimeStampedData(DERIA5String dataUri, MetaData metaData, ASN1OctetString content, Evidence temporalEvidence) {
        this.version = new ASN1Integer(1);
        this.dataUri = dataUri;
        this.metaData = metaData;
        this.content = content;
        this.temporalEvidence = temporalEvidence;
    }

    private TimeStampedData(ASN1Sequence seq) {
        this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
        int index = 1;
        if (seq.getObjectAt(1) instanceof DERIA5String) {
            int index2 = 1 + 1;
            this.dataUri = DERIA5String.getInstance(seq.getObjectAt(1));
            index = index2;
        }
        if ((seq.getObjectAt(index) instanceof MetaData) || (seq.getObjectAt(index) instanceof ASN1Sequence)) {
            this.metaData = MetaData.getInstance(seq.getObjectAt(index));
            index++;
        }
        if (seq.getObjectAt(index) instanceof ASN1OctetString) {
            this.content = ASN1OctetString.getInstance(seq.getObjectAt(index));
            index++;
        }
        this.temporalEvidence = Evidence.getInstance(seq.getObjectAt(index));
    }

    public static TimeStampedData getInstance(Object obj) {
        if (obj instanceof TimeStampedData) {
            return (TimeStampedData) obj;
        }
        if (obj != null) {
            return new TimeStampedData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DERIA5String getDataUri() {
        return this.dataUri;
    }

    public MetaData getMetaData() {
        return this.metaData;
    }

    public ASN1OctetString getContent() {
        return this.content;
    }

    public Evidence getTemporalEvidence() {
        return this.temporalEvidence;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        if (this.dataUri != null) {
            v.add(this.dataUri);
        }
        if (this.metaData != null) {
            v.add(this.metaData);
        }
        if (this.content != null) {
            v.add(this.content);
        }
        v.add(this.temporalEvidence);
        return new BERSequence(v);
    }
}