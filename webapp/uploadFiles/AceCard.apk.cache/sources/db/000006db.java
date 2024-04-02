package org.spongycastle.crypto.prng;

import org.spongycastle.crypto.Digest;

/* loaded from: classes.dex */
public class DigestRandomGenerator implements RandomGenerator {
    private static long CYCLE_COUNT = 10;
    private Digest digest;
    private byte[] seed;
    private byte[] state;
    private long seedCounter = 1;
    private long stateCounter = 1;

    public DigestRandomGenerator(Digest digest) {
        this.digest = digest;
        this.seed = new byte[digest.getDigestSize()];
        this.state = new byte[digest.getDigestSize()];
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(byte[] inSeed) {
        synchronized (this) {
            digestUpdate(inSeed);
            digestUpdate(this.seed);
            digestDoFinal(this.seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(long rSeed) {
        synchronized (this) {
            digestAddCounter(rSeed);
            digestUpdate(this.seed);
            digestDoFinal(this.seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes) {
        nextBytes(bytes, 0, bytes.length);
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes, int start, int len) {
        int stateOff;
        synchronized (this) {
            int stateOff2 = 0;
            try {
                generateState();
                int end = start + len;
                for (int i = start; i != end; i++) {
                    if (stateOff2 == this.state.length) {
                        generateState();
                        stateOff = 0;
                    } else {
                        stateOff = stateOff2;
                    }
                    try {
                        stateOff2 = stateOff + 1;
                        bytes[i] = this.state[stateOff];
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    private void cycleSeed() {
        digestUpdate(this.seed);
        long j = this.seedCounter;
        this.seedCounter = 1 + j;
        digestAddCounter(j);
        digestDoFinal(this.seed);
    }

    private void generateState() {
        long j = this.stateCounter;
        this.stateCounter = 1 + j;
        digestAddCounter(j);
        digestUpdate(this.state);
        digestUpdate(this.seed);
        digestDoFinal(this.state);
        if (this.stateCounter % CYCLE_COUNT == 0) {
            cycleSeed();
        }
    }

    private void digestAddCounter(long seed) {
        for (int i = 0; i != 8; i++) {
            this.digest.update((byte) seed);
            seed >>>= 8;
        }
    }

    private void digestUpdate(byte[] inSeed) {
        this.digest.update(inSeed, 0, inSeed.length);
    }

    private void digestDoFinal(byte[] result) {
        this.digest.doFinal(result, 0);
    }
}