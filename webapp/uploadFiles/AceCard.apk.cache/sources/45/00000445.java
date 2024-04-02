package org.spongycastle.asn1.cms;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1SequenceParser;
import org.spongycastle.asn1.ASN1SetParser;
import org.spongycastle.asn1.ASN1TaggedObjectParser;

/* loaded from: classes.dex */
public class AuthEnvelopedDataParser {
    private ASN1Encodable nextObject;
    private boolean originatorInfoCalled;
    private ASN1SequenceParser seq;
    private ASN1Integer version;

    public AuthEnvelopedDataParser(ASN1SequenceParser seq) throws IOException {
        this.seq = seq;
        this.version = ASN1Integer.getInstance(seq.readObject());
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public OriginatorInfo getOriginatorInfo() throws IOException {
        this.originatorInfoCalled = true;
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if ((this.nextObject instanceof ASN1TaggedObjectParser) && ((ASN1TaggedObjectParser) this.nextObject).getTagNo() == 0) {
            ASN1SequenceParser originatorInfo = (ASN1SequenceParser) ((ASN1TaggedObjectParser) this.nextObject).getObjectParser(16, false);
            this.nextObject = null;
            return OriginatorInfo.getInstance(originatorInfo.toASN1Primitive());
        }
        return null;
    }

    public ASN1SetParser getRecipientInfos() throws IOException {
        if (!this.originatorInfoCalled) {
            getOriginatorInfo();
        }
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        ASN1SetParser recipientInfos = (ASN1SetParser) this.nextObject;
        this.nextObject = null;
        return recipientInfos;
    }

    public EncryptedContentInfoParser getAuthEncryptedContentInfo() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            ASN1SequenceParser o = (ASN1SequenceParser) this.nextObject;
            this.nextObject = null;
            return new EncryptedContentInfoParser(o);
        }
        return null;
    }

    public ASN1SetParser getAuthAttrs() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject instanceof ASN1TaggedObjectParser) {
            ASN1Encodable o = this.nextObject;
            this.nextObject = null;
            return (ASN1SetParser) ((ASN1TaggedObjectParser) o).getObjectParser(17, false);
        }
        return null;
    }

    public ASN1OctetString getMac() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        ASN1Encodable o = this.nextObject;
        this.nextObject = null;
        return ASN1OctetString.getInstance(o.toASN1Primitive());
    }

    public ASN1SetParser getUnauthAttrs() throws IOException {
        if (this.nextObject == null) {
            this.nextObject = this.seq.readObject();
        }
        if (this.nextObject != null) {
            ASN1Encodable o = this.nextObject;
            this.nextObject = null;
            return (ASN1SetParser) ((ASN1TaggedObjectParser) o).getObjectParser(17, false);
        }
        return null;
    }
}