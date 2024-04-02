package org.spongycastle.crypto.modes.gcm;

import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class BasicGCMMultiplier implements GCMMultiplier {
    private byte[] H;

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void init(byte[] H) {
        this.H = Arrays.clone(H);
    }

    @Override // org.spongycastle.crypto.modes.gcm.GCMMultiplier
    public void multiplyH(byte[] x) {
        GCMUtil.multiply(x, this.H);
    }
}