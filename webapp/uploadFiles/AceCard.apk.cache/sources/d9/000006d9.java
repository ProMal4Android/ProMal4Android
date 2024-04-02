package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class RSAKeyParameters extends AsymmetricKeyParameter {
    private BigInteger exponent;
    private BigInteger modulus;

    public RSAKeyParameters(boolean isPrivate, BigInteger modulus, BigInteger exponent) {
        super(isPrivate);
        this.modulus = modulus;
        this.exponent = exponent;
    }

    public BigInteger getModulus() {
        return this.modulus;
    }

    public BigInteger getExponent() {
        return this.exponent;
    }
}