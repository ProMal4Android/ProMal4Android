package org.spongycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.spongycastle.util.BigIntegers;

/* loaded from: classes.dex */
public class RSABlindedEngine implements AsymmetricBlockCipher {
    private static BigInteger ONE = BigInteger.valueOf(1);
    private RSACoreEngine core = new RSACoreEngine();
    private RSAKeyParameters key;
    private SecureRandom random;

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters param) {
        this.core.init(forEncryption, param);
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            this.key = (RSAKeyParameters) rParam.getParameters();
            this.random = rParam.getRandom();
            return;
        }
        this.key = (RSAKeyParameters) param;
        this.random = new SecureRandom();
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int inLen) {
        BigInteger result;
        if (this.key == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        BigInteger input = this.core.convertInput(in, inOff, inLen);
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            RSAPrivateCrtKeyParameters k = (RSAPrivateCrtKeyParameters) this.key;
            BigInteger e = k.getPublicExponent();
            if (e != null) {
                BigInteger m = k.getModulus();
                BigInteger r = BigIntegers.createRandomInRange(ONE, m.subtract(ONE), this.random);
                BigInteger blindedInput = r.modPow(e, m).multiply(input).mod(m);
                BigInteger blindedResult = this.core.processBlock(blindedInput);
                BigInteger rInv = r.modInverse(m);
                result = blindedResult.multiply(rInv).mod(m);
            } else {
                result = this.core.processBlock(input);
            }
        } else {
            result = this.core.processBlock(input);
        }
        return this.core.convertOutput(result);
    }
}