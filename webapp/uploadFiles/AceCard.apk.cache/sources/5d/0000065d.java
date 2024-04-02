package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.MGFParameters;

/* loaded from: classes.dex */
public class MGF1BytesGenerator implements DerivationFunction {
    private Digest digest;
    private int hLen;
    private byte[] seed;

    public MGF1BytesGenerator(Digest digest) {
        this.digest = digest;
        this.hLen = digest.getDigestSize();
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public void init(DerivationParameters param) {
        if (!(param instanceof MGFParameters)) {
            throw new IllegalArgumentException("MGF parameters required for MGF1Generator");
        }
        MGFParameters p = (MGFParameters) param;
        this.seed = p.getSeed();
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public Digest getDigest() {
        return this.digest;
    }

    private void ItoOSP(int i, byte[] sp) {
        sp[0] = (byte) (i >>> 24);
        sp[1] = (byte) (i >>> 16);
        sp[2] = (byte) (i >>> 8);
        sp[3] = (byte) (i >>> 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x004c  */
    @Override // org.spongycastle.crypto.DerivationFunction
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int generateBytes(byte[] r8, int r9, int r10) throws org.spongycastle.crypto.DataLengthException, java.lang.IllegalArgumentException {
        /*
            r7 = this;
            r6 = 0
            int r3 = r8.length
            int r3 = r3 - r10
            if (r3 >= r9) goto Ld
            org.spongycastle.crypto.DataLengthException r3 = new org.spongycastle.crypto.DataLengthException
            java.lang.String r4 = "output buffer too small"
            r3.<init>(r4)
            throw r3
        Ld:
            int r3 = r7.hLen
            byte[] r2 = new byte[r3]
            r3 = 4
            byte[] r0 = new byte[r3]
            r1 = 0
            org.spongycastle.crypto.Digest r3 = r7.digest
            r3.reset()
            int r3 = r7.hLen
            if (r10 <= r3) goto L47
        L1e:
            r7.ItoOSP(r1, r0)
            org.spongycastle.crypto.Digest r3 = r7.digest
            byte[] r4 = r7.seed
            byte[] r5 = r7.seed
            int r5 = r5.length
            r3.update(r4, r6, r5)
            org.spongycastle.crypto.Digest r3 = r7.digest
            int r4 = r0.length
            r3.update(r0, r6, r4)
            org.spongycastle.crypto.Digest r3 = r7.digest
            r3.doFinal(r2, r6)
            int r3 = r7.hLen
            int r3 = r3 * r1
            int r3 = r3 + r9
            int r4 = r7.hLen
            java.lang.System.arraycopy(r2, r6, r8, r3, r4)
            int r1 = r1 + 1
            int r3 = r7.hLen
            int r3 = r10 / r3
            if (r1 < r3) goto L1e
        L47:
            int r3 = r7.hLen
            int r3 = r3 * r1
            if (r3 >= r10) goto L70
            r7.ItoOSP(r1, r0)
            org.spongycastle.crypto.Digest r3 = r7.digest
            byte[] r4 = r7.seed
            byte[] r5 = r7.seed
            int r5 = r5.length
            r3.update(r4, r6, r5)
            org.spongycastle.crypto.Digest r3 = r7.digest
            int r4 = r0.length
            r3.update(r0, r6, r4)
            org.spongycastle.crypto.Digest r3 = r7.digest
            r3.doFinal(r2, r6)
            int r3 = r7.hLen
            int r3 = r3 * r1
            int r3 = r3 + r9
            int r4 = r7.hLen
            int r4 = r4 * r1
            int r4 = r10 - r4
            java.lang.System.arraycopy(r2, r6, r8, r3, r4)
        L70:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.spongycastle.crypto.generators.MGF1BytesGenerator.generateBytes(byte[], int, int):int");
    }
}