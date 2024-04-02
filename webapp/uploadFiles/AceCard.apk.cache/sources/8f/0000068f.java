package org.spongycastle.crypto.modes.gcm;

import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class Tables1kGCMExponentiator implements GCMExponentiator {
    byte[][] lookupPowX2 = new byte[64];

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void init(byte[] x) {
        this.lookupPowX2[0] = GCMUtil.oneAsBytes();
        this.lookupPowX2[1] = Arrays.clone(x);
        for (int i = 2; i != 64; i++) {
            byte[] tmp = Arrays.clone(this.lookupPowX2[i - 1]);
            GCMUtil.multiply(tmp, tmp);
            this.lookupPowX2[i] = tmp;
        }
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void exponentiateX(long pow, byte[] output) {
        byte[] y = GCMUtil.oneAsBytes();
        int powX2 = 1;
        while (pow > 0) {
            if ((1 & pow) != 0) {
                GCMUtil.multiply(y, this.lookupPowX2[powX2]);
            }
            powX2++;
            pow >>>= 1;
        }
        System.arraycopy(y, 0, output, 0, 16);
    }
}