package org.spongycastle.crypto.params;

/* loaded from: classes.dex */
public class DESParameters extends KeyParameter {
    public static final int DES_KEY_LENGTH = 8;
    private static byte[] DES_weak_keys = {1, 1, 1, 1, 1, 1, 1, 1, 31, 31, 31, 31, 14, 14, 14, 14, -32, -32, -32, -32, -15, -15, -15, -15, -2, -2, -2, -2, -2, -2, -2, -2, 1, -2, 1, -2, 1, -2, 1, -2, 31, -32, 31, -32, 14, -15, 14, -15, 1, -32, 1, -32, 1, -15, 1, -15, 31, -2, 31, -2, 14, -2, 14, -2, 1, 31, 1, 31, 1, 14, 1, 14, -32, -2, -32, -2, -15, -2, -15, -2, -2, 1, -2, 1, -2, 1, -2, 1, -32, 31, -32, 31, -15, 14, -15, 14, -32, 1, -32, 1, -15, 1, -15, 1, -2, 31, -2, 31, -2, 14, -2, 14, 31, 1, 31, 1, 14, 1, 14, 1, -2, -32, -2, -32, -2, -15, -2, -15};
    private static final int N_DES_WEAK_KEYS = 16;

    public DESParameters(byte[] key) {
        super(key);
        if (isWeakKey(key, 0)) {
            throw new IllegalArgumentException("attempt to create weak DES key");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0023, code lost:
        r0 = r0 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static boolean isWeakKey(byte[] r6, int r7) {
        /*
            r5 = 8
            int r2 = r6.length
            int r2 = r2 - r7
            if (r2 >= r5) goto Le
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException
            java.lang.String r3 = "key material too short."
            r2.<init>(r3)
            throw r2
        Le:
            r0 = 0
        Lf:
            r2 = 16
            if (r0 >= r2) goto L2b
            r1 = 0
        L14:
            if (r1 >= r5) goto L29
            int r2 = r1 + r7
            r2 = r6[r2]
            byte[] r3 = org.spongycastle.crypto.params.DESParameters.DES_weak_keys
            int r4 = r0 * 8
            int r4 = r4 + r1
            r3 = r3[r4]
            if (r2 == r3) goto L26
            int r0 = r0 + 1
            goto Lf
        L26:
            int r1 = r1 + 1
            goto L14
        L29:
            r2 = 1
        L2a:
            return r2
        L2b:
            r2 = 0
            goto L2a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.spongycastle.crypto.params.DESParameters.isWeakKey(byte[], int):boolean");
    }

    public static void setOddParity(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            bytes[i] = (byte) ((b & 254) | (((((((((b >> 1) ^ (b >> 2)) ^ (b >> 3)) ^ (b >> 4)) ^ (b >> 5)) ^ (b >> 6)) ^ (b >> 7)) ^ 1) & 1));
        }
    }
}