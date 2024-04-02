package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class ECPrivateKeyParameters extends ECKeyParameters {
    BigInteger d;

    public ECPrivateKeyParameters(BigInteger d, ECDomainParameters params) {
        super(true, params);
        this.d = d;
    }

    public BigInteger getD() {
        return this.d;
    }
}