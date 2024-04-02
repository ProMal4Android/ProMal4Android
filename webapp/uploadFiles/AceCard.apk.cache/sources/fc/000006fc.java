package org.spongycastle.crypto.tls;

import java.io.IOException;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.MD5Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA384Digest;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.engines.DESedeEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;

/* loaded from: classes.dex */
public class DefaultTlsCipherFactory implements TlsCipherFactory {
    @Override // org.spongycastle.crypto.tls.TlsCipherFactory
    public TlsCipher createCipher(TlsClientContext context, int encryptionAlgorithm, int digestAlgorithm) throws IOException {
        switch (encryptionAlgorithm) {
            case 7:
                return createDESedeCipher(context, 24, digestAlgorithm);
            case 8:
                return createAESCipher(context, 16, digestAlgorithm);
            case 9:
                return createAESCipher(context, 32, digestAlgorithm);
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    protected TlsCipher createAESCipher(TlsClientContext context, int cipherKeySize, int digestAlgorithm) throws IOException {
        return new TlsBlockCipher(context, createAESBlockCipher(), createAESBlockCipher(), createDigest(digestAlgorithm), createDigest(digestAlgorithm), cipherKeySize);
    }

    protected TlsCipher createDESedeCipher(TlsClientContext context, int cipherKeySize, int digestAlgorithm) throws IOException {
        return new TlsBlockCipher(context, createDESedeBlockCipher(), createDESedeBlockCipher(), createDigest(digestAlgorithm), createDigest(digestAlgorithm), cipherKeySize);
    }

    protected BlockCipher createAESBlockCipher() {
        return new CBCBlockCipher(new AESFastEngine());
    }

    protected BlockCipher createDESedeBlockCipher() {
        return new CBCBlockCipher(new DESedeEngine());
    }

    protected Digest createDigest(int digestAlgorithm) throws IOException {
        switch (digestAlgorithm) {
            case 1:
                return new MD5Digest();
            case 2:
                return new SHA1Digest();
            case 3:
                return new SHA256Digest();
            case 4:
                return new SHA384Digest();
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }
}