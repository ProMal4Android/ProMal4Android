package org.spongycastle.crypto.params;

import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
public class ECPublicKeyParameters extends ECKeyParameters {
    ECPoint Q;

    public ECPublicKeyParameters(ECPoint Q, ECDomainParameters params) {
        super(false, params);
        this.Q = Q;
    }

    public ECPoint getQ() {
        return this.Q;
    }
}