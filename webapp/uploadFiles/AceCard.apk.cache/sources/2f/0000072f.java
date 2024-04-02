package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.X509CertificateStructure;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.agreement.srp.SRP6Client;
import org.spongycastle.crypto.agreement.srp.SRP6Util;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.io.SignerInputStream;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.util.PublicKeyFactory;
import org.spongycastle.util.BigIntegers;

/* loaded from: classes.dex */
class TlsSRPKeyExchange implements TlsKeyExchange {
    protected TlsClientContext context;
    protected byte[] identity;
    protected int keyExchange;
    protected byte[] password;
    protected TlsSigner tlsSigner;
    protected AsymmetricKeyParameter serverPublicKey = null;
    protected byte[] s = null;
    protected BigInteger B = null;
    protected SRP6Client srpClient = new SRP6Client();

    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsSRPKeyExchange(TlsClientContext context, int keyExchange, byte[] identity, byte[] password) {
        switch (keyExchange) {
            case 21:
                this.tlsSigner = null;
                break;
            case 22:
                this.tlsSigner = new TlsDSSSigner();
                break;
            case 23:
                this.tlsSigner = new TlsRSASigner();
                break;
            default:
                throw new IllegalArgumentException("unsupported key exchange algorithm");
        }
        this.context = context;
        this.keyExchange = keyExchange;
        this.identity = identity;
        this.password = password;
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerCertificate() throws IOException {
        if (this.tlsSigner != null) {
            throw new TlsFatalAlert((short) 10);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCertificate(Certificate serverCertificate) throws IOException {
        if (this.tlsSigner == null) {
            throw new TlsFatalAlert((short) 10);
        }
        X509CertificateStructure x509Cert = serverCertificate.certs[0];
        SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
        try {
            this.serverPublicKey = PublicKeyFactory.createKey(keyInfo);
            if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey)) {
                throw new TlsFatalAlert((short) 46);
            }
            TlsUtils.validateKeyUsage(x509Cert, 128);
        } catch (RuntimeException e) {
            throw new TlsFatalAlert((short) 43);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerKeyExchange() throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream is) throws IOException {
        SecurityParameters securityParameters = this.context.getSecurityParameters();
        InputStream sigIn = is;
        Signer signer = null;
        if (this.tlsSigner != null) {
            signer = initSigner(this.tlsSigner, securityParameters);
            sigIn = new SignerInputStream(is, signer);
        }
        byte[] NBytes = TlsUtils.readOpaque16(sigIn);
        byte[] gBytes = TlsUtils.readOpaque16(sigIn);
        byte[] sBytes = TlsUtils.readOpaque8(sigIn);
        byte[] BBytes = TlsUtils.readOpaque16(sigIn);
        if (signer != null) {
            byte[] sigByte = TlsUtils.readOpaque16(is);
            if (!signer.verifySignature(sigByte)) {
                throw new TlsFatalAlert((short) 42);
            }
        }
        BigInteger N = new BigInteger(1, NBytes);
        BigInteger g = new BigInteger(1, gBytes);
        this.s = sBytes;
        try {
            this.B = SRP6Util.validatePublicValue(N, new BigInteger(1, BBytes));
            this.srpClient.init(N, g, new SHA1Digest(), this.context.getSecureRandom());
        } catch (CryptoException e) {
            throw new TlsFatalAlert((short) 47);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void validateCertificateRequest(CertificateRequest certificateRequest) throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipClientCredentials() throws IOException {
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCredentials(TlsCredentials clientCredentials) throws IOException {
        throw new TlsFatalAlert((short) 80);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void generateClientKeyExchange(OutputStream os) throws IOException {
        byte[] keData = BigIntegers.asUnsignedByteArray(this.srpClient.generateClientCredentials(this.s, this.identity, this.password));
        TlsUtils.writeOpaque16(keData, os);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generatePremasterSecret() throws IOException {
        try {
            return BigIntegers.asUnsignedByteArray(this.srpClient.calculateSecret(this.B));
        } catch (CryptoException e) {
            throw new TlsFatalAlert((short) 47);
        }
    }

    protected Signer initSigner(TlsSigner tlsSigner, SecurityParameters securityParameters) {
        Signer signer = tlsSigner.createVerifyer(this.serverPublicKey);
        signer.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
        signer.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
        return signer;
    }
}