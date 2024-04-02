package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.X509CertificateStructure;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPrivateKeyParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;
import org.spongycastle.crypto.util.PublicKeyFactory;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class TlsDHKeyExchange implements TlsKeyExchange {
    protected static final BigInteger ONE = BigInteger.valueOf(1);
    protected static final BigInteger TWO = BigInteger.valueOf(2);
    protected TlsAgreementCredentials agreementCredentials;
    protected TlsClientContext context;
    protected int keyExchange;
    protected TlsSigner tlsSigner;
    protected AsymmetricKeyParameter serverPublicKey = null;
    protected DHPublicKeyParameters dhAgreeServerPublicKey = null;
    protected DHPrivateKeyParameters dhAgreeClientPrivateKey = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsDHKeyExchange(TlsClientContext context, int keyExchange) {
        switch (keyExchange) {
            case 3:
                this.tlsSigner = new TlsDSSSigner();
                break;
            case 4:
            case 6:
            case 8:
            default:
                throw new IllegalArgumentException("unsupported key exchange algorithm");
            case 5:
                this.tlsSigner = new TlsRSASigner();
                break;
            case 7:
            case 9:
                this.tlsSigner = null;
                break;
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
                    this.dhAgreeServerPublicKey = validateDHPublicKey((DHPublicKeyParameters) this.serverPublicKey);
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
                case 3:
                case 4:
                case 64:
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
            generateEphemeralClientKeyExchange(this.dhAgreeServerPublicKey.getParameters(), os);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generatePremasterSecret() throws IOException {
        return this.agreementCredentials != null ? this.agreementCredentials.generateAgreement(this.dhAgreeServerPublicKey) : calculateDHBasicAgreement(this.dhAgreeServerPublicKey, this.dhAgreeClientPrivateKey);
    }

    protected boolean areCompatibleParameters(DHParameters a, DHParameters b) {
        return a.getP().equals(b.getP()) && a.getG().equals(b.getG());
    }

    protected byte[] calculateDHBasicAgreement(DHPublicKeyParameters publicKey, DHPrivateKeyParameters privateKey) {
        return TlsDHUtils.calculateDHBasicAgreement(publicKey, privateKey);
    }

    protected AsymmetricCipherKeyPair generateDHKeyPair(DHParameters dhParams) {
        return TlsDHUtils.generateDHKeyPair(this.context.getSecureRandom(), dhParams);
    }

    protected void generateEphemeralClientKeyExchange(DHParameters dhParams, OutputStream os) throws IOException {
        this.dhAgreeClientPrivateKey = TlsDHUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), dhParams, os);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public DHPublicKeyParameters validateDHPublicKey(DHPublicKeyParameters key) throws IOException {
        return TlsDHUtils.validateDHPublicKey(key);
    }
}