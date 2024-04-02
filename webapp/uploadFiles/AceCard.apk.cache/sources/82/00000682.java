package org.spongycastle.crypto.modes;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.macs.CMac;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class EAXBlockCipher implements AEADBlockCipher {
    private static final byte cTAG = 2;
    private static final byte hTAG = 1;
    private static final byte nTAG = 0;
    private byte[] associatedTextMac;
    private int blockSize;
    private byte[] bufBlock;
    private int bufOff;
    private SICBlockCipher cipher;
    private boolean forEncryption;
    private Mac mac;
    private byte[] macBlock;
    private int macSize;
    private byte[] nonceMac;

    public EAXBlockCipher(BlockCipher cipher) {
        this.blockSize = cipher.getBlockSize();
        this.mac = new CMac(cipher);
        this.macBlock = new byte[this.blockSize];
        this.bufBlock = new byte[this.blockSize * 2];
        this.associatedTextMac = new byte[this.mac.getMacSize()];
        this.nonceMac = new byte[this.mac.getMacSize()];
        this.cipher = new SICBlockCipher(cipher);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "/EAX";
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public BlockCipher getUnderlyingCipher() {
        return this.cipher.getUnderlyingCipher();
    }

    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        byte[] nonce;
        byte[] associatedText;
        CipherParameters keyParam;
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters) params;
            nonce = param.getNonce();
            associatedText = param.getAssociatedText();
            this.macSize = param.getMacSize() / 8;
            keyParam = param.getKey();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param2 = (ParametersWithIV) params;
            nonce = param2.getIV();
            associatedText = new byte[0];
            this.macSize = this.mac.getMacSize() / 2;
            keyParam = param2.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to EAX");
        }
        byte[] tag = new byte[this.blockSize];
        this.mac.init(keyParam);
        tag[this.blockSize - 1] = 1;
        this.mac.update(tag, 0, this.blockSize);
        this.mac.update(associatedText, 0, associatedText.length);
        this.mac.doFinal(this.associatedTextMac, 0);
        tag[this.blockSize - 1] = 0;
        this.mac.update(tag, 0, this.blockSize);
        this.mac.update(nonce, 0, nonce.length);
        this.mac.doFinal(this.nonceMac, 0);
        tag[this.blockSize - 1] = 2;
        this.mac.update(tag, 0, this.blockSize);
        this.cipher.init(true, new ParametersWithIV(keyParam, this.nonceMac));
    }

    private void calculateMac() {
        byte[] outC = new byte[this.blockSize];
        this.mac.doFinal(outC, 0);
        for (int i = 0; i < this.macBlock.length; i++) {
            this.macBlock[i] = (byte) ((this.nonceMac[i] ^ this.associatedTextMac[i]) ^ outC[i]);
        }
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public void reset() {
        reset(true);
    }

    private void reset(boolean clearMac) {
        this.cipher.reset();
        this.mac.reset();
        this.bufOff = 0;
        Arrays.fill(this.bufBlock, (byte) 0);
        if (clearMac) {
            Arrays.fill(this.macBlock, (byte) 0);
        }
        byte[] tag = new byte[this.blockSize];
        tag[this.blockSize - 1] = 2;
        this.mac.update(tag, 0, this.blockSize);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        return process(in, out, outOff);
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        int resultLen = 0;
        for (int i = 0; i != len; i++) {
            resultLen += process(in[inOff + i], out, outOff + resultLen);
        }
        return resultLen;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int extra = this.bufOff;
        byte[] tmp = new byte[this.bufBlock.length];
        this.bufOff = 0;
        if (this.forEncryption) {
            this.cipher.processBlock(this.bufBlock, 0, tmp, 0);
            this.cipher.processBlock(this.bufBlock, this.blockSize, tmp, this.blockSize);
            System.arraycopy(tmp, 0, out, outOff, extra);
            this.mac.update(tmp, 0, extra);
            calculateMac();
            System.arraycopy(this.macBlock, 0, out, outOff + extra, this.macSize);
            reset(false);
            return this.macSize + extra;
        }
        if (extra > this.macSize) {
            this.mac.update(this.bufBlock, 0, extra - this.macSize);
            this.cipher.processBlock(this.bufBlock, 0, tmp, 0);
            this.cipher.processBlock(this.bufBlock, this.blockSize, tmp, this.blockSize);
            System.arraycopy(tmp, 0, out, outOff, extra - this.macSize);
        }
        calculateMac();
        if (!verifyMac(this.bufBlock, extra - this.macSize)) {
            throw new InvalidCipherTextException("mac check in EAX failed");
        }
        reset(false);
        return extra - this.macSize;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, this.macSize);
        return mac;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getUpdateOutputSize(int len) {
        return ((this.bufOff + len) / this.blockSize) * this.blockSize;
    }

    @Override // org.spongycastle.crypto.modes.AEADBlockCipher
    public int getOutputSize(int len) {
        return this.forEncryption ? this.bufOff + len + this.macSize : (this.bufOff + len) - this.macSize;
    }

    private int process(byte b, byte[] out, int outOff) {
        int size;
        byte[] bArr = this.bufBlock;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = b;
        if (this.bufOff == this.bufBlock.length) {
            if (this.forEncryption) {
                size = this.cipher.processBlock(this.bufBlock, 0, out, outOff);
                this.mac.update(out, outOff, this.blockSize);
            } else {
                this.mac.update(this.bufBlock, 0, this.blockSize);
                size = this.cipher.processBlock(this.bufBlock, 0, out, outOff);
            }
            this.bufOff = this.blockSize;
            System.arraycopy(this.bufBlock, this.blockSize, this.bufBlock, 0, this.blockSize);
            return size;
        }
        return 0;
    }

    private boolean verifyMac(byte[] mac, int off) {
        for (int i = 0; i < this.macSize; i++) {
            if (this.macBlock[i] != mac[off + i]) {
                return false;
            }
        }
        return true;
    }
}