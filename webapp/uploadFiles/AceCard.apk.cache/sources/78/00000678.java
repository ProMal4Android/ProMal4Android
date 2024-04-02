package org.spongycastle.crypto.macs;

import java.util.Hashtable;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.params.KeyParameter;

/* loaded from: classes.dex */
public class HMac implements Mac {
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private static Hashtable blockLengths = new Hashtable();
    private int blockLength;
    private Digest digest;
    private int digestSize;
    private byte[] inputPad;
    private byte[] outputPad;

    static {
        blockLengths.put("GOST3411", new Integer(32));
        blockLengths.put("MD2", new Integer(16));
        blockLengths.put("MD4", new Integer(64));
        blockLengths.put("MD5", new Integer(64));
        blockLengths.put("RIPEMD128", new Integer(64));
        blockLengths.put("RIPEMD160", new Integer(64));
        blockLengths.put("SHA-1", new Integer(64));
        blockLengths.put("SHA-224", new Integer(64));
        blockLengths.put("SHA-256", new Integer(64));
        blockLengths.put("SHA-384", new Integer(128));
        blockLengths.put("SHA-512", new Integer(128));
        blockLengths.put("Tiger", new Integer(64));
        blockLengths.put("Whirlpool", new Integer(64));
    }

    private static int getByteLength(Digest digest) {
        if (digest instanceof ExtendedDigest) {
            return ((ExtendedDigest) digest).getByteLength();
        }
        Integer b = (Integer) blockLengths.get(digest.getAlgorithmName());
        if (b == null) {
            throw new IllegalArgumentException("unknown digest passed: " + digest.getAlgorithmName());
        }
        return b.intValue();
    }

    public HMac(Digest digest) {
        this(digest, getByteLength(digest));
    }

    private HMac(Digest digest, int byteLength) {
        this.digest = digest;
        this.digestSize = digest.getDigestSize();
        this.blockLength = byteLength;
        this.inputPad = new byte[this.blockLength];
        this.outputPad = new byte[this.blockLength];
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/HMAC";
    }

    public Digest getUnderlyingDigest() {
        return this.digest;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) {
        this.digest.reset();
        byte[] key = ((KeyParameter) params).getKey();
        if (key.length > this.blockLength) {
            this.digest.update(key, 0, key.length);
            this.digest.doFinal(this.inputPad, 0);
            for (int i = this.digestSize; i < this.inputPad.length; i++) {
                this.inputPad[i] = 0;
            }
        } else {
            System.arraycopy(key, 0, this.inputPad, 0, key.length);
            for (int i2 = key.length; i2 < this.inputPad.length; i2++) {
                this.inputPad[i2] = 0;
            }
        }
        this.outputPad = new byte[this.inputPad.length];
        System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);
        for (int i3 = 0; i3 < this.inputPad.length; i3++) {
            byte[] bArr = this.inputPad;
            bArr[i3] = (byte) (bArr[i3] ^ IPAD);
        }
        for (int i4 = 0; i4 < this.outputPad.length; i4++) {
            byte[] bArr2 = this.outputPad;
            bArr2[i4] = (byte) (bArr2[i4] ^ OPAD);
        }
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.digestSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) {
        this.digest.update(in);
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) {
        this.digest.update(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) {
        byte[] tmp = new byte[this.digestSize];
        this.digest.doFinal(tmp, 0);
        this.digest.update(this.outputPad, 0, this.outputPad.length);
        this.digest.update(tmp, 0, tmp.length);
        int len = this.digest.doFinal(out, outOff);
        reset();
        return len;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        this.digest.reset();
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }
}