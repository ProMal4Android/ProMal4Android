package org.spongycastle.math.ec;

import java.math.BigInteger;
import org.spongycastle.asn1.eac.CertificateBody;

/* loaded from: classes.dex */
class WNafMultiplier implements ECMultiplier {
    public byte[] windowNaf(byte width, BigInteger k) {
        byte[] wnaf = new byte[k.bitLength() + 1];
        short pow2wB = (short) (1 << width);
        BigInteger pow2wBI = BigInteger.valueOf(pow2wB);
        int i = 0;
        int length = 0;
        while (k.signum() > 0) {
            if (k.testBit(0)) {
                BigInteger remainder = k.mod(pow2wBI);
                if (remainder.testBit(width - 1)) {
                    wnaf[i] = (byte) (remainder.intValue() - pow2wB);
                } else {
                    wnaf[i] = (byte) remainder.intValue();
                }
                k = k.subtract(BigInteger.valueOf(wnaf[i]));
                length = i;
            } else {
                wnaf[i] = 0;
            }
            k = k.shiftRight(1);
            i++;
        }
        int length2 = length + 1;
        byte[] wnafShort = new byte[length2];
        System.arraycopy(wnaf, 0, wnafShort, 0, length2);
        return wnafShort;
    }

    @Override // org.spongycastle.math.ec.ECMultiplier
    public ECPoint multiply(ECPoint p, BigInteger k, PreCompInfo preCompInfo) {
        WNafPreCompInfo wnafPreCompInfo;
        byte width;
        int reqPreCompLen;
        if (preCompInfo != null && (preCompInfo instanceof WNafPreCompInfo)) {
            wnafPreCompInfo = (WNafPreCompInfo) preCompInfo;
        } else {
            wnafPreCompInfo = new WNafPreCompInfo();
        }
        int m = k.bitLength();
        if (m < 13) {
            width = 2;
            reqPreCompLen = 1;
        } else if (m < 41) {
            width = 3;
            reqPreCompLen = 2;
        } else if (m < 121) {
            width = 4;
            reqPreCompLen = 4;
        } else if (m < 337) {
            width = 5;
            reqPreCompLen = 8;
        } else if (m < 897) {
            width = 6;
            reqPreCompLen = 16;
        } else if (m < 2305) {
            width = 7;
            reqPreCompLen = 32;
        } else {
            width = 8;
            reqPreCompLen = CertificateBody.profileType;
        }
        int preCompLen = 1;
        ECPoint[] preComp = wnafPreCompInfo.getPreComp();
        ECPoint twiceP = wnafPreCompInfo.getTwiceP();
        if (preComp == null) {
            preComp = new ECPoint[]{p};
        } else {
            preCompLen = preComp.length;
        }
        if (twiceP == null) {
            twiceP = p.twice();
        }
        if (preCompLen < reqPreCompLen) {
            ECPoint[] oldPreComp = preComp;
            preComp = new ECPoint[reqPreCompLen];
            System.arraycopy(oldPreComp, 0, preComp, 0, preCompLen);
            for (int i = preCompLen; i < reqPreCompLen; i++) {
                preComp[i] = twiceP.add(preComp[i - 1]);
            }
        }
        byte[] wnaf = windowNaf(width, k);
        int l = wnaf.length;
        ECPoint q = p.getCurve().getInfinity();
        for (int i2 = l - 1; i2 >= 0; i2--) {
            q = q.twice();
            if (wnaf[i2] != 0) {
                if (wnaf[i2] > 0) {
                    q = q.add(preComp[(wnaf[i2] - 1) / 2]);
                } else {
                    q = q.subtract(preComp[((-wnaf[i2]) - 1) / 2]);
                }
            }
        }
        wnafPreCompInfo.setPreComp(preComp);
        wnafPreCompInfo.setTwiceP(twiceP);
        p.setPreCompInfo(wnafPreCompInfo);
        return q;
    }
}