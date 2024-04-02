package org.spongycastle.math.ec;

import org.spongycastle.math.ec.ECPoint;

/* loaded from: classes.dex */
class WTauNafPreCompInfo implements PreCompInfo {
    private ECPoint.F2m[] preComp;

    /* JADX INFO: Access modifiers changed from: package-private */
    public WTauNafPreCompInfo(ECPoint.F2m[] preComp) {
        this.preComp = null;
        this.preComp = preComp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ECPoint.F2m[] getPreComp() {
        return this.preComp;
    }
}