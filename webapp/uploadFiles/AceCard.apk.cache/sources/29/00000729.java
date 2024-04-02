package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPrivateKeyParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;
import org.spongycastle.crypto.params.RSAKeyParameters;

/* loaded from: classes.dex */
class TlsPSKKeyExchange implements TlsKeyExchange {
    protected TlsClientContext context;
    protected int keyExchange;
    protected byte[] premasterSecret;
    protected TlsPSKIdentity pskIdentity;
    protected byte[] psk_identity_hint = null;
    protected DHPublicKeyParameters dhAgreeServerPublicKey = null;
    protected DHPrivateKeyParameters dhAgreeClientPrivateKey = null;
    protected RSAKeyParameters rsaServerPublicKey = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsPSKKeyExchange(TlsClientContext context, int keyExchange, TlsPSKIdentity pskIdentity) {
        switch (keyExchange) {
            case 13:
            case 14:
            case 15:
                this.context = context;
                this.keyExchange = keyExchange;
                this.pskIdentity = pskIdentity;
                return;
            default:
                throw new IllegalArgumentException("unsupported key exchange algorithm");
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerCertificate() throws IOException {
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerCertificate(Certificate serverCertificate) throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerKeyExchange() throws IOException {
        this.psk_identity_hint = new byte[0];
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream is) throws IOException {
        this.psk_identity_hint = TlsUtils.readOpaque16(is);
        if (this.keyExchange == 14) {
            byte[] pBytes = TlsUtils.readOpaque16(is);
            byte[] gBytes = TlsUtils.readOpaque16(is);
            byte[] YsBytes = TlsUtils.readOpaque16(is);
            BigInteger p = new BigInteger(1, pBytes);
            BigInteger g = new BigInteger(1, gBytes);
            BigInteger Ys = new BigInteger(1, YsBytes);
            this.dhAgreeServerPublicKey = TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(Ys, new DHParameters(p, g)));
            return;
        }
        if (this.psk_identity_hint.length == 0) {
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
        if (this.psk_identity_hint == null || this.psk_identity_hint.length == 0) {
            this.pskIdentity.skipIdentityHint();
        } else {
            this.pskIdentity.notifyIdentityHint(this.psk_identity_hint);
        }
        byte[] psk_identity = this.pskIdentity.getPSKIdentity();
        TlsUtils.writeOpaque16(psk_identity, os);
        if (this.keyExchange == 15) {
            this.premasterSecret = TlsRSAUtils.generateEncryptedPreMasterSecret(this.context, this.rsaServerPublicKey, os);
        } else if (this.keyExchange == 14) {
            this.dhAgreeClientPrivateKey = TlsDHUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.dhAgreeServerPublicKey.getParameters(), os);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsKeyExchange
    public byte[] generatePremasterSecret() throws IOException {
        byte[] psk = this.pskIdentity.getPSK();
        byte[] other_secret = generateOtherSecret(psk.length);
        ByteArrayOutputStream buf = new ByteArrayOutputStream(other_secret.length + 4 + psk.length);
        TlsUtils.writeOpaque16(other_secret, buf);
        TlsUtils.writeOpaque16(psk, buf);
        return buf.toByteArray();
    }

    protected byte[] generateOtherSecret(int pskLength) {
        if (this.keyExchange == 14) {
            return TlsDHUtils.calculateDHBasicAgreement(this.dhAgreeServerPublicKey, this.dhAgreeClientPrivateKey);
        }
        if (this.keyExchange == 15) {
            return this.premasterSecret;
        }
        return new byte[pskLength];
    }
}