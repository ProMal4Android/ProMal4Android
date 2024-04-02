package org.spongycastle.asn1.eac;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1ParsingException;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERApplicationSpecific;
import org.spongycastle.asn1.DEROctetString;

/* loaded from: classes.dex */
public class CVCertificate extends ASN1Object {
    public static final byte version_1 = 0;
    private CertificateBody certificateBody;
    private byte[] signature;
    private int valid;
    private static int bodyValid = 1;
    private static int signValid = 2;
    public static String ReferenceEncoding = "ISO-8859-1";

    private void setPrivateData(DERApplicationSpecific appSpe) throws IOException {
        this.valid = 0;
        if (appSpe.getApplicationTag() == 33) {
            ASN1InputStream content = new ASN1InputStream(appSpe.getContents());
            while (true) {
                ASN1Primitive tmpObj = content.readObject();
                if (tmpObj != null) {
                    if (tmpObj instanceof DERApplicationSpecific) {
                        DERApplicationSpecific aSpe = (DERApplicationSpecific) tmpObj;
                        switch (aSpe.getApplicationTag()) {
                            case 55:
                                this.signature = aSpe.getContents();
                                this.valid |= signValid;
                                break;
                            case 78:
                                this.certificateBody = CertificateBody.getInstance(aSpe);
                                this.valid |= bodyValid;
                                break;
                            default:
                                throw new IOException("Invalid tag, not an Iso7816CertificateStructure :" + aSpe.getApplicationTag());
                        }
                    } else {
                        throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
                    }
                } else {
                    return;
                }
            }
        } else {
            throw new IOException("not a CARDHOLDER_CERTIFICATE :" + appSpe.getApplicationTag());
        }
    }

    public CVCertificate(ASN1InputStream aIS) throws IOException {
        initFrom(aIS);
    }

    private void initFrom(ASN1InputStream aIS) throws IOException {
        while (true) {
            ASN1Primitive obj = aIS.readObject();
            if (obj != null) {
                if (obj instanceof DERApplicationSpecific) {
                    setPrivateData((DERApplicationSpecific) obj);
                } else {
                    throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
                }
            } else {
                return;
            }
        }
    }

    private CVCertificate(DERApplicationSpecific appSpe) throws IOException {
        setPrivateData(appSpe);
    }

    public CVCertificate(CertificateBody body, byte[] signature) throws IOException {
        this.certificateBody = body;
        this.signature = signature;
        this.valid |= bodyValid;
        this.valid |= signValid;
    }

    public static CVCertificate getInstance(Object obj) {
        if (obj instanceof CVCertificate) {
            return (CVCertificate) obj;
        }
        if (obj != null) {
            try {
                return new CVCertificate(DERApplicationSpecific.getInstance(obj));
            } catch (IOException e) {
                throw new ASN1ParsingException("unable to parse data: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public CertificateBody getBody() {
        return this.certificateBody;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.valid != (signValid | bodyValid)) {
            return null;
        }
        v.add(this.certificateBody);
        try {
            v.add(new DERApplicationSpecific(false, 55, (ASN1Encodable) new DEROctetString(this.signature)));
            return new DERApplicationSpecific(33, v);
        } catch (IOException e) {
            throw new IllegalStateException("unable to convert signature!");
        }
    }

    public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
        CertificateHolderAuthorization cha = this.certificateBody.getCertificateHolderAuthorization();
        return cha.getOid();
    }

    public PackedDate getEffectiveDate() throws IOException {
        return this.certificateBody.getCertificateEffectiveDate();
    }

    public int getCertificateType() {
        return this.certificateBody.getCertificateType();
    }

    public PackedDate getExpirationDate() throws IOException {
        return this.certificateBody.getCertificateExpirationDate();
    }

    public int getRole() throws IOException {
        CertificateHolderAuthorization cha = this.certificateBody.getCertificateHolderAuthorization();
        return cha.getAccessRights();
    }

    public CertificationAuthorityReference getAuthorityReference() throws IOException {
        return this.certificateBody.getCertificationAuthorityReference();
    }

    public CertificateHolderReference getHolderReference() throws IOException {
        return this.certificateBody.getCertificateHolderReference();
    }

    public int getHolderAuthorizationRole() throws IOException {
        int rights = this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
        return rights & CertificateHolderAuthorization.CVCA;
    }

    public Flags getHolderAuthorizationRights() throws IOException {
        return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 31);
    }
}