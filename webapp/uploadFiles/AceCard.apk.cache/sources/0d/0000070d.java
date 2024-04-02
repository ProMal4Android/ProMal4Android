package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class SSL3Mac implements Mac {
    private Digest digest;
    private byte[] ipad;
    private byte[] opad;
    private byte[] secret;
    private static final byte IPAD = 54;
    static final byte[] MD5_IPAD = genPad(IPAD, 48);
    private static final byte OPAD = 92;
    static final byte[] MD5_OPAD = genPad(OPAD, 48);
    static final byte[] SHA1_IPAD = genPad(IPAD, 40);
    static final byte[] SHA1_OPAD = genPad(OPAD, 40);

    public SSL3Mac(Digest digest) {
        this.digest = digest;
        if (digest.getDigestSize() == 20) {
            this.ipad = SHA1_IPAD;
            this.opad = SHA1_OPAD;
            return;
        }
        this.ipad = MD5_IPAD;
        this.opad = MD5_OPAD;
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return this.digest.getAlgorithmName() + "/SSL3MAC";
    }

    public Digest getUnderlyingDigest() {
        return this.digest;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) {
        this.secret = Arrays.clone(((KeyParameter) params).getKey());
        reset();
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.digest.getDigestSize();
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
        byte[] tmp = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(tmp, 0);
        this.digest.update(this.secret, 0, this.secret.length);
        this.digest.update(this.opad, 0, this.opad.length);
        this.digest.update(tmp, 0, tmp.length);
        int len = this.digest.doFinal(out, outOff);
        reset();
        return len;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        this.digest.reset();
        this.digest.update(this.secret, 0, this.secret.length);
        this.digest.update(this.ipad, 0, this.ipad.length);
    }

    private static byte[] genPad(byte b, int count) {
        byte[] padding = new byte[count];
        Arrays.fill(padding, b);
        return padding;
    }
}