package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.params.ParametersWithIV;

/* loaded from: classes.dex */
public class SICBlockCipher implements BlockCipher {
    private byte[] IV;
    private final int blockSize;
    private final BlockCipher cipher;
    private byte[] counter;
    private byte[] counterOut;

    public SICBlockCipher(BlockCipher c) {
        this.cipher = c;
        this.blockSize = this.cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.counter = new byte[this.blockSize];
        this.counterOut = new byte[this.blockSize];
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV) params;
            byte[] iv = ivParam.getIV();
            System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
            reset();
            if (ivParam.getParameters() != null) {
                this.cipher.init(true, ivParam.getParameters());
                return;
            }
            return;
        }
        throw new IllegalArgumentException("SIC mode requires ParametersWithIV");
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/SIC";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
        for (int i = 0; i < this.counterOut.length; i++) {
            out[outOff + i] = (byte) (this.counterOut[i] ^ in[inOff + i]);
        }
        int carry = 1;
        for (int i2 = this.counter.length - 1; i2 >= 0; i2--) {
            int x = (this.counter[i2] & 255) + carry;
            if (x > 255) {
                carry = 1;
            } else {
                carry = 0;
            }
            this.counter[i2] = (byte) x;
        }
        return this.counter.length;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
        System.arraycopy(this.IV, 0, this.counter, 0, this.counter.length);
        this.cipher.reset();
    }
}