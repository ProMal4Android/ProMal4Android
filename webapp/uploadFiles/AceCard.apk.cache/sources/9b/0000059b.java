package org.spongycastle.asn1.x509;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;

/* loaded from: classes.dex */
public class V2AttributeCertificateInfoGenerator {
    private DERGeneralizedTime endDate;
    private Extensions extensions;
    private Holder holder;
    private AttCertIssuer issuer;
    private DERBitString issuerUniqueID;
    private ASN1Integer serialNumber;
    private AlgorithmIdentifier signature;
    private DERGeneralizedTime startDate;
    private ASN1Integer version = new ASN1Integer(1);
    private ASN1EncodableVector attributes = new ASN1EncodableVector();

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public void addAttribute(String oid, ASN1Encodable value) {
        this.attributes.add(new Attribute(new ASN1ObjectIdentifier(oid), new DERSet(value)));
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public void setSerialNumber(ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(AttCertIssuer issuer) {
        this.issuer = issuer;
    }

    public void setStartDate(DERGeneralizedTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(DERGeneralizedTime endDate) {
        this.endDate = endDate;
    }

    public void setIssuerUniqueID(DERBitString issuerUniqueID) {
        this.issuerUniqueID = issuerUniqueID;
    }

    public void setExtensions(X509Extensions extensions) {
        this.extensions = Extensions.getInstance(extensions.toASN1Primitive());
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public AttributeCertificateInfo generateAttributeCertificateInfo() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || this.holder == null || this.attributes == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 AttributeCertificateInfo generator");
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(this.version);
        v.add(this.holder);
        v.add(this.issuer);
        v.add(this.signature);
        v.add(this.serialNumber);
        AttCertValidityPeriod validity = new AttCertValidityPeriod(this.startDate, this.endDate);
        v.add(validity);
        v.add(new DERSequence(this.attributes));
        if (this.issuerUniqueID != null) {
            v.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            v.add(this.extensions);
        }
        return AttributeCertificateInfo.getInstance(new DERSequence(v));
    }
}