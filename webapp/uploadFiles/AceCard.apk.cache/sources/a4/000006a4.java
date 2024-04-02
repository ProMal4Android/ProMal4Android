package org.spongycastle.crypto.params;

import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class DHValidationParameters {
    private int counter;
    private byte[] seed;

    public DHValidationParameters(byte[] seed, int counter) {
        this.seed = seed;
        this.counter = counter;
    }

    public int getCounter() {
        return this.counter;
    }

    public byte[] getSeed() {
        return this.seed;
    }

    public boolean equals(Object o) {
        if (o instanceof DHValidationParameters) {
            DHValidationParameters other = (DHValidationParameters) o;
            if (other.counter == this.counter) {
                return Arrays.areEqual(this.seed, other.seed);
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return this.counter ^ Arrays.hashCode(this.seed);
    }
}