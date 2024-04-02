package org.spongycastle.crypto.modes.gcm;

import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class BasicGCMExponentiator implements GCMExponentiator {
    private byte[] x;

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void init(byte[] x) {
        this.x = Arrays.clone(x);
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMExponentiator
    public void exponentiateX(long pow, byte[] output) {
        byte[] y = GCMUtil.oneAsBytes();
        if (pow > 0) {
            byte[] powX = Arrays.clone(this.x);
            do {
                if ((1 & pow) != 0) {
                    GCMUtil.multiply(y, powX);
                }
                GCMUtil.multiply(powX, powX);
                pow >>>= 1;
            } while (pow > 0);
            System.arraycopy(y, 0, output, 0, 16);
        }
        System.arraycopy(y, 0, output, 0, 16);
    }
}