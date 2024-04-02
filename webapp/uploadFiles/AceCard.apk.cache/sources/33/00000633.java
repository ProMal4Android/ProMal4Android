package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;

/* loaded from: classes.dex */
public class NullEngine implements BlockCipher {
    protected static final int BLOCK_SIZE = 1;
    private boolean initialised;

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.initialised = true;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Null";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 1;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (!this.initialised) {
            throw new IllegalStateException("Null engine not initialised");
        }
        if (inOff + 1 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 1 > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        for (int i = 0; i < 1; i++) {
            out[outOff + i] = in[inOff + i];
        }
        return 1;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }
}