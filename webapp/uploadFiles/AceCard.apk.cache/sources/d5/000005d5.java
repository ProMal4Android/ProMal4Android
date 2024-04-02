package org.spongycastle.asn1.x9;

import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
public class X9ECPoint extends ASN1Object {
    ECPoint p;

    public X9ECPoint(ECPoint p) {
        this.p = p;
    }

    public X9ECPoint(ECCurve c, ASN1OctetString s) {
        this.p = c.decodePoint(s.getOctets());
    }

    public ECPoint getPoint() {
        return this.p;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.p.getEncoded());
    }
}