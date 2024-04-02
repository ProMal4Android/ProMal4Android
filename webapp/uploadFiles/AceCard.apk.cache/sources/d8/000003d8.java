package org.spongycastle.asn1;

import java.util.Date;

/* loaded from: classes.dex */
public class ASN1UTCTime extends DERUTCTime {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1UTCTime(byte[] bytes) {
        super(bytes);
    }

    public ASN1UTCTime(Date time) {
        super(time);
    }

    public ASN1UTCTime(String time) {
        super(time);
    }
}