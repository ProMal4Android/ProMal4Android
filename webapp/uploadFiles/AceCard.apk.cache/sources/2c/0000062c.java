package org.spongycastle.crypto.engines;

import java.math.BigInteger;
import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.params.IESParameters;
import org.spongycastle.crypto.params.IESWithCipherParameters;
import org.spongycastle.crypto.params.KDFParameters;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.BigIntegers;

/* loaded from: classes.dex */
public class IESEngine {
    BasicAgreement agree;
    BufferedBlockCipher cipher;
    boolean forEncryption;
    DerivationFunction kdf;
    Mac mac;
    byte[] macBuf;
    IESParameters param;
    CipherParameters privParam;
    CipherParameters pubParam;

    public IESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = null;
    }

    public IESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac, BufferedBlockCipher cipher) {
        this.agree = agree;
        this.kdf = kdf;
        this.mac = mac;
        this.macBuf = new byte[mac.getMacSize()];
        this.cipher = cipher;
    }

    public void init(boolean forEncryption, CipherParameters privParam, CipherParameters pubParam, CipherParameters param) {
        this.forEncryption = forEncryption;
        this.privParam = privParam;
        this.pubParam = pubParam;
        this.param = (IESParameters) param;
    }

    private byte[] decryptBlock(byte[] in_enc, int inOff, int inLen, byte[] z) throws InvalidCipherTextException {
        byte[] M;
        KeyParameter macKey;
        KDFParameters kParam = new KDFParameters(z, this.param.getDerivationV());
        int macKeySize = this.param.getMacKeySize();
        this.kdf.init(kParam);
        int inLen2 = inLen - this.mac.getMacSize();
        if (this.cipher == null) {
            byte[] buf = generateKdfBytes(kParam, (macKeySize / 8) + inLen2);
            M = new byte[inLen2];
            for (int i = 0; i != inLen2; i++) {
                M[i] = (byte) (in_enc[inOff + i] ^ buf[i]);
            }
            macKey = new KeyParameter(buf, inLen2, macKeySize / 8);
        } else {
            int cipherKeySize = ((IESWithCipherParameters) this.param).getCipherKeySize();
            byte[] buf2 = generateKdfBytes(kParam, (cipherKeySize / 8) + (macKeySize / 8));
            this.cipher.init(false, new KeyParameter(buf2, 0, cipherKeySize / 8));
            byte[] tmp = new byte[this.cipher.getOutputSize(inLen2)];
            int len = this.cipher.processBytes(in_enc, inOff, inLen2, tmp, 0);
            int len2 = len + this.cipher.doFinal(tmp, len);
            M = new byte[len2];
            System.arraycopy(tmp, 0, M, 0, len2);
            macKey = new KeyParameter(buf2, cipherKeySize / 8, macKeySize / 8);
        }
        byte[] macIV = this.param.getEncodingV();
        this.mac.init(macKey);
        this.mac.update(in_enc, inOff, inLen2);
        this.mac.update(macIV, 0, macIV.length);
        this.mac.doFinal(this.macBuf, 0);
        int inOff2 = inOff + inLen2;
        for (int t = 0; t < this.macBuf.length; t++) {
            if (this.macBuf[t] != in_enc[inOff2 + t]) {
                throw new InvalidCipherTextException("Mac codes failed to equal.");
            }
        }
        return M;
    }

    private byte[] encryptBlock(byte[] in, int inOff, int inLen, byte[] z) throws InvalidCipherTextException {
        byte[] C;
        int c_text_length;
        KeyParameter macKey;
        KDFParameters kParam = new KDFParameters(z, this.param.getDerivationV());
        int macKeySize = this.param.getMacKeySize();
        if (this.cipher == null) {
            byte[] buf = generateKdfBytes(kParam, (macKeySize / 8) + inLen);
            C = new byte[this.mac.getMacSize() + inLen];
            c_text_length = inLen;
            for (int i = 0; i != inLen; i++) {
                C[i] = (byte) (in[inOff + i] ^ buf[i]);
            }
            macKey = new KeyParameter(buf, inLen, macKeySize / 8);
        } else {
            int cipherKeySize = ((IESWithCipherParameters) this.param).getCipherKeySize();
            byte[] buf2 = generateKdfBytes(kParam, (cipherKeySize / 8) + (macKeySize / 8));
            this.cipher.init(true, new KeyParameter(buf2, 0, cipherKeySize / 8));
            int c_text_length2 = this.cipher.getOutputSize(inLen);
            byte[] tmp = new byte[c_text_length2];
            int len = this.cipher.processBytes(in, inOff, inLen, tmp, 0);
            int len2 = len + this.cipher.doFinal(tmp, len);
            C = new byte[this.mac.getMacSize() + len2];
            c_text_length = len2;
            System.arraycopy(tmp, 0, C, 0, len2);
            macKey = new KeyParameter(buf2, cipherKeySize / 8, macKeySize / 8);
        }
        byte[] macIV = this.param.getEncodingV();
        this.mac.init(macKey);
        this.mac.update(C, 0, c_text_length);
        this.mac.update(macIV, 0, macIV.length);
        this.mac.doFinal(C, c_text_length);
        return C;
    }

    private byte[] generateKdfBytes(KDFParameters kParam, int length) {
        byte[] buf = new byte[length];
        this.kdf.init(kParam);
        this.kdf.generateBytes(buf, 0, buf.length);
        return buf;
    }

    public byte[] processBlock(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        this.agree.init(this.privParam);
        BigInteger z = this.agree.calculateAgreement(this.pubParam);
        byte[] zBytes = BigIntegers.asUnsignedByteArray(z);
        return this.forEncryption ? encryptBlock(in, inOff, inLen, zBytes) : decryptBlock(in, inOff, inLen, zBytes);
    }
}