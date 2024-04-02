package org.spongycastle.crypto;

import java.math.BigInteger;

/* loaded from: classes.dex */
public interface BasicAgreement {
    BigInteger calculateAgreement(CipherParameters cipherParameters);

    void init(CipherParameters cipherParameters);
}