package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.MalformedChallengeException;
import ch.boye.httpclientandroidlib.auth.params.AuthPNames;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.util.List;
import java.util.Map;

@Immutable
@Deprecated
/* loaded from: classes.dex */
public class DefaultTargetAuthenticationHandler extends AbstractAuthenticationHandler {
    @Override // ch.boye.httpclientandroidlib.client.AuthenticationHandler
    public boolean isAuthenticationRequested(HttpResponse response, HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        int status = response.getStatusLine().getStatusCode();
        return status == 401;
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationHandler
    public Map<String, Header> getChallenges(HttpResponse response, HttpContext context) throws MalformedChallengeException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        Header[] headers = response.getHeaders("WWW-Authenticate");
        return parseChallenges(headers);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.client.AbstractAuthenticationHandler
    public List<String> getAuthPreferences(HttpResponse response, HttpContext context) {
        List<String> authpref = (List) response.getParams().getParameter(AuthPNames.TARGET_AUTH_PREF);
        return authpref != null ? authpref : super.getAuthPreferences(response, context);
    }
}