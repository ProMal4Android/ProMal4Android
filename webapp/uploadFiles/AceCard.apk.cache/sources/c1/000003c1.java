package org.spongycastle.asn1;

import java.util.Date;

/* loaded from: classes.dex */
public class ASN1GeneralizedTime extends DERGeneralizedTime {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1GeneralizedTime(byte[] bytes) {
        super(bytes);
    }

    public ASN1GeneralizedTime(Date time) {
        super(time);
    }

    public ASN1GeneralizedTime(String time) {
        super(time);
    }
}