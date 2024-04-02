package org.spongycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;

/* loaded from: classes.dex */
public class SRP6Server {
    protected BigInteger A;
    protected BigInteger B;
    protected BigInteger N;
    protected BigInteger S;
    protected BigInteger b;
    protected Digest digest;
    protected BigInteger g;
    protected SecureRandom random;
    protected BigInteger u;
    protected BigInteger v;

    public void init(BigInteger N, BigInteger g, BigInteger v, Digest digest, SecureRandom random) {
        this.N = N;
        this.g = g;
        this.v = v;
        this.random = random;
        this.digest = digest;
    }

    public BigInteger generateServerCredentials() {
        BigInteger k = SRP6Util.calculateK(this.digest, this.N, this.g);
        this.b = selectPrivateValue();
        this.B = k.multiply(this.v).mod(this.N).add(this.g.modPow(this.b, this.N)).mod(this.N);
        return this.B;
    }

    public BigInteger calculateSecret(BigInteger clientA) throws CryptoException {
        this.A = SRP6Util.validatePublicValue(this.N, clientA);
        this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
        this.S = calculateS();
        return this.S;
    }

    protected BigInteger selectPrivateValue() {
        return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
    }

    private BigInteger calculateS() {
        return this.v.modPow(this.u, this.N).multiply(this.A).mod(this.N).modPow(this.b, this.N);
    }
}