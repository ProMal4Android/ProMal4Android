package org.spongycastle.math.ec;

import java.math.BigInteger;
import java.util.Random;

/* loaded from: classes.dex */
public abstract class ECFieldElement implements ECConstants {
    public abstract ECFieldElement add(ECFieldElement eCFieldElement);

    public abstract ECFieldElement divide(ECFieldElement eCFieldElement);

    public abstract String getFieldName();

    public abstract int getFieldSize();

    public abstract ECFieldElement invert();

    public abstract ECFieldElement multiply(ECFieldElement eCFieldElement);

    public abstract ECFieldElement negate();

    public abstract ECFieldElement sqrt();

    public abstract ECFieldElement square();

    public abstract ECFieldElement subtract(ECFieldElement eCFieldElement);

    public abstract BigInteger toBigInteger();

    public String toString() {
        return toBigInteger().toString(2);
    }

    /* loaded from: classes.dex */
    public static class Fp extends ECFieldElement {
        BigInteger q;
        BigInteger x;

        public Fp(BigInteger q, BigInteger x) {
            this.x = x;
            if (x.compareTo(q) >= 0) {
                throw new IllegalArgumentException("x value too large in field element");
            }
            this.q = q;
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public BigInteger toBigInteger() {
            return this.x;
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public String getFieldName() {
            return "Fp";
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public int getFieldSize() {
            return this.q.bitLength();
        }

        public BigInteger getQ() {
            return this.q;
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement add(ECFieldElement b) {
            return new Fp(this.q, this.x.add(b.toBigInteger()).mod(this.q));
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement subtract(ECFieldElement b) {
            return new Fp(this.q, this.x.subtract(b.toBigInteger()).mod(this.q));
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement multiply(ECFieldElement b) {
            return new Fp(this.q, this.x.multiply(b.toBigInteger()).mod(this.q));
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement divide(ECFieldElement b) {
            return new Fp(this.q, this.x.multiply(b.toBigInteger().modInverse(this.q)).mod(this.q));
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement negate() {
            return new Fp(this.q, this.x.negate().mod(this.q));
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement square() {
            return new Fp(this.q, this.x.multiply(this.x).mod(this.q));
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement invert() {
            return new Fp(this.q, this.x.modInverse(this.q));
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement sqrt() {
            if (!this.q.testBit(0)) {
                throw new RuntimeException("not done yet");
            }
            if (this.q.testBit(1)) {
                ECFieldElement z = new Fp(this.q, this.x.modPow(this.q.shiftRight(2).add(ECConstants.ONE), this.q));
                if (!z.square().equals(this)) {
                    return null;
                }
                return z;
            }
            BigInteger qMinusOne = this.q.subtract(ECConstants.ONE);
            BigInteger legendreExponent = qMinusOne.shiftRight(1);
            if (!this.x.modPow(legendreExponent, this.q).equals(ECConstants.ONE)) {
                return null;
            }
            BigInteger u = qMinusOne.shiftRight(2);
            BigInteger k = u.shiftLeft(1).add(ECConstants.ONE);
            BigInteger Q = this.x;
            BigInteger fourQ = Q.shiftLeft(2).mod(this.q);
            Random rand = new Random();
            while (true) {
                BigInteger P = new BigInteger(this.q.bitLength(), rand);
                if (P.compareTo(this.q) < 0 && P.multiply(P).subtract(fourQ).modPow(legendreExponent, this.q).equals(qMinusOne)) {
                    BigInteger[] result = lucasSequence(this.q, P, Q, k);
                    BigInteger U = result[0];
                    BigInteger V = result[1];
                    if (V.multiply(V).mod(this.q).equals(fourQ)) {
                        if (V.testBit(0)) {
                            V = V.add(this.q);
                        }
                        return new Fp(this.q, V.shiftRight(1));
                    } else if (!U.equals(ECConstants.ONE) && !U.equals(qMinusOne)) {
                        return null;
                    }
                }
            }
        }

        private static BigInteger[] lucasSequence(BigInteger p, BigInteger P, BigInteger Q, BigInteger k) {
            int n = k.bitLength();
            int s = k.getLowestSetBit();
            BigInteger Uh = ECConstants.ONE;
            BigInteger Vl = ECConstants.TWO;
            BigInteger Vh = P;
            BigInteger Ql = ECConstants.ONE;
            BigInteger Qh = ECConstants.ONE;
            for (int j = n - 1; j >= s + 1; j--) {
                Ql = Ql.multiply(Qh).mod(p);
                if (k.testBit(j)) {
                    Qh = Ql.multiply(Q).mod(p);
                    Uh = Uh.multiply(Vh).mod(p);
                    Vl = Vh.multiply(Vl).subtract(P.multiply(Ql)).mod(p);
                    Vh = Vh.multiply(Vh).subtract(Qh.shiftLeft(1)).mod(p);
                } else {
                    Qh = Ql;
                    Uh = Uh.multiply(Vl).subtract(Ql).mod(p);
                    Vh = Vh.multiply(Vl).subtract(P.multiply(Ql)).mod(p);
                    Vl = Vl.multiply(Vl).subtract(Ql.shiftLeft(1)).mod(p);
                }
            }
            BigInteger Ql2 = Ql.multiply(Qh).mod(p);
            BigInteger Qh2 = Ql2.multiply(Q).mod(p);
            BigInteger Uh2 = Uh.multiply(Vl).subtract(Ql2).mod(p);
            BigInteger Vl2 = Vh.multiply(Vl).subtract(P.multiply(Ql2)).mod(p);
            BigInteger Ql3 = Ql2.multiply(Qh2).mod(p);
            for (int j2 = 1; j2 <= s; j2++) {
                Uh2 = Uh2.multiply(Vl2).mod(p);
                Vl2 = Vl2.multiply(Vl2).subtract(Ql3.shiftLeft(1)).mod(p);
                Ql3 = Ql3.multiply(Ql3).mod(p);
            }
            return new BigInteger[]{Uh2, Vl2};
        }

        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof Fp) {
                Fp o = (Fp) other;
                return this.q.equals(o.q) && this.x.equals(o.x);
            }
            return false;
        }

        public int hashCode() {
            return this.q.hashCode() ^ this.x.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public static class F2m extends ECFieldElement {
        public static final int GNB = 1;
        public static final int PPB = 3;
        public static final int TPB = 2;
        private int k1;
        private int k2;
        private int k3;
        private int m;
        private int representation;
        private int t;
        private IntArray x;

        public F2m(int m, int k1, int k2, int k3, BigInteger x) {
            this.t = (m + 31) >> 5;
            this.x = new IntArray(x, this.t);
            if (k2 == 0 && k3 == 0) {
                this.representation = 2;
            } else if (k2 >= k3) {
                throw new IllegalArgumentException("k2 must be smaller than k3");
            } else {
                if (k2 <= 0) {
                    throw new IllegalArgumentException("k2 must be larger than 0");
                }
                this.representation = 3;
            }
            if (x.signum() < 0) {
                throw new IllegalArgumentException("x value cannot be negative");
            }
            this.m = m;
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
        }

        public F2m(int m, int k, BigInteger x) {
            this(m, k, 0, 0, x);
        }

        private F2m(int m, int k1, int k2, int k3, IntArray x) {
            this.t = (m + 31) >> 5;
            this.x = x;
            this.m = m;
            this.k1 = k1;
            this.k2 = k2;
            this.k3 = k3;
            if (k2 == 0 && k3 == 0) {
                this.representation = 2;
            } else {
                this.representation = 3;
            }
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public BigInteger toBigInteger() {
            return this.x.toBigInteger();
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public String getFieldName() {
            return "F2m";
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public int getFieldSize() {
            return this.m;
        }

        public static void checkFieldElements(ECFieldElement a, ECFieldElement b) {
            if (!(a instanceof F2m) || !(b instanceof F2m)) {
                throw new IllegalArgumentException("Field elements are not both instances of ECFieldElement.F2m");
            }
            F2m aF2m = (F2m) a;
            F2m bF2m = (F2m) b;
            if (aF2m.m != bF2m.m || aF2m.k1 != bF2m.k1 || aF2m.k2 != bF2m.k2 || aF2m.k3 != bF2m.k3) {
                throw new IllegalArgumentException("Field elements are not elements of the same field F2m");
            }
            if (aF2m.representation != bF2m.representation) {
                throw new IllegalArgumentException("One of the field elements are not elements has incorrect representation");
            }
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement add(ECFieldElement b) {
            IntArray iarrClone = (IntArray) this.x.clone();
            F2m bF2m = (F2m) b;
            iarrClone.addShifted(bF2m.x, 0);
            return new F2m(this.m, this.k1, this.k2, this.k3, iarrClone);
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement subtract(ECFieldElement b) {
            return add(b);
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement multiply(ECFieldElement b) {
            F2m bF2m = (F2m) b;
            IntArray mult = this.x.multiply(bF2m.x, this.m);
            mult.reduce(this.m, new int[]{this.k1, this.k2, this.k3});
            return new F2m(this.m, this.k1, this.k2, this.k3, mult);
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement divide(ECFieldElement b) {
            ECFieldElement bInv = b.invert();
            return multiply(bInv);
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement negate() {
            return this;
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement square() {
            IntArray squared = this.x.square(this.m);
            squared.reduce(this.m, new int[]{this.k1, this.k2, this.k3});
            return new F2m(this.m, this.k1, this.k2, this.k3, squared);
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement invert() {
            IntArray uz = (IntArray) this.x.clone();
            IntArray vz = new IntArray(this.t);
            vz.setBit(this.m);
            vz.setBit(0);
            vz.setBit(this.k1);
            if (this.representation == 3) {
                vz.setBit(this.k2);
                vz.setBit(this.k3);
            }
            IntArray g1z = new IntArray(this.t);
            g1z.setBit(0);
            IntArray g2z = new IntArray(this.t);
            while (!uz.isZero()) {
                int j = uz.bitLength() - vz.bitLength();
                if (j < 0) {
                    IntArray uzCopy = uz;
                    uz = vz;
                    vz = uzCopy;
                    IntArray g1zCopy = g1z;
                    g1z = g2z;
                    g2z = g1zCopy;
                    j = -j;
                }
                int jInt = j >> 5;
                int jBit = j & 31;
                IntArray vzShift = vz.shiftLeft(jBit);
                uz.addShifted(vzShift, jInt);
                IntArray g2zShift = g2z.shiftLeft(jBit);
                g1z.addShifted(g2zShift, jInt);
            }
            return new F2m(this.m, this.k1, this.k2, this.k3, g2z);
        }

        @Override // org.spongycastle.math.ec.ECFieldElement
        public ECFieldElement sqrt() {
            throw new RuntimeException("Not implemented");
        }

        public int getRepresentation() {
            return this.representation;
        }

        public int getM() {
            return this.m;
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

        public boolean equals(Object anObject) {
            if (anObject == this) {
                return true;
            }
            if (anObject instanceof F2m) {
                F2m b = (F2m) anObject;
                return this.m == b.m && this.k1 == b.k1 && this.k2 == b.k2 && this.k3 == b.k3 && this.representation == b.representation && this.x.equals(b.x);
            }
            return false;
        }

        public int hashCode() {
            return (((this.x.hashCode() ^ this.m) ^ this.k1) ^ this.k2) ^ this.k3;
        }
    }
}