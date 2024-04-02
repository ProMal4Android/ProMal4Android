package org.spongycastle.crypto.signers;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.params.ECKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
public class ECDSASigner implements ECConstants, DSA {
    ECKeyParameters key;
    SecureRandom random;

    @Override // org.spongycastle.crypto.DSA
    public void init(boolean forSigning, CipherParameters param) {
        if (forSigning) {
            if (param instanceof ParametersWithRandom) {
                ParametersWithRandom rParam = (ParametersWithRandom) param;
                this.random = rParam.getRandom();
                this.key = (ECPrivateKeyParameters) rParam.getParameters();
                return;
            }
            this.random = new SecureRandom();
            this.key = (ECPrivateKeyParameters) param;
            return;
        }
        this.key = (ECPublicKeyParameters) param;
    }

    @Override // org.spongycastle.crypto.DSA
    public BigInteger[] generateSignature(byte[] message) {
        BigInteger r;
        BigInteger s;
        BigInteger n = this.key.getParameters().getN();
        BigInteger e = calculateE(n, message);
        do {
            int nBitLength = n.bitLength();
            while (true) {
                BigInteger k = new BigInteger(nBitLength, this.random);
                if (!k.equals(ZERO) && k.compareTo(n) < 0) {
                    ECPoint p = this.key.getParameters().getG().multiply(k);
                    BigInteger x = p.getX().toBigInteger();
                    r = x.mod(n);
                    if (!r.equals(ZERO)) {
                        BigInteger d = ((ECPrivateKeyParameters) this.key).getD();
                        s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n);
                    }
                }
            }
        } while (s.equals(ZERO));
        BigInteger[] res = {r, s};
        return res;
    }

    @Override // org.spongycastle.crypto.DSA
    public boolean verifySignature(byte[] message, BigInteger r, BigInteger s) {
        BigInteger n = this.key.getParameters().getN();
        BigInteger e = calculateE(n, message);
        if (r.compareTo(ONE) < 0 || r.compareTo(n) >= 0 || s.compareTo(ONE) < 0 || s.compareTo(n) >= 0) {
            return false;
        }
        BigInteger c = s.modInverse(n);
        BigInteger u1 = e.multiply(c).mod(n);
        BigInteger u2 = r.multiply(c).mod(n);
        ECPoint G = this.key.getParameters().getG();
        ECPoint Q = ((ECPublicKeyParameters) this.key).getQ();
        ECPoint point = ECAlgorithms.sumOfTwoMultiplies(G, u1, Q, u2);
        BigInteger v = point.getX().toBigInteger().mod(n);
        return v.equals(r);
    }

    private BigInteger calculateE(BigInteger n, byte[] message) {
        int log2n = n.bitLength();
        int messageBitLength = message.length * 8;
        if (log2n >= messageBitLength) {
            return new BigInteger(1, message);
        }
        BigInteger trunc = new BigInteger(1, message);
        return trunc.shiftRight(messageBitLength - log2n);
    }
}