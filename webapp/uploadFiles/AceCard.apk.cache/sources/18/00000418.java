package org.spongycastle.asn1;

/* loaded from: classes.dex */
public class OIDTokenizer {
    private int index = 0;
    private String oid;

    public OIDTokenizer(String oid) {
        this.oid = oid;
    }

    public boolean hasMoreTokens() {
        return this.index != -1;
    }

    public String nextToken() {
        if (this.index == -1) {
            return null;
        }
        int end = this.oid.indexOf(46, this.index);
        if (end == -1) {
            String substring = this.oid.substring(this.index);
            this.index = -1;
            return substring;
        }
        String substring2 = this.oid.substring(this.index, end);
        this.index = end + 1;
        return substring2;
    }
}