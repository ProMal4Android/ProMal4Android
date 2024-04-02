package org.spongycastle.crypto.params;

import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class DSAValidationParameters {
    private int counter;
    private byte[] seed;

    public DSAValidationParameters(byte[] seed, int counter) {
        this.seed = seed;
        this.counter = counter;
    }

    public int getCounter() {
        return this.counter;
    }

    public byte[] getSeed() {
        return this.seed;
    }

    public int hashCode() {
        return this.counter ^ Arrays.hashCode(this.seed);
    }

    public boolean equals(Object o) {
        if (o instanceof DSAValidationParameters) {
            DSAValidationParameters other = (DSAValidationParameters) o;
            if (other.counter == this.counter) {
                return Arrays.areEqual(this.seed, other.seed);
            }
            return false;
        }
        return false;
    }
}