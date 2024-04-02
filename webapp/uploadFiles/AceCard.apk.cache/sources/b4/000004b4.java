package org.spongycastle.asn1.esf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.x500.DirectoryString;

/* loaded from: classes.dex */
public class SignerLocation extends ASN1Object {
    private DERUTF8String countryName;
    private DERUTF8String localityName;
    private ASN1Sequence postalAddress;

    private SignerLocation(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            DERTaggedObject o = (DERTaggedObject) e.nextElement();
            switch (o.getTagNo()) {
                case 0:
                    DirectoryString countryNameDirectoryString = DirectoryString.getInstance(o, true);
                    this.countryName = new DERUTF8String(countryNameDirectoryString.getString());
                    break;
                case 1:
                    DirectoryString localityNameDirectoryString = DirectoryString.getInstance(o, true);
                    this.localityName = new DERUTF8String(localityNameDirectoryString.getString());
                    break;
                case 2:
                    if (o.isExplicit()) {
                        this.postalAddress = ASN1Sequence.getInstance(o, true);
                    } else {
                        this.postalAddress = ASN1Sequence.getInstance(o, false);
                    }
                    if (this.postalAddress != null && this.postalAddress.size() > 6) {
                        throw new IllegalArgumentException("postal address must contain less than 6 strings");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("illegal tag");
            }
        }
    }

    public SignerLocation(DERUTF8String countryName, DERUTF8String localityName, ASN1Sequence postalAddress) {
        if (postalAddress != null && postalAddress.size() > 6) {
            throw new IllegalArgumentException("postal address must contain less than 6 strings");
        }
        if (countryName != null) {
            this.countryName = DERUTF8String.getInstance(countryName.toASN1Primitive());
        }
        if (localityName != null) {
            this.localityName = DERUTF8String.getInstance(localityName.toASN1Primitive());
        }
        if (postalAddress != null) {
            this.postalAddress = ASN1Sequence.getInstance(postalAddress.toASN1Primitive());
        }
    }

    public static SignerLocation getInstance(Object obj) {
        return (obj == null || (obj instanceof SignerLocation)) ? (SignerLocation) obj : new SignerLocation(ASN1Sequence.getInstance(obj));
    }

    public DERUTF8String getCountryName() {
        return this.countryName;
    }

    public DERUTF8String getLocalityName() {
        return this.localityName;
    }

    public ASN1Sequence getPostalAddress() {
        return this.postalAddress;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.countryName != null) {
            v.add(new DERTaggedObject(true, 0, this.countryName));
        }
        if (this.localityName != null) {
            v.add(new DERTaggedObject(true, 1, this.localityName));
        }
        if (this.postalAddress != null) {
            v.add(new DERTaggedObject(true, 2, this.postalAddress));
        }
        return new DERSequence(v);
    }
}