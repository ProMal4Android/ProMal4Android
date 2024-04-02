package org.spongycastle.crypto.params;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class DHPublicKeyParameters extends DHKeyParameters {
    private BigInteger y;

    public DHPublicKeyParameters(BigInteger y, DHParameters params) {
        super(false, params);
        this.y = y;
    }

    public BigInteger getY() {
        return this.y;
    }

    @Override // org.spongycastle.crypto.params.DHKeyParameters
    public int hashCode() {
        return this.y.hashCode() ^ super.hashCode();
    }

    @Override // org.spongycastle.crypto.params.DHKeyParameters
    public boolean equals(Object obj) {
        if (obj instanceof DHPublicKeyParameters) {
            DHPublicKeyParameters other = (DHPublicKeyParameters) obj;
            return other.getY().equals(this.y) && super.equals(obj);
        }
        return false;
    }
}