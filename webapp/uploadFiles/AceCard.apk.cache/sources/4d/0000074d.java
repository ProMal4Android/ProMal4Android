package org.spongycastle.math.ntru.euclid;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class BigIntEuclidean {
    public BigInteger gcd;
    public BigInteger x;
    public BigInteger y;

    private BigIntEuclidean() {
    }

    public static BigIntEuclidean calculate(BigInteger a, BigInteger b) {
        BigInteger x = BigInteger.ZERO;
        BigInteger lastx = BigInteger.ONE;
        BigInteger y = BigInteger.ONE;
        BigInteger lasty = BigInteger.ZERO;
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger[] quotientAndRemainder = a.divideAndRemainder(b);
            BigInteger quotient = quotientAndRemainder[0];
            a = b;
            b = quotientAndRemainder[1];
            BigInteger temp = x;
            x = lastx.subtract(quotient.multiply(x));
            lastx = temp;
            BigInteger temp2 = y;
            y = lasty.subtract(quotient.multiply(y));
            lasty = temp2;
        }
        BigIntEuclidean result = new BigIntEuclidean();
        result.x = lastx;
        result.y = lasty;
        result.gcd = a;
        return result;
    }
}