package org.spongycastle.crypto.params;

import java.security.SecureRandom;
import org.spongycastle.crypto.KeyGenerationParameters;

/* loaded from: classes.dex */
public class GOST3410KeyGenerationParameters extends KeyGenerationParameters {
    private GOST3410Parameters params;

    public GOST3410KeyGenerationParameters(SecureRandom random, GOST3410Parameters params) {
        super(random, params.getP().bitLength() - 1);
        this.params = params;
    }

    public GOST3410Parameters getParameters() {
        return this.params;
    }
}