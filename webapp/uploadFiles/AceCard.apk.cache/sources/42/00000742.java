package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FpNafMultiplier implements ECMultiplier {
    @Override // org.spongycastle.math.ec.ECMultiplier
    public ECPoint multiply(ECPoint p, BigInteger k, PreCompInfo preCompInfo) {
        BigInteger h = k.multiply(BigInteger.valueOf(3L));
        ECPoint neg = p.negate();
        ECPoint R = p;
        for (int i = h.bitLength() - 2; i > 0; i--) {
            R = R.twice();
            boolean hBit = h.testBit(i);
            boolean eBit = k.testBit(i);
            if (hBit != eBit) {
                R = R.add(hBit ? p : neg);
            }
        }
        return R;
    }
}