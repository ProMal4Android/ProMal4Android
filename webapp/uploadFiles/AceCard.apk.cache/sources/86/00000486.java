package org.spongycastle.asn1.cryptopro;

import com.baseapp.Constants;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
public class ECGOST3410NamedCurves {
    static final Hashtable objIds = new Hashtable();
    static final Hashtable params = new Hashtable();
    static final Hashtable names = new Hashtable();

    static {
        BigInteger mod_p = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639319");
        BigInteger mod_q = new BigInteger("115792089237316195423570985008687907853073762908499243225378155805079068850323");
        ECCurve.Fp curve = new ECCurve.Fp(mod_p, new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639316"), new BigInteger("166"));
        ECDomainParameters ecParams = new ECDomainParameters(curve, new ECPoint.Fp(curve, new ECFieldElement.Fp(curve.getQ(), new BigInteger(Constants.CLIENT_NUMBER)), new ECFieldElement.Fp(curve.getQ(), new BigInteger("64033881142927202683649881450433473985931760268884941288852745803908878638612"))), mod_q);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A, ecParams);
        BigInteger mod_p2 = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639319");
        BigInteger mod_q2 = new BigInteger("115792089237316195423570985008687907853073762908499243225378155805079068850323");
        ECCurve.Fp curve2 = new ECCurve.Fp(mod_p2, new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639316"), new BigInteger("166"));
        ECDomainParameters ecParams2 = new ECDomainParameters(curve2, new ECPoint.Fp(curve2, new ECFieldElement.Fp(curve2.getQ(), new BigInteger(Constants.CLIENT_NUMBER)), new ECFieldElement.Fp(curve2.getQ(), new BigInteger("64033881142927202683649881450433473985931760268884941288852745803908878638612"))), mod_q2);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA, ecParams2);
        BigInteger mod_p3 = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564823193");
        BigInteger mod_q3 = new BigInteger("57896044618658097711785492504343953927102133160255826820068844496087732066703");
        ECCurve.Fp curve3 = new ECCurve.Fp(mod_p3, new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564823190"), new BigInteger("28091019353058090096996979000309560759124368558014865957655842872397301267595"));
        ECDomainParameters ecParams3 = new ECDomainParameters(curve3, new ECPoint.Fp(curve3, new ECFieldElement.Fp(mod_p3, new BigInteger(Constants.CLIENT_NUMBER)), new ECFieldElement.Fp(mod_p3, new BigInteger("28792665814854611296992347458380284135028636778229113005756334730996303888124"))), mod_q3);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B, ecParams3);
        BigInteger mod_p4 = new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502619");
        BigInteger mod_q4 = new BigInteger("70390085352083305199547718019018437840920882647164081035322601458352298396601");
        ECCurve.Fp curve4 = new ECCurve.Fp(mod_p4, new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502616"), new BigInteger("32858"));
        ECDomainParameters ecParams4 = new ECDomainParameters(curve4, new ECPoint.Fp(curve4, new ECFieldElement.Fp(mod_p4, new BigInteger("0")), new ECFieldElement.Fp(mod_p4, new BigInteger("29818893917731240733471273240314769927240550812383695689146495261604565990247"))), mod_q4);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB, ecParams4);
        BigInteger mod_p5 = new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502619");
        BigInteger mod_q5 = new BigInteger("70390085352083305199547718019018437840920882647164081035322601458352298396601");
        ECCurve.Fp curve5 = new ECCurve.Fp(mod_p5, new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502616"), new BigInteger("32858"));
        ECDomainParameters ecParams5 = new ECDomainParameters(curve5, new ECPoint.Fp(curve5, new ECFieldElement.Fp(mod_p5, new BigInteger("0")), new ECFieldElement.Fp(mod_p5, new BigInteger("29818893917731240733471273240314769927240550812383695689146495261604565990247"))), mod_q5);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C, ecParams5);
        objIds.put("GostR3410-2001-CryptoPro-A", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A);
        objIds.put("GostR3410-2001-CryptoPro-B", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B);
        objIds.put("GostR3410-2001-CryptoPro-C", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C);
        objIds.put("GostR3410-2001-CryptoPro-XchA", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA);
        objIds.put("GostR3410-2001-CryptoPro-XchB", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB);
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A, "GostR3410-2001-CryptoPro-A");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B, "GostR3410-2001-CryptoPro-B");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C, "GostR3410-2001-CryptoPro-C");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA, "GostR3410-2001-CryptoPro-XchA");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB, "GostR3410-2001-CryptoPro-XchB");
    }

    public static ECDomainParameters getByOID(ASN1ObjectIdentifier oid) {
        return (ECDomainParameters) params.get(oid);
    }

    public static Enumeration getNames() {
        return objIds.keys();
    }

    public static ECDomainParameters getByName(String name) {
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) objIds.get(name);
        if (oid != null) {
            return (ECDomainParameters) params.get(oid);
        }
        return null;
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        return (String) names.get(oid);
    }

    public static ASN1ObjectIdentifier getOID(String name) {
        return (ASN1ObjectIdentifier) objIds.get(name);
    }
}