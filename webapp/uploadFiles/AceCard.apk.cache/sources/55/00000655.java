package org.spongycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.DSAParameters;
import org.spongycastle.crypto.params.DSAValidationParameters;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

/* loaded from: classes.dex */
public class DSAParametersGenerator {
    private int L;
    private int N;
    private int certainty;
    private SecureRandom random;
    private static final BigInteger ZERO = BigInteger.valueOf(0);
    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    public void init(int size, int certainty, SecureRandom random) {
        init(size, getDefaultN(size), certainty, random);
    }

    private void init(int L, int N, int certainty, SecureRandom random) {
        this.L = L;
        this.N = N;
        this.certainty = certainty;
        this.random = random;
    }

    public DSAParameters generateParameters() {
        return this.L > 1024 ? generateParameters_FIPS186_3() : generateParameters_FIPS186_2();
    }

    private DSAParameters generateParameters_FIPS186_2() {
        byte[] seed = new byte[20];
        byte[] part1 = new byte[20];
        byte[] part2 = new byte[20];
        byte[] u = new byte[20];
        SHA1Digest sha1 = new SHA1Digest();
        int n = (this.L - 1) / 160;
        byte[] w = new byte[this.L / 8];
        while (true) {
            this.random.nextBytes(seed);
            hash(sha1, seed, part1);
            System.arraycopy(seed, 0, part2, 0, seed.length);
            inc(part2);
            hash(sha1, part2, part2);
            for (int i = 0; i != u.length; i++) {
                u[i] = (byte) (part1[i] ^ part2[i]);
            }
            u[0] = (byte) (u[0] | Byte.MIN_VALUE);
            u[19] = (byte) (u[19] | 1);
            BigInteger q = new BigInteger(1, u);
            if (q.isProbablePrime(this.certainty)) {
                byte[] offset = Arrays.clone(seed);
                inc(offset);
                for (int counter = 0; counter < 4096; counter++) {
                    for (int k = 0; k < n; k++) {
                        inc(offset);
                        hash(sha1, offset, part1);
                        System.arraycopy(part1, 0, w, w.length - ((k + 1) * part1.length), part1.length);
                    }
                    inc(offset);
                    hash(sha1, offset, part1);
                    System.arraycopy(part1, part1.length - (w.length - (part1.length * n)), w, 0, w.length - (part1.length * n));
                    w[0] = (byte) (w[0] | Byte.MIN_VALUE);
                    BigInteger x = new BigInteger(1, w);
                    BigInteger c = x.mod(q.shiftLeft(1));
                    BigInteger p = x.subtract(c.subtract(ONE));
                    if (p.bitLength() == this.L && p.isProbablePrime(this.certainty)) {
                        BigInteger g = calculateGenerator_FIPS186_2(p, q, this.random);
                        return new DSAParameters(p, q, g, new DSAValidationParameters(seed, counter));
                    }
                }
                continue;
            }
        }
    }

    private static BigInteger calculateGenerator_FIPS186_2(BigInteger p, BigInteger q, SecureRandom r) {
        BigInteger g;
        BigInteger e = p.subtract(ONE).divide(q);
        BigInteger pSub2 = p.subtract(TWO);
        do {
            BigInteger h = BigIntegers.createRandomInRange(TWO, pSub2, r);
            g = h.modPow(e, p);
        } while (g.bitLength() <= 1);
        return g;
    }

    private DSAParameters generateParameters_FIPS186_3() {
        Digest d = new SHA256Digest();
        int outlen = d.getDigestSize() * 8;
        int seedlen = this.N;
        byte[] seed = new byte[seedlen / 8];
        int n = (this.L - 1) / outlen;
        int b = (this.L - 1) % outlen;
        byte[] output = new byte[d.getDigestSize()];
        while (true) {
            this.random.nextBytes(seed);
            hash(d, seed, output);
            BigInteger U = new BigInteger(1, output).mod(ONE.shiftLeft(this.N - 1));
            BigInteger q = ONE.shiftLeft(this.N - 1).add(U).add(ONE).subtract(U.mod(TWO));
            if (q.isProbablePrime(this.certainty)) {
                byte[] offset = Arrays.clone(seed);
                int counterLimit = this.L * 4;
                for (int counter = 0; counter < counterLimit; counter++) {
                    BigInteger W = ZERO;
                    int j = 0;
                    int exp = 0;
                    while (j <= n) {
                        inc(offset);
                        hash(d, offset, output);
                        BigInteger Vj = new BigInteger(1, output);
                        if (j == n) {
                            Vj = Vj.mod(ONE.shiftLeft(b));
                        }
                        W = W.add(Vj.shiftLeft(exp));
                        j++;
                        exp += outlen;
                    }
                    BigInteger X = W.add(ONE.shiftLeft(this.L - 1));
                    BigInteger c = X.mod(q.shiftLeft(1));
                    BigInteger p = X.subtract(c.subtract(ONE));
                    if (p.bitLength() == this.L && p.isProbablePrime(this.certainty)) {
                        BigInteger g = calculateGenerator_FIPS186_3_Unverifiable(p, q, this.random);
                        return new DSAParameters(p, q, g, new DSAValidationParameters(seed, counter));
                    }
                }
                continue;
            }
        }
    }

    private static BigInteger calculateGenerator_FIPS186_3_Unverifiable(BigInteger p, BigInteger q, SecureRandom r) {
        return calculateGenerator_FIPS186_2(p, q, r);
    }

    private static void hash(Digest d, byte[] input, byte[] output) {
        d.update(input, 0, input.length);
        d.doFinal(output, 0);
    }

    private static int getDefaultN(int L) {
        return L > 1024 ? 256 : 160;
    }

    private static void inc(byte[] buf) {
        for (int i = buf.length - 1; i >= 0; i--) {
            byte b = (byte) ((buf[i] + 1) & 255);
            buf[i] = b;
            if (b != 0) {
                return;
            }
        }
    }
}