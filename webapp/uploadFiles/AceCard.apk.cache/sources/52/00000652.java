package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.params.DHParameters;
import org.spongycastle.crypto.params.DHValidationParameters;

/* loaded from: classes.dex */
public class DHParametersGenerator {
    private static final BigInteger TWO = BigInteger.valueOf(2);
    private int certainty;
    private SecureRandom random;
    private int size;

    public void init(int size, int certainty, SecureRandom random) {
        this.size = size;
        this.certainty = certainty;
        this.random = random;
    }

    public DHParameters generateParameters() {
        BigInteger[] safePrimes = DHParametersHelper.generateSafePrimes(this.size, this.certainty, this.random);
        BigInteger p = safePrimes[0];
        BigInteger q = safePrimes[1];
        BigInteger g = DHParametersHelper.selectGenerator(p, q, this.random);
        return new DHParameters(p, g, q, TWO, (DHValidationParameters) null);
    }
}