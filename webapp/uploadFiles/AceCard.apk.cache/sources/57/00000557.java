package org.spongycastle.asn1.x500.style;

import org.spongycastle.asn1.x500.RDN;
import org.spongycastle.asn1.x500.X500Name;

/* loaded from: classes.dex */
public class BCStrictStyle extends BCStyle {
    @Override // org.spongycastle.asn1.x500.style.BCStyle, org.spongycastle.asn1.x500.X500NameStyle
    public boolean areEqual(X500Name name1, X500Name name2) {
        RDN[] rdns1 = name1.getRDNs();
        RDN[] rdns2 = name2.getRDNs();
        if (rdns1.length != rdns2.length) {
            return false;
        }
        for (int i = 0; i != rdns1.length; i++) {
            if (rdnAreEqual(rdns1[i], rdns2[i])) {
                return false;
            }
        }
        return true;
    }
}