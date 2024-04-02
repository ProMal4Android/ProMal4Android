package org.spongycastle.crypto.params;

import java.math.BigInteger;
import org.spongycastle.crypto.CipherParameters;

/* loaded from: classes.dex */
public class ElGamalParameters implements CipherParameters {
    private BigInteger g;
    private int l;
    private BigInteger p;

    public ElGamalParameters(BigInteger p, BigInteger g) {
        this(p, g, 0);
    }

    public ElGamalParameters(BigInteger p, BigInteger g, int l) {
        this.g = g;
        this.p = p;
        this.l = l;
    }

    public BigInteger getP() {
        return this.p;
    }

    public BigInteger getG() {
        return this.g;
    }

    public int getL() {
        return this.l;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ElGamalParameters) {
            ElGamalParameters pm = (ElGamalParameters) obj;
            return pm.getP().equals(this.p) && pm.getG().equals(this.g) && pm.getL() == this.l;
        }
        return false;
    }

    public int hashCode() {
        return (getP().hashCode() ^ getG().hashCode()) + this.l;
    }
}