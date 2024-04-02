package org.spongycastle.crypto.tls;

import java.math.BigInteger;
import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.agreement.DHBasicAgreement;
import org.spongycastle.crypto.agreement.ECDHBasicAgreement;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.DHPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.util.BigIntegers;

/* loaded from: classes.dex */
public class DefaultTlsAgreementCredentials implements TlsAgreementCredentials {
    protected BasicAgreement basicAgreement;
    protected Certificate clientCert;
    protected AsymmetricKeyParameter clientPrivateKey;

    public DefaultTlsAgreementCredentials(Certificate clientCertificate, AsymmetricKeyParameter clientPrivateKey) {
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
        if (clientPrivateKey instanceof DHPrivateKeyParameters) {
            this.basicAgreement = new DHBasicAgreement();
        } else if (clientPrivateKey instanceof ECPrivateKeyParameters) {
            this.basicAgreement = new ECDHBasicAgreement();
        } else {
            throw new IllegalArgumentException("'clientPrivateKey' type not supported: " + clientPrivateKey.getClass().getName());
        }
        this.clientCert = clientCertificate;
        this.clientPrivateKey = clientPrivateKey;
    }

    @Override // org.spongycastle.crypto.tls.TlsCredentials
    public Certificate getCertificate() {
        return this.clientCert;
    }

    @Override // org.spongycastle.crypto.tls.TlsAgreementCredentials
    public byte[] generateAgreement(AsymmetricKeyParameter serverPublicKey) {
        this.basicAgreement.init(this.clientPrivateKey);
        BigInteger agreementValue = this.basicAgreement.calculateAgreement(serverPublicKey);
        return BigIntegers.asUnsignedByteArray(agreementValue);
    }
}