package org.spongycastle.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERInteger;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.nist.NISTNamedCurves;
import org.spongycastle.asn1.oiw.ElGamalParameter;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.DHParameter;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.RSAPublicKey;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.DSAParameter;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.asn1.x9.DHDomainParameters;
import org.spongycastle.asn1.x9.DHPublicKey;
import org.spongycastle.asn1.x9.DHValidationParms;
import org.spongycastle.asn1.x9.X962NamedCurves;
import org.spongycastle.asn1.x9.X962Parameters;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9ECPoint;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;
import org.spongycastle.crypto.params.DHValidationParameters;
import org.spongycastle.crypto.params.DSAParameters;
import org.spongycastle.crypto.params.DSAPublicKeyParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ElGamalParameters;
import org.spongycastle.crypto.params.ElGamalPublicKeyParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;

/* loaded from: classes.dex */
public class PublicKeyFactory {
    public static AsymmetricKeyParameter createKey(byte[] keyInfoData) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(keyInfoData)));
    }

    public static AsymmetricKeyParameter createKey(InputStream inStr) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(inStr).readObject()));
    }

    public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo keyInfo) throws IOException {
        X9ECParameters x9;
        AlgorithmIdentifier algId = keyInfo.getAlgorithm();
        if (algId.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption) || algId.getAlgorithm().equals(X509ObjectIdentifiers.id_ea_rsa)) {
            RSAPublicKey pubKey = RSAPublicKey.getInstance(keyInfo.parsePublicKey());
            return new RSAKeyParameters(false, pubKey.getModulus(), pubKey.getPublicExponent());
        } else if (algId.getAlgorithm().equals(X9ObjectIdentifiers.dhpublicnumber)) {
            DHPublicKey dhPublicKey = DHPublicKey.getInstance(keyInfo.parsePublicKey());
            BigInteger y = dhPublicKey.getY().getValue();
            DHDomainParameters dhParams = DHDomainParameters.getInstance(algId.getParameters());
            BigInteger p = dhParams.getP().getValue();
            BigInteger g = dhParams.getG().getValue();
            BigInteger q = dhParams.getQ().getValue();
            BigInteger j = null;
            if (dhParams.getJ() != null) {
                j = dhParams.getJ().getValue();
            }
            DHValidationParameters validation = null;
            DHValidationParms dhValidationParms = dhParams.getValidationParms();
            if (dhValidationParms != null) {
                byte[] seed = dhValidationParms.getSeed().getBytes();
                BigInteger pgenCounter = dhValidationParms.getPgenCounter().getValue();
                validation = new DHValidationParameters(seed, pgenCounter.intValue());
            }
            return new DHPublicKeyParameters(y, new DHParameters(p, g, q, j, validation));
        } else if (algId.getAlgorithm().equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            DHParameter params = DHParameter.getInstance(algId.getParameters());
            DERInteger derY = (DERInteger) keyInfo.parsePublicKey();
            BigInteger lVal = params.getL();
            int l = lVal == null ? 0 : lVal.intValue();
            return new DHPublicKeyParameters(derY.getValue(), new DHParameters(params.getP(), params.getG(), null, l));
        } else if (algId.getAlgorithm().equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
            ElGamalParameter params2 = new ElGamalParameter((ASN1Sequence) algId.getParameters());
            DERInteger derY2 = (DERInteger) keyInfo.parsePublicKey();
            return new ElGamalPublicKeyParameters(derY2.getValue(), new ElGamalParameters(params2.getP(), params2.getG()));
        } else if (algId.getAlgorithm().equals(X9ObjectIdentifiers.id_dsa) || algId.getAlgorithm().equals(OIWObjectIdentifiers.dsaWithSHA1)) {
            DERInteger derY3 = (DERInteger) keyInfo.parsePublicKey();
            ASN1Encodable de = algId.getParameters();
            DSAParameters parameters = null;
            if (de != null) {
                DSAParameter params3 = DSAParameter.getInstance(de.toASN1Primitive());
                parameters = new DSAParameters(params3.getP(), params3.getQ(), params3.getG());
            }
            return new DSAPublicKeyParameters(derY3.getValue(), parameters);
        } else if (algId.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            X962Parameters params4 = new X962Parameters((ASN1Primitive) algId.getParameters());
            if (params4.isNamedCurve()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) params4.getParameters();
                x9 = X962NamedCurves.getByOID(oid);
                if (x9 == null && (x9 = SECNamedCurves.getByOID(oid)) == null && (x9 = NISTNamedCurves.getByOID(oid)) == null) {
                    x9 = TeleTrusTNamedCurves.getByOID(oid);
                }
            } else {
                x9 = X9ECParameters.getInstance(params4.getParameters());
            }
            ASN1OctetString key = new DEROctetString(keyInfo.getPublicKeyData().getBytes());
            X9ECPoint derQ = new X9ECPoint(x9.getCurve(), key);
            ECDomainParameters dParams = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            return new ECPublicKeyParameters(derQ.getPoint(), dParams);
        } else {
            throw new RuntimeException("algorithm identifier in key not recognised");
        }
    }
}