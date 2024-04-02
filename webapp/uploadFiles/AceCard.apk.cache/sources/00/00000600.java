package org.spongycastle.crypto.agreement.srp;

import java.math.BigInteger;
import org.spongycastle.crypto.Digest;

/* loaded from: classes.dex */
public class SRP6VerifierGenerator {
    protected BigInteger N;
    protected Digest digest;
    protected BigInteger g;

    public void init(BigInteger N, BigInteger g, Digest digest) {
        this.N = N;
        this.g = g;
        this.digest = digest;
    }

    public BigInteger generateVerifier(byte[] salt, byte[] identity, byte[] password) {
        BigInteger x = SRP6Util.calculateX(this.digest, this.N, salt, identity, password);
        return this.g.modPow(x, this.N);
    }
}