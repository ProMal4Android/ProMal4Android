package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.spongycastle.crypto.Signer;
import org.spongycastle.crypto.io.SignerInputStream;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHPublicKeyParameters;

/* loaded from: classes.dex */
class TlsDHEKeyExchange extends TlsDHKeyExchange {
    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsDHEKeyExchange(TlsClientContext context, int keyExchange) {
        super(context, keyExchange);
    }

    @Override // org.spongycastle.crypto.tls.TlsDHKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void skipServerKeyExchange() throws IOException {
        throw new TlsFatalAlert((short) 10);
    }

    @Override // org.spongycastle.crypto.tls.TlsDHKeyExchange, org.spongycastle.crypto.tls.TlsKeyExchange
    public void processServerKeyExchange(InputStream is) throws IOException {
        SecurityParameters securityParameters = this.context.getSecurityParameters();
        Signer signer = initSigner(this.tlsSigner, securityParameters);
        InputStream sigIn = new SignerInputStream(is, signer);
        byte[] pBytes = TlsUtils.readOpaque16(sigIn);
        byte[] gBytes = TlsUtils.readOpaque16(sigIn);
        byte[] YsBytes = TlsUtils.readOpaque16(sigIn);
        byte[] sigByte = TlsUtils.readOpaque16(is);
        if (!signer.verifySignature(sigByte)) {
            throw new TlsFatalAlert((short) 42);
        }
        BigInteger p = new BigInteger(1, pBytes);
        BigInteger g = new BigInteger(1, gBytes);
        BigInteger Ys = new BigInteger(1, YsBytes);
        this.dhAgreeServerPublicKey = validateDHPublicKey(new DHPublicKeyParameters(Ys, new DHParameters(p, g)));
    }

    protected Signer initSigner(TlsSigner tlsSigner, SecurityParameters securityParameters) {
        Signer signer = tlsSigner.createVerifyer(this.serverPublicKey);
        signer.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
        signer.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
        return signer;
    }
}