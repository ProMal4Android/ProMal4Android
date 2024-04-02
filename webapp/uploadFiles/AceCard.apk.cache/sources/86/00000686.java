package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;

/* loaded from: classes.dex */
public class OpenPGPCFBBlockCipher implements BlockCipher {
    private byte[] FR;
    private byte[] FRE;
    private byte[] IV;
    private int blockSize;
    private BlockCipher cipher;
    private int count;
    private boolean forEncryption;

    public OpenPGPCFBBlockCipher(BlockCipher cipher) {
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.FR = new byte[this.blockSize];
        this.FRE = new byte[this.blockSize];
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/OpenPGPCFB";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        return this.forEncryption ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
        this.count = 0;
        System.arraycopy(this.IV, 0, this.FR, 0, this.FR.length);
        this.cipher.reset();
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        reset();
        this.cipher.init(true, params);
    }

    private byte encryptByte(byte data, int blockOff) {
        return (byte) (this.FRE[blockOff] ^ data);
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.blockSize + inOff > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (this.blockSize + outOff > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            byte encryptByte = encryptByte(in[inOff], this.blockSize - 2);
            out[outOff] = encryptByte;
            this.FR[this.blockSize - 2] = encryptByte;
            byte encryptByte2 = encryptByte(in[inOff + 1], this.blockSize - 1);
            out[outOff + 1] = encryptByte2;
            this.FR[this.blockSize - 1] = encryptByte2;
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 2; n < this.blockSize; n++) {
                byte encryptByte3 = encryptByte(in[inOff + n], n - 2);
                out[outOff + n] = encryptByte3;
                this.FR[n - 2] = encryptByte3;
            }
        } else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n2 = 0; n2 < this.blockSize; n2++) {
                byte[] bArr = this.FR;
                byte encryptByte4 = encryptByte(in[inOff + n2], n2);
                out[outOff + n2] = encryptByte4;
                bArr[n2] = encryptByte4;
            }
            this.count += this.blockSize;
        } else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            out[outOff] = encryptByte(in[inOff], 0);
            out[outOff + 1] = encryptByte(in[inOff + 1], 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            System.arraycopy(out, outOff, this.FR, this.blockSize - 2, 2);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n3 = 2; n3 < this.blockSize; n3++) {
                byte encryptByte5 = encryptByte(in[inOff + n3], n3 - 2);
                out[outOff + n3] = encryptByte5;
                this.FR[n3 - 2] = encryptByte5;
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.blockSize + inOff > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (this.blockSize + outOff > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        if (this.count > this.blockSize) {
            byte inVal = in[inOff];
            this.FR[this.blockSize - 2] = inVal;
            out[outOff] = encryptByte(inVal, this.blockSize - 2);
            byte inVal2 = in[inOff + 1];
            this.FR[this.blockSize - 1] = inVal2;
            out[outOff + 1] = encryptByte(inVal2, this.blockSize - 1);
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n = 2; n < this.blockSize; n++) {
                byte inVal3 = in[inOff + n];
                this.FR[n - 2] = inVal3;
                out[outOff + n] = encryptByte(inVal3, n - 2);
            }
        } else if (this.count == 0) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n2 = 0; n2 < this.blockSize; n2++) {
                this.FR[n2] = in[inOff + n2];
                out[n2] = encryptByte(in[inOff + n2], n2);
            }
            this.count += this.blockSize;
        } else if (this.count == this.blockSize) {
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            byte inVal1 = in[inOff];
            byte inVal22 = in[inOff + 1];
            out[outOff] = encryptByte(inVal1, 0);
            out[outOff + 1] = encryptByte(inVal22, 1);
            System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
            this.FR[this.blockSize - 2] = inVal1;
            this.FR[this.blockSize - 1] = inVal22;
            this.cipher.processBlock(this.FR, 0, this.FRE, 0);
            for (int n3 = 2; n3 < this.blockSize; n3++) {
                byte inVal4 = in[inOff + n3];
                this.FR[n3 - 2] = inVal4;
                out[outOff + n3] = encryptByte(inVal4, n3 - 2);
            }
            this.count += this.blockSize;
        }
        return this.blockSize;
    }
}