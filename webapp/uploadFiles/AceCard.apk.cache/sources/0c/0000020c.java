package ch.boye.httpclientandroidlib.conn.ssl;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import javax.net.ssl.SSLException;

@Immutable
/* loaded from: classes.dex */
public class StrictHostnameVerifier extends AbstractVerifier {
    @Override // ch.boye.httpclientandroidlib.conn.ssl.X509HostnameVerifier
    public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        verify(host, cns, subjectAlts, true);
    }

    public final String toString() {
        return "STRICT";
    }
}