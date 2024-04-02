package org.spongycastle.math.ec;

/* loaded from: classes.dex */
class WNafPreCompInfo implements PreCompInfo {
    private ECPoint[] preComp = null;
    private ECPoint twiceP = null;

    /* JADX INFO: Access modifiers changed from: protected */
    public ECPoint[] getPreComp() {
        return this.preComp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPreComp(ECPoint[] preComp) {
        this.preComp = preComp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ECPoint getTwiceP() {
        return this.twiceP;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setTwiceP(ECPoint twiceThis) {
        this.twiceP = twiceThis;
    }
}