package ch.boye.httpclientandroidlib.auth.params;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HTTP;

@Immutable
/* loaded from: classes.dex */
public final class AuthParams {
    private AuthParams() {
    }

    public static String getCredentialCharset(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        String charset = (String) params.getParameter(AuthPNames.CREDENTIAL_CHARSET);
        if (charset == null) {
            return HTTP.DEF_PROTOCOL_CHARSET.name();
        }
        return charset;
    }

    public static void setCredentialCharset(HttpParams params, String charset) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        params.setParameter(AuthPNames.CREDENTIAL_CHARSET, charset);
    }
}