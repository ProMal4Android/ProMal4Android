package org.spongycastle.crypto.engines;

/* loaded from: classes.dex */
public class AESWrapEngine extends RFC3394WrapEngine {
    public AESWrapEngine() {
        super(new AESEngine());
    }
}