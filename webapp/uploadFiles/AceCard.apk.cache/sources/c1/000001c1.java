package ch.boye.httpclientandroidlib.client.protocol;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.AuthState;
import ch.boye.httpclientandroidlib.conn.HttpRoutedConnection;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class RequestProxyAuthentication extends RequestAuthenticationBase {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        if (!request.containsHeader("Proxy-Authorization")) {
            HttpRoutedConnection conn = (HttpRoutedConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
            if (conn == null) {
                this.log.debug("HTTP connection not set in the context");
                return;
            }
            HttpRoute route = conn.getRoute();
            if (!route.isTunnelled()) {
                AuthState authState = (AuthState) context.getAttribute(ClientContext.PROXY_AUTH_STATE);
                if (authState == null) {
                    this.log.debug("Proxy auth state not set in the context");
                    return;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Proxy auth state: " + authState.getState());
                }
                process(authState, request, context);
            }
        }
    }
}