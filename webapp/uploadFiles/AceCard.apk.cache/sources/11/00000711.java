package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.security.SecureRandom;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class TlsBlockCipher implements TlsCipher {
    protected TlsClientContext context;
    protected BlockCipher decryptCipher;
    protected BlockCipher encryptCipher;
    protected TlsMac readMac;
    protected TlsMac writeMac;

    public TlsMac getWriteMac() {
        return this.writeMac;
    }

    public TlsMac getReadMac() {
        return this.readMac;
    }

    public TlsBlockCipher(TlsClientContext context, BlockCipher encryptCipher, BlockCipher decryptCipher, Digest writeDigest, Digest readDigest, int cipherKeySize) {
        this.context = context;
        this.encryptCipher = encryptCipher;
        this.decryptCipher = decryptCipher;
        int key_block_size = (cipherKeySize * 2) + writeDigest.getDigestSize() + readDigest.getDigestSize() + encryptCipher.getBlockSize() + decryptCipher.getBlockSize();
        byte[] key_block = TlsUtils.calculateKeyBlock(context, key_block_size);
        this.writeMac = new TlsMac(context, writeDigest, key_block, 0, writeDigest.getDigestSize());
        int offset = 0 + writeDigest.getDigestSize();
        this.readMac = new TlsMac(context, readDigest, key_block, offset, readDigest.getDigestSize());
        int offset2 = offset + readDigest.getDigestSize();
        initCipher(true, encryptCipher, key_block, cipherKeySize, offset2, offset2 + (cipherKeySize * 2));
        int offset3 = offset2 + cipherKeySize;
        initCipher(false, decryptCipher, key_block, cipherKeySize, offset3, offset3 + cipherKeySize + encryptCipher.getBlockSize());
    }

    protected void initCipher(boolean forEncryption, BlockCipher cipher, byte[] key_block, int key_size, int key_offset, int iv_offset) {
        KeyParameter key_parameter = new KeyParameter(key_block, key_offset, key_size);
        ParametersWithIV parameters_with_iv = new ParametersWithIV(key_parameter, key_block, iv_offset, cipher.getBlockSize());
        cipher.init(forEncryption, parameters_with_iv);
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] encodePlaintext(short type, byte[] plaintext, int offset, int len) {
        int blocksize = this.encryptCipher.getBlockSize();
        int minPaddingSize = blocksize - (((this.writeMac.getSize() + len) + 1) % blocksize);
        int paddingSize = minPaddingSize;
        boolean isTls = this.context.getServerVersion().getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        if (isTls) {
            int maxExtraPadBlocks = (255 - minPaddingSize) / blocksize;
            int actualExtraPadBlocks = chooseExtraPadBlocks(this.context.getSecureRandom(), maxExtraPadBlocks);
            paddingSize += actualExtraPadBlocks * blocksize;
        }
        int totalsize = this.writeMac.getSize() + len + paddingSize + 1;
        byte[] outbuf = new byte[totalsize];
        System.arraycopy(plaintext, offset, outbuf, 0, len);
        byte[] mac = this.writeMac.calculateMac(type, plaintext, offset, len);
        System.arraycopy(mac, 0, outbuf, len, mac.length);
        int paddoffset = len + mac.length;
        for (int i = 0; i <= paddingSize; i++) {
            outbuf[i + paddoffset] = (byte) paddingSize;
        }
        for (int i2 = 0; i2 < totalsize; i2 += blocksize) {
            this.encryptCipher.processBlock(outbuf, i2, outbuf, i2);
        }
        return outbuf;
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] decodeCiphertext(short type, byte[] ciphertext, int offset, int len) throws IOException {
        int minLength = this.readMac.getSize() + 1;
        int blocksize = this.decryptCipher.getBlockSize();
        boolean decrypterror = false;
        if (len < minLength) {
            throw new TlsFatalAlert((short) 50);
        }
        if (len % blocksize != 0) {
            throw new TlsFatalAlert((short) 21);
        }
        for (int i = 0; i < len; i += blocksize) {
            this.decryptCipher.processBlock(ciphertext, i + offset, ciphertext, i + offset);
        }
        int lastByteOffset = (offset + len) - 1;
        byte paddingsizebyte = ciphertext[lastByteOffset];
        int paddingsize = paddingsizebyte & 255;
        boolean isTls = this.context.getServerVersion().getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        int maxPaddingSize = len - minLength;
        if (!isTls) {
            maxPaddingSize = Math.min(maxPaddingSize, blocksize);
        }
        if (paddingsize > maxPaddingSize) {
            decrypterror = true;
            paddingsize = 0;
        } else if (isTls) {
            byte diff = 0;
            for (int i2 = lastByteOffset - paddingsize; i2 < lastByteOffset; i2++) {
                diff = (byte) ((ciphertext[i2] ^ paddingsizebyte) | diff);
            }
            if (diff != 0) {
                decrypterror = true;
                paddingsize = 0;
            }
        }
        int plaintextlength = (len - minLength) - paddingsize;
        byte[] calculatedMac = this.readMac.calculateMac(type, ciphertext, offset, plaintextlength);
        byte[] decryptedMac = new byte[calculatedMac.length];
        System.arraycopy(ciphertext, offset + plaintextlength, decryptedMac, 0, calculatedMac.length);
        if (!Arrays.constantTimeAreEqual(calculatedMac, decryptedMac)) {
            decrypterror = true;
        }
        if (decrypterror) {
            throw new TlsFatalAlert((short) 20);
        }
        byte[] plaintext = new byte[plaintextlength];
        System.arraycopy(ciphertext, offset, plaintext, 0, plaintextlength);
        return plaintext;
    }

    protected int chooseExtraPadBlocks(SecureRandom r, int max) {
        int x = r.nextInt();
        int n = lowestBitSet(x);
        return Math.min(n, max);
    }

    protected int lowestBitSet(int x) {
        if (x == 0) {
            return 32;
        }
        int n = 0;
        while ((x & 1) == 0) {
            n++;
            x >>= 1;
        }
        return n;
    }
}