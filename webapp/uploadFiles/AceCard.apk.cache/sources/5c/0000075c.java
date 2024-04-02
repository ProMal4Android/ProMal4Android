package org.spongycastle.math.ntru.polynomial;

import java.math.BigInteger;

/* loaded from: classes.dex */
public class Resultant {
    public BigInteger res;
    public BigIntPolynomial rho;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Resultant(BigIntPolynomial rho, BigInteger res) {
        this.rho = rho;
        this.res = res;
    }
}