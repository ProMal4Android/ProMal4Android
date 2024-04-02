package org.spongycastle.math.ec;

import java.math.BigInteger;
import org.spongycastle.asn1.x9.X9IntegerConverter;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECFieldElement;

/* loaded from: classes.dex */
public abstract class ECPoint {
    private static X9IntegerConverter converter = new X9IntegerConverter();
    ECCurve curve;
    protected ECMultiplier multiplier = null;
    protected PreCompInfo preCompInfo = null;
    protected boolean withCompression;
    ECFieldElement x;
    ECFieldElement y;

    public abstract ECPoint add(ECPoint eCPoint);

    public abstract byte[] getEncoded();

    public abstract ECPoint negate();

    public abstract ECPoint subtract(ECPoint eCPoint);

    public abstract ECPoint twice();

    protected ECPoint(ECCurve curve, ECFieldElement x, ECFieldElement y) {
        this.curve = curve;
        this.x = x;
        this.y = y;
    }

    public ECCurve getCurve() {
        return this.curve;
    }

    public ECFieldElement getX() {
        return this.x;
    }

    public ECFieldElement getY() {
        return this.y;
    }

    public boolean isInfinity() {
        return this.x == null && this.y == null;
    }

    public boolean isCompressed() {
        return this.withCompression;
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof ECPoint) {
            ECPoint o = (ECPoint) other;
            if (isInfinity()) {
                return o.isInfinity();
            }
            return this.x.equals(o.x) && this.y.equals(o.y);
        }
        return false;
    }

    public int hashCode() {
        if (isInfinity()) {
            return 0;
        }
        return this.x.hashCode() ^ this.y.hashCode();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPreCompInfo(PreCompInfo preCompInfo) {
        this.preCompInfo = preCompInfo;
    }

    synchronized void assertECMultiplier() {
        if (this.multiplier == null) {
            this.multiplier = new FpNafMultiplier();
        }
    }

    public ECPoint multiply(BigInteger k) {
        if (k.signum() < 0) {
            throw new IllegalArgumentException("The multiplicator cannot be negative");
        }
        if (!isInfinity()) {
            if (k.signum() == 0) {
                return this.curve.getInfinity();
            }
            assertECMultiplier();
            return this.multiplier.multiply(this, k, this.preCompInfo);
        }
        return this;
    }

    /* loaded from: classes.dex */
    public static class Fp extends ECPoint {
        public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y) {
            this(curve, x, y, false);
        }

        public Fp(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression) {
            super(curve, x, y);
            if ((x != null && y == null) || (x == null && y != null)) {
                throw new IllegalArgumentException("Exactly one of the field elements is null");
            }
            this.withCompression = withCompression;
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public byte[] getEncoded() {
            byte PC;
            if (!isInfinity()) {
                int qLength = ECPoint.converter.getByteLength(this.x);
                if (!this.withCompression) {
                    byte[] X = ECPoint.converter.integerToBytes(getX().toBigInteger(), qLength);
                    byte[] Y = ECPoint.converter.integerToBytes(getY().toBigInteger(), qLength);
                    byte[] PO = new byte[X.length + Y.length + 1];
                    PO[0] = 4;
                    System.arraycopy(X, 0, PO, 1, X.length);
                    System.arraycopy(Y, 0, PO, X.length + 1, Y.length);
                    return PO;
                }
                if (getY().toBigInteger().testBit(0)) {
                    PC = 3;
                } else {
                    PC = 2;
                }
                byte[] X2 = ECPoint.converter.integerToBytes(getX().toBigInteger(), qLength);
                byte[] PO2 = new byte[X2.length + 1];
                PO2[0] = PC;
                System.arraycopy(X2, 0, PO2, 1, X2.length);
                return PO2;
            }
            return new byte[1];
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint add(ECPoint b) {
            if (!isInfinity()) {
                if (b.isInfinity()) {
                    return this;
                }
                if (this.x.equals(b.x)) {
                    if (this.y.equals(b.y)) {
                        return twice();
                    }
                    return this.curve.getInfinity();
                }
                ECFieldElement gamma = b.y.subtract(this.y).divide(b.x.subtract(this.x));
                ECFieldElement x3 = gamma.square().subtract(this.x).subtract(b.x);
                ECFieldElement y3 = gamma.multiply(this.x.subtract(x3)).subtract(this.y);
                return new Fp(this.curve, x3, y3);
            }
            return b;
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint twice() {
            if (!isInfinity()) {
                if (this.y.toBigInteger().signum() == 0) {
                    return this.curve.getInfinity();
                }
                ECFieldElement TWO = this.curve.fromBigInteger(BigInteger.valueOf(2L));
                ECFieldElement THREE = this.curve.fromBigInteger(BigInteger.valueOf(3L));
                ECFieldElement gamma = this.x.square().multiply(THREE).add(this.curve.a).divide(this.y.multiply(TWO));
                ECFieldElement x3 = gamma.square().subtract(this.x.multiply(TWO));
                ECFieldElement y3 = gamma.multiply(this.x.subtract(x3)).subtract(this.y);
                return new Fp(this.curve, x3, y3, this.withCompression);
            }
            return this;
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint subtract(ECPoint b) {
            return b.isInfinity() ? this : add(b.negate());
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint negate() {
            return new Fp(this.curve, this.x, this.y.negate(), this.withCompression);
        }

        @Override // org.spongycastle.math.ec.ECPoint
        synchronized void assertECMultiplier() {
            if (this.multiplier == null) {
                this.multiplier = new WNafMultiplier();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class F2m extends ECPoint {
        public F2m(ECCurve curve, ECFieldElement x, ECFieldElement y) {
            this(curve, x, y, false);
        }

        public F2m(ECCurve curve, ECFieldElement x, ECFieldElement y, boolean withCompression) {
            super(curve, x, y);
            if ((x != null && y == null) || (x == null && y != null)) {
                throw new IllegalArgumentException("Exactly one of the field elements is null");
            }
            if (x != null) {
                ECFieldElement.F2m.checkFieldElements(this.x, this.y);
                if (curve != null) {
                    ECFieldElement.F2m.checkFieldElements(this.x, this.curve.getA());
                }
            }
            this.withCompression = withCompression;
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public byte[] getEncoded() {
            if (!isInfinity()) {
                int byteCount = ECPoint.converter.getByteLength(this.x);
                byte[] X = ECPoint.converter.integerToBytes(getX().toBigInteger(), byteCount);
                if (!this.withCompression) {
                    byte[] Y = ECPoint.converter.integerToBytes(getY().toBigInteger(), byteCount);
                    byte[] PO = new byte[byteCount + byteCount + 1];
                    PO[0] = 4;
                    System.arraycopy(X, 0, PO, 1, byteCount);
                    System.arraycopy(Y, 0, PO, byteCount + 1, byteCount);
                    return PO;
                }
                byte[] PO2 = new byte[byteCount + 1];
                PO2[0] = 2;
                if (!getX().toBigInteger().equals(ECConstants.ZERO) && getY().multiply(getX().invert()).toBigInteger().testBit(0)) {
                    PO2[0] = 3;
                }
                System.arraycopy(X, 0, PO2, 1, byteCount);
                return PO2;
            }
            return new byte[1];
        }

        private static void checkPoints(ECPoint a, ECPoint b) {
            if (!a.curve.equals(b.curve)) {
                throw new IllegalArgumentException("Only points on the same curve can be added or subtracted");
            }
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint add(ECPoint b) {
            checkPoints(this, b);
            return addSimple((F2m) b);
        }

        public F2m addSimple(F2m b) {
            if (isInfinity()) {
                return b;
            }
            if (b.isInfinity()) {
                return this;
            }
            ECFieldElement.F2m x2 = (ECFieldElement.F2m) b.getX();
            ECFieldElement.F2m y2 = (ECFieldElement.F2m) b.getY();
            if (this.x.equals(x2)) {
                if (this.y.equals(y2)) {
                    return (F2m) twice();
                }
                return (F2m) this.curve.getInfinity();
            }
            ECFieldElement.F2m lambda = (ECFieldElement.F2m) this.y.add(y2).divide(this.x.add(x2));
            ECFieldElement.F2m x3 = (ECFieldElement.F2m) lambda.square().add(lambda).add(this.x).add(x2).add(this.curve.getA());
            ECFieldElement.F2m y3 = (ECFieldElement.F2m) lambda.multiply(this.x.add(x3)).add(x3).add(this.y);
            return new F2m(this.curve, x3, y3, this.withCompression);
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint subtract(ECPoint b) {
            checkPoints(this, b);
            return subtractSimple((F2m) b);
        }

        public F2m subtractSimple(F2m b) {
            return b.isInfinity() ? this : addSimple((F2m) b.negate());
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint twice() {
            if (!isInfinity()) {
                if (this.x.toBigInteger().signum() == 0) {
                    return this.curve.getInfinity();
                }
                ECFieldElement.F2m lambda = (ECFieldElement.F2m) this.x.add(this.y.divide(this.x));
                ECFieldElement.F2m x3 = (ECFieldElement.F2m) lambda.square().add(lambda).add(this.curve.getA());
                ECFieldElement ONE = this.curve.fromBigInteger(ECConstants.ONE);
                ECFieldElement.F2m y3 = (ECFieldElement.F2m) this.x.square().add(x3.multiply(lambda.add(ONE)));
                return new F2m(this.curve, x3, y3, this.withCompression);
            }
            return this;
        }

        @Override // org.spongycastle.math.ec.ECPoint
        public ECPoint negate() {
            return new F2m(this.curve, getX(), getY().add(getX()), this.withCompression);
        }

        @Override // org.spongycastle.math.ec.ECPoint
        synchronized void assertECMultiplier() {
            if (this.multiplier == null) {
                if (((ECCurve.F2m) this.curve).isKoblitz()) {
                    this.multiplier = new WTauNafMultiplier();
                } else {
                    this.multiplier = new WNafMultiplier();
                }
            }
        }
    }
}