package org.spongycastle.math.ec;

import java.math.BigInteger;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
class WTauNafMultiplier implements ECMultiplier {
    @Override // org.spongycastle.math.ec.ECMultiplier
    public ECPoint multiply(ECPoint point, BigInteger k, PreCompInfo preCompInfo) {
        if (!(point instanceof ECPoint.F2m)) {
            throw new IllegalArgumentException("Only ECPoint.F2m can be used in WTauNafMultiplier");
        }
        ECPoint.F2m p = (ECPoint.F2m) point;
        ECCurve.F2m curve = (ECCurve.F2m) p.getCurve();
        int m = curve.getM();
        byte a = curve.getA().toBigInteger().byteValue();
        byte mu = curve.getMu();
        BigInteger[] s = curve.getSi();
        ZTauElement rho = Tnaf.partModReduction(k, m, a, s, mu, (byte) 10);
        return multiplyWTnaf(p, rho, preCompInfo, a, mu);
    }

    private ECPoint.F2m multiplyWTnaf(ECPoint.F2m p, ZTauElement lambda, PreCompInfo preCompInfo, byte a, byte mu) {
        ZTauElement[] alpha;
        if (a == 0) {
            alpha = Tnaf.alpha0;
        } else {
            alpha = Tnaf.alpha1;
        }
        BigInteger tw = Tnaf.getTw(mu, 4);
        byte[] u = Tnaf.tauAdicWNaf(mu, lambda, (byte) 4, BigInteger.valueOf(16L), tw, alpha);
        return multiplyFromWTnaf(p, u, preCompInfo);
    }

    private static ECPoint.F2m multiplyFromWTnaf(ECPoint.F2m p, byte[] u, PreCompInfo preCompInfo) {
        ECPoint.F2m[] pu;
        ECCurve.F2m curve = (ECCurve.F2m) p.getCurve();
        byte a = curve.getA().toBigInteger().byteValue();
        if (preCompInfo == null || !(preCompInfo instanceof WTauNafPreCompInfo)) {
            pu = Tnaf.getPreComp(p, a);
            p.setPreCompInfo(new WTauNafPreCompInfo(pu));
        } else {
            pu = ((WTauNafPreCompInfo) preCompInfo).getPreComp();
        }
        ECPoint.F2m q = (ECPoint.F2m) p.getCurve().getInfinity();
        for (int i = u.length - 1; i >= 0; i--) {
            q = Tnaf.tau(q);
            if (u[i] != 0) {
                if (u[i] > 0) {
                    q = q.addSimple(pu[u[i]]);
                } else {
                    q = q.subtractSimple(pu[-u[i]]);
                }
            }
        }
        return q;
    }
}