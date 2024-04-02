package org.spongycastle.crypto.modes.gcm;

import java.lang.reflect.Array;
import org.spongycastle.crypto.util.Pack;

/* loaded from: classes.dex */
public class Tables64kGCMMultiplier implements GCMMultiplier {
    private final int[][][] M = (int[][][]) Array.newInstance(int[].class, 16, 256);

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        this.M[0][0] = new int[4];
        this.M[0][128] = GCMUtil.asInts(H);
        for (int j = 64; j >= 1; j >>= 1) {
            int[] tmp = new int[4];
            System.arraycopy(this.M[0][j + j], 0, tmp, 0, 4);
            GCMUtil.multiplyP(tmp);
            this.M[0][j] = tmp;
        }
        int i = 0;
        while (true) {
            for (int j2 = 2; j2 < 256; j2 += j2) {
                for (int k = 1; k < j2; k++) {
                    int[] tmp2 = new int[4];
                    System.arraycopy(this.M[i][j2], 0, tmp2, 0, 4);
                    GCMUtil.xor(tmp2, this.M[i][k]);
                    this.M[i][j2 + k] = tmp2;
                }
            }
            i++;
            if (i == 16) {
                return;
            }
            this.M[i][0] = new int[4];
            for (int j3 = 128; j3 > 0; j3 >>= 1) {
                int[] tmp3 = new int[4];
                System.arraycopy(this.M[i - 1][j3], 0, tmp3, 0, 4);
                GCMUtil.multiplyP8(tmp3);
                this.M[i][j3] = tmp3;
            }
        }
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        int[] z = new int[4];
        for (int i = 15; i >= 0; i--) {
            int[] m = this.M[i][x[i] & 255];
            z[0] = z[0] ^ m[0];
            z[1] = z[1] ^ m[1];
            z[2] = z[2] ^ m[2];
            z[3] = z[3] ^ m[3];
        }
        Pack.intToBigEndian(z, x, 0);
    }
}