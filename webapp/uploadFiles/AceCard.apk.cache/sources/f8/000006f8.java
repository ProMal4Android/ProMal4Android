package org.spongycastle.crypto.tls;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.MD5Digest;
import org.spongycastle.crypto.digests.SHA1Digest;

/* loaded from: classes.dex */
class CombinedHash implements Digest {
    protected TlsClientContext context;
    protected MD5Digest md5;
    protected SHA1Digest sha1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CombinedHash() {
        this.md5 = new MD5Digest();
        this.sha1 = new SHA1Digest();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CombinedHash(TlsClientContext context) {
        this.context = context;
        this.md5 = new MD5Digest();
        this.sha1 = new SHA1Digest();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CombinedHash(CombinedHash t) {
        this.context = t.context;
        this.md5 = new MD5Digest(t.md5);
        this.sha1 = new SHA1Digest(t.sha1);
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return this.md5.getAlgorithmName() + " and " + this.sha1.getAlgorithmName();
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 36;
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        this.md5.update(in);
        this.sha1.update(in);
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        this.md5.update(in, inOff, len);
        this.sha1.update(in, inOff, len);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        if (this.context != null) {
            boolean isTls = this.context.getServerVersion().getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
            if (!isTls) {
                ssl3Complete(this.md5, SSL3Mac.MD5_IPAD, SSL3Mac.MD5_OPAD);
                ssl3Complete(this.sha1, SSL3Mac.SHA1_IPAD, SSL3Mac.SHA1_OPAD);
            }
        }
        int i1 = this.md5.doFinal(out, outOff);
        int i2 = this.sha1.doFinal(out, outOff + 16);
        return i1 + i2;
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this.md5.reset();
        this.sha1.reset();
    }

    protected void ssl3Complete(Digest d, byte[] ipad, byte[] opad) {
        byte[] secret = this.context.getSecurityParameters().masterSecret;
        d.update(secret, 0, secret.length);
        d.update(ipad, 0, ipad.length);
        byte[] tmp = new byte[d.getDigestSize()];
        d.doFinal(tmp, 0);
        d.update(secret, 0, secret.length);
        d.update(opad, 0, opad.length);
        d.update(tmp, 0, tmp.length);
    }
}