package org.spongycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;

/* loaded from: classes.dex */
public class SRP6Client {
    protected BigInteger A;
    protected BigInteger B;
    protected BigInteger N;
    protected BigInteger S;
    protected BigInteger a;
    protected Digest digest;
    protected BigInteger g;
    protected SecureRandom random;
    protected BigInteger u;
    protected BigInteger x;

    public void init(BigInteger N, BigInteger g, Digest digest, SecureRandom random) {
        this.N = N;
        this.g = g;
        this.digest = digest;
        this.random = random;
    }

    public BigInteger generateClientCredentials(byte[] salt, byte[] identity, byte[] password) {
        this.x = SRP6Util.calculateX(this.digest, this.N, salt, identity, password);
        this.a = selectPrivateValue();
        this.A = this.g.modPow(this.a, this.N);
        return this.A;
    }

    public BigInteger calculateSecret(BigInteger serverB) throws CryptoException {
        this.B = SRP6Util.validatePublicValue(this.N, serverB);
        this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
        this.S = calculateS();
        return this.S;
    }

    protected BigInteger selectPrivateValue() {
        return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
    }

    private BigInteger calculateS() {
        BigInteger k = SRP6Util.calculateK(this.digest, this.N, this.g);
        BigInteger exp = this.u.multiply(this.x).add(this.a);
        BigInteger tmp = this.g.modPow(this.x, this.N).multiply(k).mod(this.N);
        return this.B.subtract(tmp).mod(this.N).modPow(exp, this.N);
    }
}