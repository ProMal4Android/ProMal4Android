package org.spongycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.macs.CBCBlockCipherMac;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class CCMBlockCipher implements AEADBlockCipher {
    private byte[] associatedText;
    private int blockSize;
    private BlockCipher cipher;
    private ByteArrayOutputStream data = new ByteArrayOutputStream();
    private boolean forEncryption;
    private CipherParameters keyParam;
    private byte[] macBlock;
    private int macSize;
    private byte[] nonce;

    public CCMBlockCipher(BlockCipher c) {
        this.cipher = c;
        this.blockSize = c.getBlockSize();
        this.macBlock = new byte[this.blockSize];
        if (this.blockSize != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters) params;
            this.nonce = param.getNonce();
            this.associatedText = param.getAssociatedText();
            this.macSize = param.getMacSize() / 8;
            this.keyParam = param.getKey();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param2 = (ParametersWithIV) params;
            this.nonce = param2.getIV();
            this.associatedText = null;
            this.macSize = this.macBlock.length / 2;
            this.keyParam = param2.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to CCM");
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CCM";
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.data.write(in);
        return 0;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processBytes(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.data.write(in, inOff, inLen);
        return 0;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        byte[] text = this.data.toByteArray();
        byte[] enc = processPacket(text, 0, text.length);
        System.arraycopy(enc, 0, out, outOff, enc.length);
        reset();
        return enc.length;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void reset() {
        this.cipher.reset();
        this.data.reset();
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, mac.length);
        return mac;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getUpdateOutputSize(int len) {
        return 0;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getOutputSize(int len) {
        return this.forEncryption ? this.data.size() + len + this.macSize : (this.data.size() + len) - this.macSize;
    }

    public byte[] processPacket(byte[] in, int inOff, int inLen) throws IllegalStateException, InvalidCipherTextException {
        byte[] out;
        if (this.keyParam == null) {
            throw new IllegalStateException("CCM cipher unitialized.");
        }
        BlockCipher ctrCipher = new SICBlockCipher(this.cipher);
        byte[] iv = new byte[this.blockSize];
        iv[0] = (byte) (((15 - this.nonce.length) - 1) & 7);
        System.arraycopy(this.nonce, 0, iv, 1, this.nonce.length);
        ctrCipher.init(this.forEncryption, new ParametersWithIV(this.keyParam, iv));
        if (this.forEncryption) {
            int index = inOff;
            int outOff = 0;
            out = new byte[this.macSize + inLen];
            calculateMac(in, inOff, inLen, this.macBlock);
            ctrCipher.processBlock(this.macBlock, 0, this.macBlock, 0);
            while (index < inLen - this.blockSize) {
                ctrCipher.processBlock(in, index, out, outOff);
                outOff += this.blockSize;
                index += this.blockSize;
            }
            byte[] block = new byte[this.blockSize];
            System.arraycopy(in, index, block, 0, inLen - index);
            ctrCipher.processBlock(block, 0, block, 0);
            System.arraycopy(block, 0, out, outOff, inLen - index);
            int outOff2 = outOff + (inLen - index);
            System.arraycopy(this.macBlock, 0, out, outOff2, out.length - outOff2);
        } else {
            int index2 = inOff;
            int outOff3 = 0;
            out = new byte[inLen - this.macSize];
            System.arraycopy(in, (inOff + inLen) - this.macSize, this.macBlock, 0, this.macSize);
            ctrCipher.processBlock(this.macBlock, 0, this.macBlock, 0);
            for (int i = this.macSize; i != this.macBlock.length; i++) {
                this.macBlock[i] = 0;
            }
            while (outOff3 < out.length - this.blockSize) {
                ctrCipher.processBlock(in, index2, out, outOff3);
                outOff3 += this.blockSize;
                index2 += this.blockSize;
            }
            byte[] block2 = new byte[this.blockSize];
            System.arraycopy(in, index2, block2, 0, out.length - outOff3);
            ctrCipher.processBlock(block2, 0, block2, 0);
            System.arraycopy(block2, 0, out, outOff3, out.length - outOff3);
            byte[] calculatedMacBlock = new byte[this.blockSize];
            calculateMac(out, 0, out.length, calculatedMacBlock);
            if (!Arrays.constantTimeAreEqual(this.macBlock, calculatedMacBlock)) {
                throw new InvalidCipherTextException("mac check in CCM failed");
            }
        }
        return out;
    }

    private int calculateMac(byte[] data, int dataOff, int dataLen, byte[] macBlock) {
        int extra;
        Mac cMac = new CBCBlockCipherMac(this.cipher, this.macSize * 8);
        cMac.init(this.keyParam);
        byte[] b0 = new byte[16];
        if (hasAssociatedText()) {
            b0[0] = (byte) (b0[0] | 64);
        }
        b0[0] = (byte) (b0[0] | ((((cMac.getMacSize() - 2) / 2) & 7) << 3));
        b0[0] = (byte) (b0[0] | (((15 - this.nonce.length) - 1) & 7));
        System.arraycopy(this.nonce, 0, b0, 1, this.nonce.length);
        int q = dataLen;
        int count = 1;
        while (q > 0) {
            b0[b0.length - count] = (byte) (q & 255);
            q >>>= 8;
            count++;
        }
        cMac.update(b0, 0, b0.length);
        if (hasAssociatedText()) {
            if (this.associatedText.length < 65280) {
                cMac.update((byte) (this.associatedText.length >> 8));
                cMac.update((byte) this.associatedText.length);
                extra = 2;
            } else {
                cMac.update((byte) -1);
                cMac.update((byte) -2);
                cMac.update((byte) (this.associatedText.length >> 24));
                cMac.update((byte) (this.associatedText.length >> 16));
                cMac.update((byte) (this.associatedText.length >> 8));
                cMac.update((byte) this.associatedText.length);
                extra = 6;
            }
            cMac.update(this.associatedText, 0, this.associatedText.length);
            int extra2 = (this.associatedText.length + extra) % 16;
            if (extra2 != 0) {
                for (int i = 0; i != 16 - extra2; i++) {
                    cMac.update((byte) 0);
                }
            }
        }
        cMac.update(data, dataOff, dataLen);
        return cMac.doFinal(macBlock, 0);
    }

    private boolean hasAssociatedText() {
        return (this.associatedText == null || this.associatedText.length == 0) ? false : true;
    }
}