package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.util.BigIntegers;

/* loaded from: classes.dex */
class DHParametersHelper {
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    DHParametersHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BigInteger[] generateSafePrimes(int size, int certainty, SecureRandom random) {
        BigInteger q;
        BigInteger p;
        int qLength = size - 1;
        while (true) {
            q = new BigInteger(qLength, 2, random);
            p = q.shiftLeft(1).add(ONE);
            if (!p.isProbablePrime(certainty) || (certainty > 2 && !q.isProbablePrime(certainty))) {
            }
        }
        return new BigInteger[]{p, q};
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BigInteger selectGenerator(BigInteger p, BigInteger q, SecureRandom random) {
        BigInteger g;
        BigInteger pMinusTwo = p.subtract(TWO);
        do {
            BigInteger h = BigIntegers.createRandomInRange(TWO, pMinusTwo, random);
            g = h.modPow(TWO, p);
        } while (g.equals(ONE));
        return g;
    }
}