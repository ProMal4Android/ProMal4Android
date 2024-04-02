package org.spongycastle.math.ntru.polynomial;

import java.math.BigInteger;
import org.spongycastle.math.ntru.euclid.BigIntEuclidean;

/* loaded from: classes.dex */
public class ModularResultant extends Resultant {
    BigInteger modulus;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ModularResultant(BigIntPolynomial rho, BigInteger res, BigInteger modulus) {
        super(rho, res);
        this.modulus = modulus;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ModularResultant combineRho(ModularResultant modRes1, ModularResultant modRes2) {
        BigInteger mod1 = modRes1.modulus;
        BigInteger mod2 = modRes2.modulus;
        BigInteger prod = mod1.multiply(mod2);
        BigIntEuclidean er = BigIntEuclidean.calculate(mod2, mod1);
        BigIntPolynomial rho1 = (BigIntPolynomial) modRes1.rho.clone();
        rho1.mult(er.x.multiply(mod2));
        BigIntPolynomial rho2 = (BigIntPolynomial) modRes2.rho.clone();
        rho2.mult(er.y.multiply(mod1));
        rho1.add(rho2);
        rho1.mod(prod);
        return new ModularResultant(rho1, null, prod);
    }
}