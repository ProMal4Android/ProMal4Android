package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.X509CertificateStructure;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.agreement.ECDHBasicAgreement;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class TlsECDHKeyExchange implements TlsKeyExchange {
    protected TlsAgreementCredentials agreementCredentials;
    protected TlsClientContext context;
    protected ECPrivateKeyParameters ecAgreeClientPrivateKey = null;
    protected ECPublicKeyParameters ecAgreeServerPublicKey;
    protected int keyExchange;
    protected AsymmetricKeyParameter serverPublicKey;
    protected TlsSigner tlsSigner;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsECDHKeyExchange(TlsClientContext context, int keyExchange) {
        switch (keyExchange) {
            case 16:
            case 18:
                this.tlsSigner = null;
                break;
            case 17:
                this.tlsSigner = new TlsECDSASigner();
                break;
            case 19:
                this.tlsSigner = new TlsRSASigner();
                break;
            default:
                throw new IllegalArgumentException("unsupported key exchange algorithm");
        }
        this.context = context;
        this.keyExchange = keyExchange;
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerCertificate() throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCertificate(Certificate serverCertificate) throws IOException {
        X509CertificateStructure x509Cert = serverCertificate.certs[0];
        SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
        try {
            this.serverPublicKey = PublicKeyFactory.createKey(keyInfo);
            if (this.tlsSigner == null) {
                try {
                    this.ecAgreeServerPublicKey = validateECPublicKey((ECPublicKeyParameters) this.serverPublicKey);
                    TlsUtils.validateKeyUsage(x509Cert, 8);
                } catch (ClassCastException e) {
                    throw new TlsFatalAlert((short) 46);
                }
            } else if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey)) {
                throw new TlsFatalAlert((short) 46);
            } else {
                TlsUtils.validateKeyUsage(x509Cert, 128);
            }
        } catch (RuntimeException e2) {
            throw new TlsFatalAlert((short) 43);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerKeyExchange() throws IOException {
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream is) throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void validateCertificateRequest(CertificateRequest certificateRequest) throws IOException {
        short[] types = certificateRequest.getCertificateTypes();
        for (short s : types) {
            switch (s) {
                case 1:
                case 2:
                case 64:
                case EACTags.COUNTRY_CODE_NATIONAL_DATA /* 65 */:
                case 66:
                default:
                    throw new TlsFatalAlert((short) 47);
            }
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipClientCredentials() throws IOException {
        this.agreementCredentials = null;
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCredentials(TlsCredentials clientCredentials) throws IOException {
        if (clientCredentials instanceof TlsAgreementCredentials) {
            this.agreementCredentials = (TlsAgreementCredentials) clientCredentials;
        } else if (!(clientCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short) 80);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void generateClientKeyExchange(OutputStream os) throws IOException {
        if (this.agreementCredentials == null) {
            generateEphemeralClientKeyExchange(this.ecAgreeServerPublicKey.getParameters(), os);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generatePremasterSecret() throws IOException {
        return this.agreementCredentials != null ? this.agreementCredentials.generateAgreement(this.ecAgreeServerPublicKey) : calculateECDHBasicAgreement(this.ecAgreeServerPublicKey, this.ecAgreeClientPrivateKey);
    }

    protected boolean areOnSameCurve(ECDomainParameters a, ECDomainParameters b) {
        return a.getCurve().equals(b.getCurve()) && a.getG().equals(b.getG()) && a.getN().equals(b.getN()) && a.getH().equals(b.getH());
    }

    protected byte[] externalizeKey(ECPublicKeyParameters keyParameters) throws IOException {
        return keyParameters.getQ().getEncoded();
    }

    protected AsymmetricCipherKeyPair generateECKeyPair(ECDomainParameters ecParams) {
        ECKeyPairGenerator keyPairGenerator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keyGenerationParameters = new ECKeyGenerationParameters(ecParams, this.context.getSecureRandom());
        keyPairGenerator.init(keyGenerationParameters);
        return keyPairGenerator.generateKeyPair();
    }

    protected void generateEphemeralClientKeyExchange(ECDomainParameters ecParams, OutputStream os) throws IOException {
        AsymmetricCipherKeyPair ecAgreeClientKeyPair = generateECKeyPair(ecParams);
        this.ecAgreeClientPrivateKey = (ECPrivateKeyParameters) ecAgreeClientKeyPair.getPrivate();
        byte[] keData = externalizeKey((ECPublicKeyParameters) ecAgreeClientKeyPair.getPublic());
        TlsUtils.writeOpaque8(keData, os);
    }

    protected byte[] calculateECDHBasicAgreement(ECPublicKeyParameters publicKey, ECPrivateKeyParameters privateKey) {
        ECDHBasicAgreement basicAgreement = new ECDHBasicAgreement();
        basicAgreement.init(privateKey);
        BigInteger agreement = basicAgreement.calculateAgreement(publicKey);
        return BigIntegers.asUnsignedByteArray(agreement);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ECPublicKeyParameters validateECPublicKey(ECPublicKeyParameters key) throws IOException {
        return key;
    }
}