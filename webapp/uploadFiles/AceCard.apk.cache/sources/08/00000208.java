package ch.boye.httpclientandroidlib.conn.ssl;

import ch.boye.httpclientandroidlib.annotation.Immutable;

@Immutable
/* loaded from: classes.dex */
public class AllowAllHostnameVerifier extends AbstractVerifier {
    @Override // ch.boye.httpclientandroidlib.conn.ssl.X509HostnameVerifier
    public final void verify(String host, String[] cns, String[] subjectAlts) {
    }

    public final String toString() {
        return "ALLOW_ALL";
    }
}