package org.spongycastle.crypto.signers;

import java.nio.ByteBuffer;
import org.spongycastle.crypto.Digest;

/* loaded from: classes.dex */
public class NTRUSignerPrng {
    private int counter = 0;
    private Digest hashAlg;
    private byte[] seed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NTRUSignerPrng(byte[] seed, Digest hashAlg) {
        this.seed = seed;
        this.hashAlg = hashAlg;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] nextBytes(int n) {
        ByteBuffer buf = ByteBuffer.allocate(n);
        while (buf.hasRemaining()) {
            ByteBuffer cbuf = ByteBuffer.allocate(this.seed.length + 4);
            cbuf.put(this.seed);
            cbuf.putInt(this.counter);
            byte[] array = cbuf.array();
            byte[] hash = new byte[this.hashAlg.getDigestSize()];
            this.hashAlg.update(array, 0, array.length);
            this.hashAlg.doFinal(hash, 0);
            if (buf.remaining() < hash.length) {
                buf.put(hash, 0, buf.remaining());
            } else {
                buf.put(hash);
            }
            this.counter++;
        }
        return buf.array();
    }
}