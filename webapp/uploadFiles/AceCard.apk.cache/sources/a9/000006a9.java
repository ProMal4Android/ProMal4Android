package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class DSAPublicKeyParameters extends DSAKeyParameters {
    private BigInteger y;

    public DSAPublicKeyParameters(BigInteger y, DSAParameters params) {
        super(false, params);
        this.y = y;
    }

    public BigInteger getY() {
        return this.y;
    }
}