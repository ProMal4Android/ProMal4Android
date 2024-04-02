package org.spongycastle.math.ec;

import java.math.BigInteger;
import java.util.Random;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
public abstract class ECCurve {
    ECFieldElement a;
    ECFieldElement b;

    public abstract ECPoint createPoint(BigInteger bigInteger, BigInteger bigInteger2, boolean z);

    public abstract ECPoint decodePoint(byte[] bArr);

    public abstract ECFieldElement fromBigInteger(BigInteger bigInteger);

    public abstract int getFieldSize();

    public abstract ECPoint getInfinity();

    public ECFieldElement getA() {
        return this.a;
    }

    public ECFieldElement getB() {
        return this.b;
    }

    /* loaded from: classes.dex */
    public static class Fp extends ECCurve {
        ECPoint.Fp infinity;
        BigInteger q;

        public Fp(BigInteger q, BigInteger a, BigInteger b) {
            this.q = q;
            this.a = fromBigInteger(a);
            this.b = fromBigInteger(b);
            this.infinity = new ECPoint.Fp(this, null, null);
        }

        public BigInteger getQ() {
            return this.q;
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public int getFieldSize() {
            return this.q.bitLength();
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECFieldElement fromBigInteger(BigInteger x) {
            return new ECFieldElement.Fp(this.q, x);
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECPoint createPoint(BigInteger x, BigInteger y, boolean withCompression) {
            return new ECPoint.Fp(this, fromBigInteger(x), fromBigInteger(y), withCompression);
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECPoint decodePoint(byte[] encoded) {
            switch (encoded[0]) {
                case 0:
                    if (encoded.length > 1) {
                        throw new RuntimeException("Invalid point encoding");
                    }
                    ECPoint p = getInfinity();
                    return p;
                case 1:
                case 5:
                default:
                    throw new RuntimeException("Invalid point encoding 0x" + Integer.toString(encoded[0], 16));
                case 2:
                case 3:
                    int ytilde = encoded[0] & 1;
                    byte[] i = new byte[encoded.length - 1];
                    System.arraycopy(encoded, 1, i, 0, i.length);
                    ECFieldElement x = new ECFieldElement.Fp(this.q, new BigInteger(1, i));
                    ECFieldElement alpha = x.multiply(x.square().add(this.a)).add(this.b);
                    ECFieldElement beta = alpha.sqrt();
                    if (beta == null) {
                        throw new RuntimeException("Invalid point compression");
                    }
                    int bit0 = beta.toBigInteger().testBit(0) ? 1 : 0;
                    if (bit0 == ytilde) {
                        ECPoint p2 = new ECPoint.Fp(this, x, beta, true);
                        return p2;
                    }
                    ECPoint p3 = new ECPoint.Fp(this, x, new ECFieldElement.Fp(this.q, this.q.subtract(beta.toBigInteger())), true);
                    return p3;
                case 4:
                case 6:
                case 7:
                    byte[] xEnc = new byte[(encoded.length - 1) / 2];
                    byte[] yEnc = new byte[(encoded.length - 1) / 2];
                    System.arraycopy(encoded, 1, xEnc, 0, xEnc.length);
                    System.arraycopy(encoded, xEnc.length + 1, yEnc, 0, yEnc.length);
                    ECPoint p4 = new ECPoint.Fp(this, new ECFieldElement.Fp(this.q, new BigInteger(1, xEnc)), new ECFieldElement.Fp(this.q, new BigInteger(1, yEnc)));
                    return p4;
            }
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECPoint getInfinity() {
            return this.infinity;
        }

        public boolean equals(Object anObject) {
            if (anObject == this) {
                return true;
            }
            if (anObject instanceof Fp) {
                Fp other = (Fp) anObject;
                return this.q.equals(other.q) && this.a.equals(other.a) && this.b.equals(other.b);
            }
            return false;
        }

        public int hashCode() {
            return (this.a.hashCode() ^ this.b.hashCode()) ^ this.q.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public static class F2m extends ECCurve {
        private BigInteger h;
        private ECPoint.F2m infinity;
        private int k1;
        private int k2;
        private int k3;
        private int m;
        private byte mu;
        private BigInteger n;
        private BigInteger[] si;

        public F2m(int m, int k, BigInteger a, BigInteger b) {
            this(m, k, 0, 0, a, b, null, null);
        }

        public F2m(int m, int k, BigInteger a, BigInteger b, BigInteger n, BigInteger h) {
            this(m, k, 0, 0, a, b, n, h);
        }

        public F2m(int m, int k1, int k2, int k3, BigInteger a, BigInteger b) {
            this(m, k1, k2, k3, a, b, null, null);
        }

        public F2m(int m, int k1, int k2, int k3, BigInteger a, BigInteger b, BigInteger n, BigInteger h) {
            this.mu = (byte) 0;
            this.si = null;
            this.m = m;
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            this.n = n;
            this.h = h;
            if (k1 == 0) {
                throw new IllegalArgumentException("k1 must be > 0");
            }
            if (k2 == 0) {
                if (k3 != 0) {
                    throw new IllegalArgumentException("k3 must be 0 if k2 == 0");
                }
            } else if (k2 <= k1) {
                throw new IllegalArgumentException("k2 must be > k1");
            } else {
                if (k3 <= k2) {
                    throw new IllegalArgumentException("k3 must be > k2");
                }
            }
            this.a = fromBigInteger(a);
            this.b = fromBigInteger(b);
            this.infinity = new ECPoint.F2m(this, null, null);
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public int getFieldSize() {
            return this.m;
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECFieldElement fromBigInteger(BigInteger x) {
            return new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, x);
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECPoint createPoint(BigInteger x, BigInteger y, boolean withCompression) {
            return new ECPoint.F2m(this, fromBigInteger(x), fromBigInteger(y), withCompression);
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECPoint decodePoint(byte[] encoded) {
            switch (encoded[0]) {
                case 0:
                    if (encoded.length > 1) {
                        throw new RuntimeException("Invalid point encoding");
                    }
                    ECPoint p = getInfinity();
                    return p;
                case 1:
                case 5:
                default:
                    throw new RuntimeException("Invalid point encoding 0x" + Integer.toString(encoded[0], 16));
                case 2:
                case 3:
                    byte[] enc = new byte[encoded.length - 1];
                    System.arraycopy(encoded, 1, enc, 0, enc.length);
                    if (encoded[0] == 2) {
                        ECPoint p2 = decompressPoint(enc, 0);
                        return p2;
                    }
                    ECPoint p3 = decompressPoint(enc, 1);
                    return p3;
                case 4:
                case 6:
                case 7:
                    byte[] xEnc = new byte[(encoded.length - 1) / 2];
                    byte[] yEnc = new byte[(encoded.length - 1) / 2];
                    System.arraycopy(encoded, 1, xEnc, 0, xEnc.length);
                    System.arraycopy(encoded, xEnc.length + 1, yEnc, 0, yEnc.length);
                    ECPoint p4 = new ECPoint.F2m(this, new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(1, xEnc)), new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(1, yEnc)), false);
                    return p4;
            }
        }

        @Override // org.spongycastle.math.ec.ECCurve
        public ECPoint getInfinity() {
            return this.infinity;
        }

        public boolean isKoblitz() {
            return (this.n == null || this.h == null || (!this.a.toBigInteger().equals(ECConstants.ZERO) && !this.a.toBigInteger().equals(ECConstants.ONE)) || !this.b.toBigInteger().equals(ECConstants.ONE)) ? false : true;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public synchronized byte getMu() {
            if (this.mu == 0) {
                this.mu = Tnaf.getMu(this);
            }
            return this.mu;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public synchronized BigInteger[] getSi() {
            if (this.si == null) {
                this.si = Tnaf.getSi(this);
            }
            return this.si;
        }

        private ECPoint decompressPoint(byte[] xEnc, int ypBit) {
            ECFieldElement yp;
            ECFieldElement xp = new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(1, xEnc));
            if (xp.toBigInteger().equals(ECConstants.ZERO)) {
                ECFieldElement yp2 = this.b;
                yp = (ECFieldElement.F2m) yp2;
                for (int i = 0; i < this.m - 1; i++) {
                    yp = yp.square();
                }
            } else {
                ECFieldElement beta = xp.add(this.a).add(this.b.multiply(xp.square().invert()));
                ECFieldElement z = solveQuadradicEquation(beta);
                if (z == null) {
                    throw new RuntimeException("Invalid point compression");
                }
                int zBit = 0;
                if (z.toBigInteger().testBit(0)) {
                    zBit = 1;
                }
                if (zBit != ypBit) {
                    z = z.add(new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, ECConstants.ONE));
                }
                yp = xp.multiply(z);
            }
            return new ECPoint.F2m(this, xp, yp);
        }

        private ECFieldElement solveQuadradicEquation(ECFieldElement beta) {
            ECFieldElement z;
            ECFieldElement gamma;
            ECFieldElement zeroElement = new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, ECConstants.ZERO);
            if (!beta.toBigInteger().equals(ECConstants.ZERO)) {
                Random rand = new Random();
                do {
                    ECFieldElement t = new ECFieldElement.F2m(this.m, this.k1, this.k2, this.k3, new BigInteger(this.m, rand));
                    z = zeroElement;
                    ECFieldElement w = beta;
                    for (int i = 1; i <= this.m - 1; i++) {
                        ECFieldElement w2 = w.square();
                        z = z.square().add(w2.multiply(t));
                        w = w2.add(beta);
                    }
                    if (!w.toBigInteger().equals(ECConstants.ZERO)) {
                        return null;
                    }
                    gamma = z.square().add(z);
                } while (gamma.toBigInteger().equals(ECConstants.ZERO));
                return z;
            }
            return zeroElement;
        }

        public boolean equals(Object anObject) {
            if (anObject == this) {
                return true;
            }
            if (anObject instanceof F2m) {
                F2m other = (F2m) anObject;
                return this.m == other.m && this.k1 == other.k1 && this.k2 == other.k2 && this.k3 == other.k3 && this.a.equals(other.a) && this.b.equals(other.b);
            }
            return false;
        }

        public int hashCode() {
            return ((((this.a.hashCode() ^ this.b.hashCode()) ^ this.m) ^ this.k1) ^ this.k2) ^ this.k3;
        }

        public int getM() {
            return this.m;
        }

        public boolean isTrinomial() {
            return this.k2 == 0 && this.k3 == 0;
        }

        public int getK1() {
            return this.k1;
        }

        public int getK2() {
            return this.k2;
        }

        public int getK3() {
            return this.k3;
        }

        public BigInteger getN() {
            return this.n;
        }

        public BigInteger getH() {
            return this.h;
        }
    }
}