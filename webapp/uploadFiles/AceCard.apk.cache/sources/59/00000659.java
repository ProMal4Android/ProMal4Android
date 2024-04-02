package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.GOST3410KeyGenerationParameters;
import org.spongycastle.crypto.params.GOST3410Parameters;
import org.spongycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.spongycastle.crypto.params.GOST3410PublicKeyParameters;

/* loaded from: classes.dex */
public class GOST3410KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private GOST3410KeyGenerationParameters param;

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public void init(KeyGenerationParameters param) {
        this.param = (GOST3410KeyGenerationParameters) param;
    }

    @Override // org.spongycastle.crypto.AsymmetricCipherKeyPairGenerator
    public AsymmetricCipherKeyPair generateKeyPair() {
        GOST3410Parameters GOST3410Params = this.param.getParameters();
        SecureRandom random = this.param.getRandom();
        BigInteger q = GOST3410Params.getQ();
        BigInteger p = GOST3410Params.getP();
        BigInteger a = GOST3410Params.getA();
        while (true) {
            BigInteger x = new BigInteger(256, random);
            if (!x.equals(ZERO) && x.compareTo(q) < 0) {
                BigInteger y = a.modPow(x, p);
                return new AsymmetricCipherKeyPair(new GOST3410PublicKeyParameters(y, GOST3410Params), new GOST3410PrivateKeyParameters(x, GOST3410Params));
            }
        }
    }
}