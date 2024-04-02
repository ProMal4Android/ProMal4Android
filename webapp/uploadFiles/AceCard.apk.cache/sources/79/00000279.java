package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.AuthScheme;
import ch.boye.httpclientandroidlib.auth.AuthState;
import ch.boye.httpclientandroidlib.auth.Credentials;
import ch.boye.httpclientandroidlib.client.UserTokenHandler;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.conn.HttpRoutedConnection;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.security.Principal;
import javax.net.ssl.SSLSession;

@Immutable
/* loaded from: classes.dex */
public class DefaultUserTokenHandler implements UserTokenHandler {
    @Override // ch.boye.httpclientandroidlib.client.UserTokenHandler
    public Object getUserToken(HttpContext context) {
        SSLSession sslsession;
        Principal userPrincipal = null;
        AuthState targetAuthState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
        if (targetAuthState != null && (userPrincipal = getAuthPrincipal(targetAuthState)) == null) {
            AuthState proxyAuthState = (AuthState) context.getAttribute(ClientContext.PROXY_AUTH_STATE);
            userPrincipal = getAuthPrincipal(proxyAuthState);
        }
        if (userPrincipal == null) {
            HttpRoutedConnection conn = (HttpRoutedConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
            if (conn.isOpen() && (sslsession = conn.getSSLSession()) != null) {
                Principal userPrincipal2 = sslsession.getLocalPrincipal();
                return userPrincipal2;
            }
            return userPrincipal;
        }
        return userPrincipal;
    }

    private static Principal getAuthPrincipal(AuthState authState) {
        Credentials creds;
        AuthScheme scheme = authState.getAuthScheme();
        if (scheme == null || !scheme.isComplete() || !scheme.isConnectionBased() || (creds = authState.getCredentials()) == null) {
            return null;
        }
        return creds.getUserPrincipal();
    }
}