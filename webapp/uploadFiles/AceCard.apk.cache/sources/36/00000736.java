package org.spongycastle.math.ec;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECCurve;

/* loaded from: classes.dex */
public class ECAlgorithms {
    public static ECPoint sumOfTwoMultiplies(ECPoint P, BigInteger a, ECPoint Q, BigInteger b) {
        ECCurve c = P.getCurve();
        if (!c.equals(Q.getCurve())) {
            throw new IllegalArgumentException("P and Q must be on same curve");
        }
        if (c instanceof ECCurve.F2m) {
            ECCurve.F2m f2mCurve = (ECCurve.F2m) c;
            if (f2mCurve.isKoblitz()) {
                return P.multiply(a).add(Q.multiply(b));
            }
        }
        return implShamirsTrick(P, a, Q, b);
    }

    public static ECPoint shamirsTrick(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        if (!P.getCurve().equals(Q.getCurve())) {
            throw new IllegalArgumentException("P and Q must be on same curve");
        }
        return implShamirsTrick(P, k, Q, l);
    }

    private static ECPoint implShamirsTrick(ECPoint P, BigInteger k, ECPoint Q, BigInteger l) {
        int m = Math.max(k.bitLength(), l.bitLength());
        ECPoint Z = P.add(Q);
        ECPoint R = P.getCurve().getInfinity();
        for (int i = m - 1; i >= 0; i--) {
            R = R.twice();
            if (k.testBit(i)) {
                if (l.testBit(i)) {
                    R = R.add(Z);
                } else {
                    R = R.add(P);
                }
            } else if (l.testBit(i)) {
                R = R.add(Q);
            }
        }
        return R;
    }
}