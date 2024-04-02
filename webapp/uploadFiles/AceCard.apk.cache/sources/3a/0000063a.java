package org.spongycastle.crypto.engines;

import java.security.SecureRandom;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.Wrapper;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.params.ParametersWithRandom;

/* loaded from: classes.dex */
public class RFC3211WrapEngine implements Wrapper {
    private CBCBlockCipher engine;
    private boolean forWrapping;
    private ParametersWithIV param;
    private SecureRandom rand;

    public RFC3211WrapEngine(BlockCipher engine) {
        this.engine = new CBCBlockCipher(engine);
    }

    @Override // org.spongycastle.crypto.Wrapper
    public void init(boolean forWrapping, CipherParameters param) {
        this.forWrapping = forWrapping;
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom p = (ParametersWithRandom) param;
            this.rand = p.getRandom();
            this.param = (ParametersWithIV) p.getParameters();
            return;
        }
        if (forWrapping) {
            this.rand = new SecureRandom();
        }
        this.param = (ParametersWithIV) param;
    }

    @Override // org.spongycastle.crypto.Wrapper
    public String getAlgorithmName() {
        return this.engine.getUnderlyingCipher().getAlgorithmName() + "/RFC3211Wrap";
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] wrap(byte[] in, int inOff, int inLen) {
        byte[] cekBlock;
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        this.engine.init(true, this.param);
        int blockSize = this.engine.getBlockSize();
        if (inLen + 4 < blockSize * 2) {
            cekBlock = new byte[blockSize * 2];
        } else {
            cekBlock = new byte[(inLen + 4) % blockSize == 0 ? inLen + 4 : (((inLen + 4) / blockSize) + 1) * blockSize];
        }
        cekBlock[0] = (byte) inLen;
        cekBlock[1] = (byte) (in[inOff] ^ (-1));
        cekBlock[2] = (byte) (in[inOff + 1] ^ (-1));
        cekBlock[3] = (byte) (in[inOff + 2] ^ (-1));
        System.arraycopy(in, inOff, cekBlock, 4, inLen);
        for (int i = inLen + 4; i < cekBlock.length; i++) {
            cekBlock[i] = (byte) this.rand.nextInt();
        }
        for (int i2 = 0; i2 < cekBlock.length; i2 += blockSize) {
            this.engine.processBlock(cekBlock, i2, cekBlock, i2);
        }
        for (int i3 = 0; i3 < cekBlock.length; i3 += blockSize) {
            this.engine.processBlock(cekBlock, i3, cekBlock, i3);
        }
        return cekBlock;
    }

    @Override // org.spongycastle.crypto.Wrapper
    public byte[] unwrap(byte[] in, int inOff, int inLen) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        int blockSize = this.engine.getBlockSize();
        if (inLen < blockSize * 2) {
            throw new InvalidCipherTextException("input too short");
        }
        byte[] cekBlock = new byte[inLen];
        byte[] iv = new byte[blockSize];
        System.arraycopy(in, inOff, cekBlock, 0, inLen);
        System.arraycopy(in, inOff, iv, 0, iv.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), iv));
        for (int i = blockSize; i < cekBlock.length; i += blockSize) {
            this.engine.processBlock(cekBlock, i, cekBlock, i);
        }
        System.arraycopy(cekBlock, cekBlock.length - iv.length, iv, 0, iv.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), iv));
        this.engine.processBlock(cekBlock, 0, cekBlock, 0);
        this.engine.init(false, this.param);
        for (int i2 = 0; i2 < cekBlock.length; i2 += blockSize) {
            this.engine.processBlock(cekBlock, i2, cekBlock, i2);
        }
        if ((cekBlock[0] & 255) > cekBlock.length - 4) {
            throw new InvalidCipherTextException("wrapped key corrupted");
        }
        byte[] key = new byte[cekBlock[0] & 255];
        System.arraycopy(cekBlock, 4, key, 0, cekBlock[0]);
        int nonEqual = 0;
        for (int i3 = 0; i3 != 3; i3++) {
            byte check = (byte) (cekBlock[i3 + 1] ^ (-1));
            nonEqual |= key[i3] ^ check;
        }
        if (nonEqual != 0) {
            throw new InvalidCipherTextException("wrapped key fails checksum");
        }
        return key;
    }
}