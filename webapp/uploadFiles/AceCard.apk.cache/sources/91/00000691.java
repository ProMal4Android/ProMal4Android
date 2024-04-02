package org.spongycastle.crypto.modes.gcm;

import java.lang.reflect.Array;
import net.freehaven.tor.control.TorControlCommands;
import org.spongycastle.crypto.util.Pack;

/* loaded from: classes.dex */
public class Tables8kGCMMultiplier implements GCMMultiplier {
    private final int[][][] M = (int[][][]) Array.newInstance(int[].class, 32, 16);

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        this.M[0][0] = new int[4];
        this.M[1][0] = new int[4];
        this.M[1][8] = GCMUtil.asInts(H);
        for (int j = 4; j >= 1; j >>= 1) {
            int[] tmp = new int[4];
            System.arraycopy(this.M[1][j + j], 0, tmp, 0, 4);
            GCMUtil.multiplyP(tmp);
            this.M[1][j] = tmp;
        }
        int[] tmp2 = new int[4];
        System.arraycopy(this.M[1][1], 0, tmp2, 0, 4);
        GCMUtil.multiplyP(tmp2);
        this.M[0][8] = tmp2;
        for (int j2 = 4; j2 >= 1; j2 >>= 1) {
            int[] tmp3 = new int[4];
            System.arraycopy(this.M[0][j2 + j2], 0, tmp3, 0, 4);
            GCMUtil.multiplyP(tmp3);
            this.M[0][j2] = tmp3;
        }
        int i = 0;
        while (true) {
            for (int j3 = 2; j3 < 16; j3 += j3) {
                for (int k = 1; k < j3; k++) {
                    int[] tmp4 = new int[4];
                    System.arraycopy(this.M[i][j3], 0, tmp4, 0, 4);
                    GCMUtil.xor(tmp4, this.M[i][k]);
                    this.M[i][j3 + k] = tmp4;
                }
            }
            i++;
            if (i == 32) {
                return;
            }
            if (i > 1) {
                this.M[i][0] = new int[4];
                for (int j4 = 8; j4 > 0; j4 >>= 1) {
                    int[] tmp5 = new int[4];
                    System.arraycopy(this.M[i - 2][j4], 0, tmp5, 0, 4);
                    GCMUtil.multiplyP8(tmp5);
                    this.M[i][j4] = tmp5;
                }
            }
        }
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        int[] z = new int[4];
        for (int i = 15; i >= 0; i--) {
            int[] m = this.M[i + i][x[i] & TorControlCommands.SIGNAL_TERM];
            z[0] = z[0] ^ m[0];
            z[1] = z[1] ^ m[1];
            z[2] = z[2] ^ m[2];
            z[3] = z[3] ^ m[3];
            int[] m2 = this.M[i + i + 1][(x[i] & 240) >>> 4];
            z[0] = z[0] ^ m2[0];
            z[1] = z[1] ^ m2[1];
            z[2] = z[2] ^ m2[2];
            z[3] = z[3] ^ m2[3];
        }
        Pack.intToBigEndian(z, x, 0);
    }
}