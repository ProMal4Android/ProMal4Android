package org.spongycastle.crypto.agreement;

import java.math.BigInteger;
import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.MQVPrivateParameters;
import org.spongycastle.crypto.params.MQVPublicParameters;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
public class ECMQVBasicAgreement implements BasicAgreement {
    MQVPrivateParameters privParams;

    @Override // org.spongycastle.crypto.BasicAgreement
    public void init(CipherParameters key) {
        this.privParams = (MQVPrivateParameters) key;
    }

    @Override // org.spongycastle.crypto.BasicAgreement
    public BigInteger calculateAgreement(CipherParameters pubKey) {
        MQVPublicParameters pubParams = (MQVPublicParameters) pubKey;
        ECPrivateKeyParameters staticPrivateKey = this.privParams.getStaticPrivateKey();
        ECPoint agreement = calculateMqvAgreement(staticPrivateKey.getParameters(), staticPrivateKey, this.privParams.getEphemeralPrivateKey(), this.privParams.getEphemeralPublicKey(), pubParams.getStaticPublicKey(), pubParams.getEphemeralPublicKey());
        return agreement.getX().toBigInteger();
    }

    private ECPoint calculateMqvAgreement(ECDomainParameters parameters, ECPrivateKeyParameters d1U, ECPrivateKeyParameters d2U, ECPublicKeyParameters Q2U, ECPublicKeyParameters Q1V, ECPublicKeyParameters Q2V) {
        ECPoint q;
        BigInteger n = parameters.getN();
        int e = (n.bitLength() + 1) / 2;
        BigInteger powE = ECConstants.ONE.shiftLeft(e);
        if (Q2U == null) {
            q = parameters.getG().multiply(d2U.getD());
        } else {
            q = Q2U.getQ();
        }
        BigInteger x = q.getX().toBigInteger();
        BigInteger xBar = x.mod(powE);
        BigInteger Q2UBar = xBar.setBit(e);
        BigInteger s = d1U.getD().multiply(Q2UBar).mod(n).add(d2U.getD()).mod(n);
        BigInteger xPrime = Q2V.getQ().getX().toBigInteger();
        BigInteger xPrimeBar = xPrime.mod(powE);
        BigInteger Q2VBar = xPrimeBar.setBit(e);
        BigInteger hs = parameters.getH().multiply(s).mod(n);
        ECPoint p = ECAlgorithms.sumOfTwoMultiplies(Q1V.getQ(), Q2VBar.multiply(hs).mod(n), Q2V.getQ(), hs);
        if (p.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for MQV");
        }
        return p;
    }
}