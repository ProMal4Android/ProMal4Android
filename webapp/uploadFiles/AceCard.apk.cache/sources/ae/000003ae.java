package net.freehaven.tor.control;

import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
final class Bytes {
    private static final char[] NYBBLES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static void setU16(byte[] bArr, int i, short s) {
        bArr[i] = (byte) ((s >> 8) & 255);
        bArr[i + 1] = (byte) (s & 255);
    }

    public static void setU32(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) ((i2 >> 24) & 255);
        bArr[i + 1] = (byte) ((i2 >> 16) & 255);
        bArr[i + 2] = (byte) ((i2 >> 8) & 255);
        bArr[i + 3] = (byte) (i2 & 255);
    }

    public static int getU32(byte[] bArr, int i) {
        return ((bArr[i] & 255) << 24) | ((bArr[i + 1] & 255) << 16) | ((bArr[i + 2] & 255) << 8) | (bArr[i + 3] & 255);
    }

    public static String getU32S(byte[] bArr, int i) {
        return String.valueOf(getU32(bArr, i) & 4294967295L);
    }

    public static int getU16(byte[] bArr, int i) {
        return ((bArr[i] & 255) << 8) | (bArr[i + 1] & 255);
    }

    public static String getNulTerminatedStr(byte[] bArr, int i) {
        int length = bArr.length - i;
        int i2 = 0;
        while (i2 < length && bArr[i + i2] != 0) {
            i2++;
        }
        return new String(bArr, i, i2);
    }

    /* JADX WARN: Code restructure failed: missing block: B:14:0x001b, code lost:
        r3.add(new java.lang.String(r4, r5, r0));
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void splitStr(java.util.List<java.lang.String> r3, byte[] r4, int r5, byte r6) {
        /*
        L0:
            int r0 = r4.length
            if (r5 >= r0) goto L2e
            r0 = r4[r5]
            if (r0 == 0) goto L2e
            r0 = 0
        L8:
            int r1 = r5 + r0
            int r2 = r4.length
            if (r1 >= r2) goto L19
            int r1 = r5 + r0
            r1 = r4[r1]
            if (r1 == 0) goto L19
            int r1 = r5 + r0
            r1 = r4[r1]
            if (r1 != r6) goto L2b
        L19:
            if (r0 <= 0) goto L23
            java.lang.String r1 = new java.lang.String
            r1.<init>(r4, r5, r0)
            r3.add(r1)
        L23:
            int r5 = r5 + r0
            r0 = r4[r5]
            if (r0 != r6) goto L0
            int r5 = r5 + 1
            goto L0
        L2b:
            int r0 = r0 + 1
            goto L8
        L2e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: net.freehaven.tor.control.Bytes.splitStr(java.util.List, byte[], int, byte):void");
    }

    public static List<String> splitStr(List<String> list, String str) {
        String[] split = str.split(" ", -1);
        if (list == null) {
            return Arrays.asList(split);
        }
        list.addAll(Arrays.asList(split));
        return list;
    }

    public static final String hex(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            int i = b & 255;
            stringBuffer.append(NYBBLES[i >> 4]);
            stringBuffer.append(NYBBLES[i & 15]);
        }
        return stringBuffer.toString();
    }

    private Bytes() {
    }
}