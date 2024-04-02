package org.spongycastle.crypto.prng;

/* loaded from: classes.dex */
public class ReversedWindowGenerator implements RandomGenerator {
    private final RandomGenerator generator;
    private byte[] window;
    private int windowCount;

    public ReversedWindowGenerator(RandomGenerator generator, int windowSize) {
        if (generator == null) {
            throw new IllegalArgumentException("generator cannot be null");
        }
        if (windowSize < 2) {
            throw new IllegalArgumentException("windowSize must be at least 2");
        }
        this.generator = generator;
        this.window = new byte[windowSize];
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(byte[] seed) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void addSeedMaterial(long seed) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(seed);
        }
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes) {
        doNextBytes(bytes, 0, bytes.length);
    }

    @Override // org.spongycastle.crypto.prng.RandomGenerator
    public void nextBytes(byte[] bytes, int start, int len) {
        doNextBytes(bytes, start, len);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:17:0x0032 -> B:15:0x0030). Please submit an issue!!! */
    private void doNextBytes(byte[] bytes, int start, int len) {
        int done;
        int i;
        synchronized (this) {
            int done2 = 0;
            while (done2 < len) {
                try {
                    if (this.windowCount < 1) {
                        this.generator.nextBytes(this.window, 0, this.window.length);
                        this.windowCount = this.window.length;
                    }
                    done = done2 + 1;
                    i = start + done2;
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
                try {
                    byte[] bArr = this.window;
                    int i2 = this.windowCount - 1;
                    this.windowCount = i2;
                    bytes[i] = bArr[i2];
                    done2 = done;
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
        }
    }
}