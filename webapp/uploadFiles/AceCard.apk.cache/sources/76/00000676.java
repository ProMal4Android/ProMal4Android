package org.spongycastle.crypto.macs;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.ISO7816d4Padding;

/* loaded from: classes.dex */
public class CMac implements Mac {
    private static final byte CONSTANT_128 = -121;
    private static final byte CONSTANT_64 = 27;
    private byte[] L;
    private byte[] Lu;
    private byte[] Lu2;
    private byte[] ZEROES;
    private byte[] buf;
    private int bufOff;
    private BlockCipher cipher;
    private byte[] mac;
    private int macSize;

    public CMac(BlockCipher cipher) {
        this(cipher, cipher.getBlockSize() * 8);
    }

    public CMac(BlockCipher cipher, int macSizeInBits) {
        if (macSizeInBits % 8 != 0) {
            throw new IllegalArgumentException("MAC size must be multiple of 8");
        }
        if (macSizeInBits > cipher.getBlockSize() * 8) {
            throw new IllegalArgumentException("MAC size must be less or equal to " + (cipher.getBlockSize() * 8));
        }
        if (cipher.getBlockSize() != 8 && cipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("Block size must be either 64 or 128 bits");
        }
        this.cipher = new CBCBlockCipher(cipher);
        this.macSize = macSizeInBits / 8;
        this.mac = new byte[cipher.getBlockSize()];
        this.buf = new byte[cipher.getBlockSize()];
        this.ZEROES = new byte[cipher.getBlockSize()];
        this.bufOff = 0;
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName();
    }

    private byte[] doubleLu(byte[] in) {
        int FirstBit = (in[0] & 255) >> 7;
        byte[] ret = new byte[in.length];
        for (int i = 0; i < in.length - 1; i++) {
            ret[i] = (byte) ((in[i] << 1) + ((in[i + 1] & 255) >> 7));
        }
        ret[in.length - 1] = (byte) (in[in.length - 1] << 1);
        if (FirstBit == 1) {
            int length = in.length - 1;
            ret[length] = (byte) ((in.length == 16 ? CONSTANT_128 : CONSTANT_64) ^ ret[length]);
        }
        return ret;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) {
        reset();
        this.cipher.init(true, params);
        this.L = new byte[this.ZEROES.length];
        this.cipher.processBlock(this.ZEROES, 0, this.L, 0);
        this.Lu = doubleLu(this.L);
        this.Lu2 = doubleLu(this.Lu);
        this.cipher.init(true, params);
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) {
        if (this.bufOff == this.buf.length) {
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int blockSize = this.cipher.getBlockSize();
        int gapLen = blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            this.cipher.processBlock(this.buf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > blockSize) {
                this.cipher.processBlock(in, inOff, this.mac, 0);
                len -= blockSize;
                inOff += blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) {
        byte[] lu;
        int blockSize = this.cipher.getBlockSize();
        if (this.bufOff == blockSize) {
            lu = this.Lu;
        } else {
            new ISO7816d4Padding().addPadding(this.buf, this.bufOff);
            lu = this.Lu2;
        }
        for (int i = 0; i < this.mac.length; i++) {
            byte[] bArr = this.buf;
            bArr[i] = (byte) (bArr[i] ^ lu[i]);
        }
        this.cipher.processBlock(this.buf, 0, this.mac, 0);
        System.arraycopy(this.mac, 0, out, outOff, this.macSize);
        reset();
        return this.macSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        for (int i = 0; i < this.buf.length; i++) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.cipher.reset();
    }
}