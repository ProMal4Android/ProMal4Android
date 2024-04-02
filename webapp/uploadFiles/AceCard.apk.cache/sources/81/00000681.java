package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;

/* loaded from: classes.dex */
public class CTSBlockCipher extends BufferedBlockCipher {
    private int blockSize;

    public CTSBlockCipher(BlockCipher cipher) {
        if ((cipher instanceof OFBBlockCipher) || (cipher instanceof CFBBlockCipher)) {
            throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
        }
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.buf = new byte[this.blockSize * 2];
        this.bufOff = 0;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int getUpdateOutputSize(int len) {
        int total = len + this.bufOff;
        int leftOver = total % this.buf.length;
        return leftOver == 0 ? total - this.buf.length : total - leftOver;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int getOutputSize(int len) {
        return this.bufOff + len;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int resultLen = 0;
        if (this.bufOff == this.buf.length) {
            resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
            System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
            this.bufOff = this.blockSize;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
        return resultLen;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = getBlockSize();
        int length = getUpdateOutputSize(len);
        if (length > 0 && outOff + length > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        int resultLen = 0;
        int gapLen = this.buf.length - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            resultLen = 0 + this.cipher.processBlock(this.buf, 0, out, outOff);
            System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
            this.bufOff = blockSize;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                System.arraycopy(in, inOff, this.buf, this.bufOff, blockSize);
                resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
                System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
        return resultLen;
    }

    @Override // org.spongycastle.crypto.BufferedBlockCipher
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        if (this.bufOff + outOff > out.length) {
            throw new DataLengthException("output buffer to small in doFinal");
        }
        int blockSize = this.cipher.getBlockSize();
        int len = this.bufOff - blockSize;
        byte[] block = new byte[blockSize];
        if (this.forEncryption) {
            this.cipher.processBlock(this.buf, 0, block, 0);
            if (this.bufOff < blockSize) {
                throw new DataLengthException("need at least one block of input for CTS");
            }
            for (int i = this.bufOff; i != this.buf.length; i++) {
                this.buf[i] = block[i - blockSize];
            }
            for (int i2 = blockSize; i2 != this.bufOff; i2++) {
                byte[] bArr = this.buf;
                bArr[i2] = (byte) (bArr[i2] ^ block[i2 - blockSize]);
            }
            if (this.cipher instanceof CBCBlockCipher) {
                BlockCipher c = ((CBCBlockCipher) this.cipher).getUnderlyingCipher();
                c.processBlock(this.buf, blockSize, out, outOff);
            } else {
                this.cipher.processBlock(this.buf, blockSize, out, outOff);
            }
            System.arraycopy(block, 0, out, outOff + blockSize, len);
        } else {
            byte[] lastBlock = new byte[blockSize];
            if (this.cipher instanceof CBCBlockCipher) {
                BlockCipher c2 = ((CBCBlockCipher) this.cipher).getUnderlyingCipher();
                c2.processBlock(this.buf, 0, block, 0);
            } else {
                this.cipher.processBlock(this.buf, 0, block, 0);
            }
            for (int i3 = blockSize; i3 != this.bufOff; i3++) {
                lastBlock[i3 - blockSize] = (byte) (block[i3 - blockSize] ^ this.buf[i3]);
            }
            System.arraycopy(this.buf, blockSize, block, 0, len);
            this.cipher.processBlock(block, 0, out, outOff);
            System.arraycopy(lastBlock, 0, out, outOff + blockSize, len);
        }
        int offset = this.bufOff;
        reset();
        return offset;
    }
}