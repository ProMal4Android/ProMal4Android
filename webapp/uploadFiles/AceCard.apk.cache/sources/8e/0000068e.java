package org.spongycastle.crypto.modes.gcm;

import org.spongycastle.crypto.util.Pack;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
abstract class GCMUtil {
    GCMUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] oneAsBytes() {
        byte[] tmp = new byte[16];
        tmp[0] = Byte.MIN_VALUE;
        return tmp;
    }

    static int[] oneAsInts() {
        int[] tmp = new int[4];
        tmp[0] = Integer.MIN_VALUE;
        return tmp;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] asInts(byte[] bs) {
        int[] us = {Pack.bigEndianToInt(bs, 0), Pack.bigEndianToInt(bs, 4), Pack.bigEndianToInt(bs, 8), Pack.bigEndianToInt(bs, 12)};
        return us;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void multiply(byte[] block, byte[] val) {
        byte[] tmp = Arrays.clone(block);
        byte[] c = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte bits = val[i];
            for (int j = 7; j >= 0; j--) {
                if (((1 << j) & bits) != 0) {
                    xor(c, tmp);
                }
                boolean lsb = (tmp[15] & 1) != 0;
                shiftRight(tmp);
                if (lsb) {
                    tmp[0] = (byte) (tmp[0] ^ (-31));
                }
            }
        }
        System.arraycopy(c, 0, block, 0, 16);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void multiplyP(int[] x) {
        boolean lsb = (x[3] & 1) != 0;
        shiftRight(x);
        if (lsb) {
            x[0] = x[0] ^ (-520093696);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void multiplyP8(int[] x) {
        int lsw = x[3];
        shiftRightN(x, 8);
        for (int i = 7; i >= 0; i--) {
            if (((1 << i) & lsw) != 0) {
                x[0] = x[0] ^ ((-520093696) >>> (7 - i));
            }
        }
    }

    static void shiftRight(byte[] block) {
        int i = 0;
        int bit = 0;
        while (true) {
            int b = block[i] & 255;
            block[i] = (byte) ((b >>> 1) | bit);
            i++;
            if (i != 16) {
                bit = (b & 1) << 7;
            } else {
                return;
            }
        }
    }

    static void shiftRight(int[] block) {
        int i = 0;
        int bit = 0;
        while (true) {
            int b = block[i];
            block[i] = (b >>> 1) | bit;
            i++;
            if (i != 4) {
                bit = b << 31;
            } else {
                return;
            }
        }
    }

    static void shiftRightN(int[] block, int n) {
        int i = 0;
        int bits = 0;
        while (true) {
            int b = block[i];
            block[i] = (b >>> n) | bits;
            i++;
            if (i != 4) {
                bits = b << (32 - n);
            } else {
                return;
            }
        }
    }

    static void xor(byte[] block, byte[] val) {
        for (int i = 15; i >= 0; i--) {
            block[i] = (byte) (block[i] ^ val[i]);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void xor(int[] block, int[] val) {
        for (int i = 3; i >= 0; i--) {
            block[i] = block[i] ^ val[i];
        }
    }
}