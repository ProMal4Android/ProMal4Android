package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.io.SignerInputStream;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
class TlsECDHEKeyExchange extends TlsECDHKeyExchange {
    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsECDHEKeyExchange(TlsClientContext context, int keyExchange) {
        super(context, keyExchange);
    }

    @Override // org.spongycastle.crypto.tls.TlsECDHKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerKeyExchange() throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsECDHKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream is) throws IOException {
        SecurityParameters securityParameters = this.context.getSecurityParameters();
        Signer signer = initSigner(this.tlsSigner, securityParameters);
        InputStream sigIn = new SignerInputStream(is, signer);
        short curveType = TlsUtils.readUint8(sigIn);
        if (curveType == 3) {
            int namedCurve = TlsUtils.readUint16(sigIn);
            ECDomainParameters curve_params = NamedCurve.getECParameters(namedCurve);
            byte[] publicBytes = TlsUtils.readOpaque8(sigIn);
            byte[] sigByte = TlsUtils.readOpaque16(is);
            if (!signer.verifySignature(sigByte)) {
                throw new TlsFatalAlert((short) 42);
            }
            ECPoint Q = curve_params.getCurve().decodePoint(publicBytes);
            this.ecAgreeServerPublicKey = validateECPublicKey(new ECPublicKeyParameters(Q, curve_params));
            return;
        }
        throw new TlsFatalAlert((short) 40);
    }

    @Override // org.spongycastle.crypto.tls.TlsECDHKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void validateCertificateRequest(CertificateRequest certificateRequest) throws IOException {
        short[] types = certificateRequest.getCertificateTypes();
        for (short s : types) {
            switch (s) {
                case 1:
                case 2:
                case 64:
                default:
                    throw new TlsFatalAlert((short) 47);
            }
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsECDHKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processClientCredentials(TlsCredentials clientCredentials) throws IOException {
        if (!(clientCredentials instanceof TlsSignerCredentials)) {
            throw new TlsFatalAlert((short) 80);
        }
    }

    protected Signer initSigner(TlsSigner tlsSigner, SecurityParameters securityParameters) {
        Signer signer = tlsSigner.createVerifyer(this.serverPublicKey);
        signer.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
        signer.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
        return signer;
    }
}