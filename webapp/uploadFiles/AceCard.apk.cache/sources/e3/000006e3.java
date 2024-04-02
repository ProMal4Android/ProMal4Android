package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.params.DSAKeyParameters;
import org.spongycastle.crypto.params.DSAParameters;
import org.spongycastle.crypto.params.DSAPrivateKeyParameters;
import org.spongycastle.crypto.params.DSAPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;

/* loaded from: classes.dex */
public class DSASigner implements DSA {
    DSAKeyParameters key;
    SecureRandom random;

    @Override // org.spongycastle.crypto.DSA
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.random = rParam.getRandom();
                this.key = (DSAPrivateKeyParameters) rParam.getParameters();
                return;
            }
            this.random = new SecureRandom();
            this.key = (DSAPrivateKeyParameters) param;
            return;
        }
        this.key = (DSAPublicKeyParameters) param;
    }

    @Override // org.spongycastle.crypto.DSA
    public BigInteger[] generateSignature(byte[] message) {
        BigInteger k;
        DSAParameters params = this.key.getParameters();
        BigInteger m = calculateE(params.getQ(), message);
        int qBitLength = params.getQ().bitLength();
        do {
            k = new BigInteger(qBitLength, this.random);
        } while (k.compareTo(params.getQ()) >= 0);
        BigInteger r = params.getG().modPow(k, params.getP()).mod(params.getQ());
        BigInteger s = k.modInverse(params.getQ()).multiply(m.add(((DSAPrivateKeyParameters) this.key).getX().multiply(r))).mod(params.getQ());
        BigInteger[] res = {r, s};
        return res;
    }

    @Override // org.spongycastle.crypto.DSA
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        DSAParameters params = this.key.getParameters();
        BigInteger m = calculateE(params.getQ(), message);
        BigInteger zero = BigInteger.valueOf(0L);
        if (zero.compareTo(r) >= 0 || params.getQ().compareTo(r) <= 0 || zero.compareTo(s) >= 0 || params.getQ().compareTo(s) <= 0) {
            return false;
        }
        BigInteger w = s.modInverse(params.getQ());
        BigInteger u1 = m.multiply(w).mod(params.getQ());
        BigInteger u2 = r.multiply(w).mod(params.getQ());
        BigInteger v = params.getG().modPow(u1, params.getP()).multiply(((DSAPublicKeyParameters) this.key).getY().modPow(u2, params.getP())).mod(params.getP()).mod(params.getQ());
        return v.equals(r);
    }

    private BigInteger calculateE(BigInteger n, byte[] message) {
        if (n.bitLength() >= message.length * 8) {
            return new BigInteger(1, message);
        }
        byte[] trunc = new byte[n.bitLength() / 8];
        System.arraycopy(message, 0, trunc, 0, trunc.length);
        return new BigInteger(1, trunc);
    }
}