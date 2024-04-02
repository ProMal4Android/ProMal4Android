package org.spongycastle.math.ec;

import java.math.BigInteger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface ECMultiplier {
    ECPoint multiply(ECPoint eCPoint, BigInteger bigInteger, PreCompInfo preCompInfo);
}