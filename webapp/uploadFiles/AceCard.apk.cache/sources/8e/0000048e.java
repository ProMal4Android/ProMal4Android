package org.spongycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1ParsingException;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERApplicationSpecific;
import org.spongycastle.asn1.DEROctetString;

/* loaded from: classes.dex */
public class CVCertificateRequest extends ASN1Object {
    int ProfileId;
    private CertificateBody certificateBody;
    byte[] encoded;
    byte[] encodedAuthorityReference;
    private byte[] outerSignature;
    String strCertificateHolderReference;
    private int valid;
    private static int bodyValid = 1;
    private static int signValid = 2;
    public static byte[] ZeroArray = {0};
    private byte[] innerSignature = null;
    ASN1ObjectIdentifier signOid = null;
    ASN1ObjectIdentifier keyOid = null;
    byte[] certificate = null;
    protected String overSignerReference = null;
    PublicKeyDataObject iso7816PubKey = null;

    private CVCertificateRequest(DERApplicationSpecific request) throws IOException {
        this.outerSignature = null;
        if (request.getApplicationTag() == 103) {
            ASN1Sequence seq = ASN1Sequence.getInstance(request.getObject(16));
            initCertBody(DERApplicationSpecific.getInstance(seq.getObjectAt(0)));
            this.outerSignature = DERApplicationSpecific.getInstance(seq.getObjectAt(seq.size() - 1)).getContents();
            return;
        }
        initCertBody(request);
    }

    private void initCertBody(DERApplicationSpecific request) throws IOException {
        if (request.getApplicationTag() == 33) {
            ASN1Sequence seq = ASN1Sequence.getInstance(request.getObject(16));
            Enumeration en = seq.getObjects();
            while (en.hasMoreElements()) {
                DERApplicationSpecific obj = DERApplicationSpecific.getInstance(en.nextElement());
                switch (obj.getApplicationTag()) {
                    case 55:
                        this.innerSignature = obj.getContents();
                        this.valid |= signValid;
                        break;
                    case 78:
                        this.certificateBody = CertificateBody.getInstance(obj);
                        this.valid |= bodyValid;
                        break;
                    default:
                        throw new IOException("Invalid tag, not an CV Certificate Request element:" + obj.getApplicationTag());
                }
            }
            return;
        }
        throw new IOException("not a CARDHOLDER_CERTIFICATE in request:" + request.getApplicationTag());
    }

    public static CVCertificateRequest getInstance(Object obj) {
        if (obj instanceof CVCertificateRequest) {
            return (CVCertificateRequest) obj;
        }
        if (obj != null) {
            try {
                return new CVCertificateRequest(DERApplicationSpecific.getInstance(obj));
            } catch (IOException e) {
                throw new ASN1ParsingException("unable to parse data: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public CertificateBody getCertificateBody() {
        return this.certificateBody;
    }

    public PublicKeyDataObject getPublicKey() {
        return this.certificateBody.getPublicKey();
    }

    public byte[] getInnerSignature() {
        return this.innerSignature;
    }

    public byte[] getOuterSignature() {
        return this.outerSignature;
    }

    public boolean hasOuterSignature() {
        return this.outerSignature != null;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.certificateBody);
        try {
            v.add(new DERApplicationSpecific(false, 55, (ASN1Encodable) new DEROctetString(this.innerSignature)));
            return new DERApplicationSpecific(33, v);
        } catch (IOException e) {
            throw new IllegalStateException("unable to convert signature!");
        }
    }
}