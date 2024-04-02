package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.DerivationParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.ISO18033KDFParameters;
import org.spongycastle.crypto.params.KDFParameters;

/* loaded from: classes.dex */
public class BaseKDFBytesGenerator implements DerivationFunction {
    private int counterStart;
    private Digest digest;
    private byte[] iv;
    private byte[] shared;

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseKDFBytesGenerator(int counterStart, Digest digest) {
        this.counterStart = counterStart;
        this.digest = digest;
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public void init(DerivationParameters param) {
        if (param instanceof KDFParameters) {
            KDFParameters p = (KDFParameters) param;
            this.shared = p.getSharedSecret();
            this.iv = p.getIV();
        } else if (param instanceof ISO18033KDFParameters) {
            this.shared = ((ISO18033KDFParameters) param).getSeed();
            this.iv = null;
        } else {
            throw new IllegalArgumentException("KDF parameters required for KDF2Generator");
        }
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public Digest getDigest() {
        return this.digest;
    }

    @Override // org.spongycastle.crypto.DerivationFunction
    public int generateBytes(byte[] out, int outOff, int len) throws DataLengthException, IllegalArgumentException {
        if (out.length - len < outOff) {
            throw new DataLengthException("output buffer too small");
        }
        long oBytes = len;
        int outLen = this.digest.getDigestSize();
        if (oBytes > 8589934591L) {
            throw new IllegalArgumentException("Output length too large");
        }
        int cThreshold = (int) (((outLen + oBytes) - 1) / outLen);
        byte[] dig = new byte[this.digest.getDigestSize()];
        int counter = this.counterStart;
        for (int i = 0; i < cThreshold; i++) {
            this.digest.update(this.shared, 0, this.shared.length);
            this.digest.update((byte) (counter >> 24));
            this.digest.update((byte) (counter >> 16));
            this.digest.update((byte) (counter >> 8));
            this.digest.update((byte) counter);
            if (this.iv != null) {
                this.digest.update(this.iv, 0, this.iv.length);
            }
            this.digest.doFinal(dig, 0);
            if (len > outLen) {
                System.arraycopy(dig, 0, out, outOff, outLen);
                outOff += outLen;
                len -= outLen;
            } else {
                System.arraycopy(dig, 0, out, outOff, len);
            }
            counter++;
        }
        this.digest.reset();
        return len;
    }
}