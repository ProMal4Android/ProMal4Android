package ch.boye.httpclientandroidlib.client.protocol;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.AuthState;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class RequestTargetAuthentication extends RequestAuthenticationBase {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        String method = request.getRequestLine().getMethod();
        if (!method.equalsIgnoreCase("CONNECT") && !request.containsHeader("Authorization")) {
            AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
            if (authState == null) {
                this.log.debug("Target auth state not set in the context");
                return;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Target auth state: " + authState.getState());
            }
            process(authState, request, context);
        }
    }
}