package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.params.DESedeParameters;

/* loaded from: classes.dex */
public class DESedeKeyGenerator extends DESKeyGenerator {
    @Override // org.spongycastle.crypto.generators.DESKeyGenerator, org.spongycastle.crypto.CipherKeyGenerator
    public void init(KeyGenerationParameters param) {
        this.random = param.getRandom();
        this.strength = (param.getStrength() + 7) / 8;
        if (this.strength == 0 || this.strength == 21) {
            this.strength = 24;
        } else if (this.strength == 14) {
            this.strength = 16;
        } else if (this.strength != 24 && this.strength != 16) {
            throw new IllegalArgumentException("DESede key must be 192 or 128 bits long.");
        }
    }

    @Override // org.spongycastle.crypto.generators.DESKeyGenerator, org.spongycastle.crypto.CipherKeyGenerator
    public byte[] generateKey() {
        byte[] newKey = new byte[this.strength];
        do {
            this.random.nextBytes(newKey);
            DESedeParameters.setOddParity(newKey);
        } while (DESedeParameters.isWeakKey(newKey, 0, newKey.length));
        return newKey;
    }
}