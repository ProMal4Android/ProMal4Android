package org.spongycastle.crypto.tls;

import java.io.IOException;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DSAPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;

/* loaded from: classes.dex */
public class DefaultTlsSignerCredentials implements TlsSignerCredentials {
    protected Certificate clientCert;
    protected AsymmetricKeyParameter clientPrivateKey;
    protected TlsSigner clientSigner;
    protected TlsClientContext context;

    public DefaultTlsSignerCredentials(TlsClientContext context, Certificate clientCertificate, AsymmetricKeyParameter clientPrivateKey) {
        if (clientCertificate == null) {
            throw new IllegalArgumentException("'clientCertificate' cannot be null");
        }
        if (clientCertificate.certs.length == 0) {
            throw new IllegalArgumentException("'clientCertificate' cannot be empty");
        }
        if (clientPrivateKey == null) {
            throw new IllegalArgumentException("'clientPrivateKey' cannot be null");
        }
        if (!clientPrivateKey.isPrivate()) {
            throw new IllegalArgumentException("'clientPrivateKey' must be private");
        }
        if (clientPrivateKey instanceof RSAKeyParameters) {
            this.clientSigner = new TlsRSASigner();
        } else if (clientPrivateKey instanceof DSAPrivateKeyParameters) {
            this.clientSigner = new TlsDSSSigner();
        } else if (clientPrivateKey instanceof ECPrivateKeyParameters) {
            this.clientSigner = new TlsECDSASigner();
        } else {
            throw new IllegalArgumentException("'clientPrivateKey' type not supported: " + clientPrivateKey.getClass().getName());
        }
        this.context = context;
        this.clientCert = clientCertificate;
        this.clientPrivateKey = clientPrivateKey;
    }

    @Override // org.spongycastle.crypto.tls.TlsCredentials
    public Certificate getCertificate() {
        return this.clientCert;
    }

    @Override // org.spongycastle.crypto.tls.TlsSignerCredentials
    public byte[] generateCertificateSignature(byte[] md5andsha1) throws IOException {
        try {
            return this.clientSigner.calculateRawSignature(this.context.getSecureRandom(), this.clientPrivateKey, md5andsha1);
        } catch (CryptoException e) {
            throw new TlsFatalAlert((short) 80);
        }
    }
}