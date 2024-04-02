package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.modes.gcm.GCMMultiplier;
import org.spongycastle.crypto.modes.gcm.Tables8kGCMMultiplier;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.util.Pack;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class GCMBlockCipher implements AEADBlockCipher {
    private static final int BLOCK_SIZE = 16;
    private static final byte[] ZEROES = new byte[16];
    private byte[] A;
    private byte[] H;
    private byte[] J0;
    private byte[] S;
    private byte[] bufBlock;
    private int bufOff;
    private BlockCipher cipher;
    private byte[] counter;
    private boolean forEncryption;
    private byte[] initS;
    private byte[] macBlock;
    private int macSize;
    private GCMMultiplier multiplier;
    private byte[] nonce;
    private long totalLength;

    public GCMBlockCipher(BlockCipher c) {
        this(c, null);
    }

    public GCMBlockCipher(BlockCipher c, GCMMultiplier m) {
        if (c.getBlockSize() != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
        m = m == null ? new Tables8kGCMMultiplier() : m;
        this.cipher = c;
        this.multiplier = m;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCM";
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        KeyParameter keyParam;
        this.forEncryption = forEncryption;
        this.macBlock = null;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters) params;
            this.nonce = param.getNonce();
            this.A = param.getAssociatedText();
            int macSizeBits = param.getMacSize();
            if (macSizeBits < 96 || macSizeBits > 128 || macSizeBits % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSizeBits);
            }
            this.macSize = macSizeBits / 8;
            keyParam = param.getKey();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param2 = (ParametersWithIV) params;
            this.nonce = param2.getIV();
            this.A = null;
            this.macSize = 16;
            keyParam = (KeyParameter) param2.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to GCM");
        }
        int bufLength = forEncryption ? 16 : this.macSize + 16;
        this.bufBlock = new byte[bufLength];
        if (this.nonce == null || this.nonce.length < 1) {
            throw new IllegalArgumentException("IV must be at least 1 byte");
        }
        if (this.A == null) {
            this.A = new byte[0];
        }
        if (keyParam != null) {
            this.cipher.init(true, keyParam);
        }
        this.H = new byte[16];
        this.cipher.processBlock(ZEROES, 0, this.H, 0);
        this.multiplier.init(this.H);
        this.initS = gHASH(this.A);
        if (this.nonce.length == 12) {
            this.J0 = new byte[16];
            System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
            this.J0[15] = 1;
        } else {
            this.J0 = gHASH(this.nonce);
            byte[] X = new byte[16];
            packLength(this.nonce.length * 8, X, 8);
            xor(this.J0, X);
            this.multiplier.multiplyH(this.J0);
        }
        this.S = Arrays.clone(this.initS);
        this.counter = Arrays.clone(this.J0);
        this.bufOff = 0;
        this.totalLength = 0L;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public byte[] getMac() {
        return Arrays.clone(this.macBlock);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getOutputSize(int len) {
        return this.forEncryption ? this.bufOff + len + this.macSize : (this.bufOff + len) - this.macSize;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getUpdateOutputSize(int len) {
        return ((this.bufOff + len) / 16) * 16;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        return process(in, out, outOff);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        int resultLen = 0;
        for (int i = 0; i != len; i++) {
            byte[] bArr = this.bufBlock;
            int i2 = this.bufOff;
            this.bufOff = i2 + 1;
            bArr[i2] = in[inOff + i];
            if (this.bufOff == this.bufBlock.length) {
                gCTRBlock(this.bufBlock, 16, out, outOff + resultLen);
                if (!this.forEncryption) {
                    System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
                }
                this.bufOff = this.bufBlock.length - 16;
                resultLen += 16;
            }
        }
        return resultLen;
    }

    private int process(byte in, byte[] out, int outOff) throws DataLengthException {
        byte[] bArr = this.bufBlock;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
        if (this.bufOff == this.bufBlock.length) {
            gCTRBlock(this.bufBlock, 16, out, outOff);
            if (!this.forEncryption) {
                System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
            }
            this.bufOff = this.bufBlock.length - 16;
            return 16;
        }
        return 0;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int extra = this.bufOff;
        if (!this.forEncryption) {
            if (extra < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            extra -= this.macSize;
        }
        if (extra > 0) {
            byte[] tmp = new byte[16];
            System.arraycopy(this.bufBlock, 0, tmp, 0, extra);
            gCTRBlock(tmp, extra, out, outOff);
        }
        byte[] X = new byte[16];
        packLength(this.A.length * 8, X, 0);
        packLength(this.totalLength * 8, X, 8);
        xor(this.S, X);
        this.multiplier.multiplyH(this.S);
        byte[] tag = new byte[16];
        this.cipher.processBlock(this.J0, 0, tag, 0);
        xor(tag, this.S);
        int resultLen = extra;
        this.macBlock = new byte[this.macSize];
        System.arraycopy(tag, 0, this.macBlock, 0, this.macSize);
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, out, this.bufOff + outOff, this.macSize);
            resultLen += this.macSize;
        } else {
            byte[] msgMac = new byte[this.macSize];
            System.arraycopy(this.bufBlock, extra, msgMac, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.macBlock, msgMac)) {
                throw new InvalidCipherTextException("mac check in GCM failed");
            }
        }
        reset(false);
        return resultLen;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void reset() {
        reset(true);
    }

    private void reset(boolean clearMac) {
        this.S = Arrays.clone(this.initS);
        this.counter = Arrays.clone(this.J0);
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.bufBlock != null) {
            Arrays.fill(this.bufBlock, (byte) 0);
        }
        if (clearMac) {
            this.macBlock = null;
        }
        this.cipher.reset();
    }

    private void gCTRBlock(byte[] buf, int bufCount, byte[] out, int outOff) {
        byte[] hashBytes;
        for (int i = 15; i >= 12; i--) {
            byte b = (byte) ((this.counter[i] + 1) & 255);
            this.counter[i] = b;
            if (b != 0) {
                break;
            }
        }
        byte[] tmp = new byte[16];
        this.cipher.processBlock(this.counter, 0, tmp, 0);
        if (this.forEncryption) {
            System.arraycopy(ZEROES, bufCount, tmp, bufCount, 16 - bufCount);
            hashBytes = tmp;
        } else {
            hashBytes = buf;
        }
        for (int i2 = bufCount - 1; i2 >= 0; i2--) {
            tmp[i2] = (byte) (tmp[i2] ^ buf[i2]);
            out[outOff + i2] = tmp[i2];
        }
        xor(this.S, hashBytes);
        this.multiplier.multiplyH(this.S);
        this.totalLength += bufCount;
    }

    private byte[] gHASH(byte[] b) {
        byte[] Y = new byte[16];
        for (int pos = 0; pos < b.length; pos += 16) {
            byte[] X = new byte[16];
            int num = Math.min(b.length - pos, 16);
            System.arraycopy(b, pos, X, 0, num);
            xor(Y, X);
            this.multiplier.multiplyH(Y);
        }
        return Y;
    }

    private static void xor(byte[] block, byte[] val) {
        for (int i = 15; i >= 0; i--) {
            block[i] = (byte) (block[i] ^ val[i]);
        }
    }

    private static void packLength(long count, byte[] bs, int off) {
        Pack.intToBigEndian((int) (count >>> 32), bs, off);
        Pack.intToBigEndian((int) count, bs, off + 4);
    }
}