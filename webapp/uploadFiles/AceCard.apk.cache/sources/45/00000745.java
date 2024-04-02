package org.spongycastle.math.ec;

import java.math.BigInteger;

/* loaded from: classes.dex */
class ReferenceMultiplier implements ECMultiplier {
    ReferenceMultiplier() {
    }

    @Override // org.spongycastle.math.ec.ECMultiplier
    public ECPoint multiply(ECPoint p, BigInteger k, PreCompInfo preCompInfo) {
        ECPoint q = p.getCurve().getInfinity();
        int t = k.bitLength();
        for (int i = 0; i < t; i++) {
            if (k.testBit(i)) {
                q = q.add(p);
            }
            p = p.twice();
        }
        return q;
    }
}