package ch.boye.httpclientandroidlib.client.protocol;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.auth.AuthSchemeRegistry;
import ch.boye.httpclientandroidlib.client.CookieStore;
import ch.boye.httpclientandroidlib.client.CredentialsProvider;
import ch.boye.httpclientandroidlib.cookie.CookieSpecRegistry;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

@NotThreadSafe
/* loaded from: classes.dex */
public class ClientContextConfigurer implements ClientContext {
    private final HttpContext context;

    public ClientContextConfigurer(HttpContext context) {
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        this.context = context;
    }

    public void setCookieSpecRegistry(CookieSpecRegistry registry) {
        this.context.setAttribute(ClientContext.COOKIESPEC_REGISTRY, registry);
    }

    public void setAuthSchemeRegistry(AuthSchemeRegistry registry) {
        this.context.setAttribute(ClientContext.AUTHSCHEME_REGISTRY, registry);
    }

    public void setCookieStore(CookieStore store) {
        this.context.setAttribute(ClientContext.COOKIE_STORE, store);
    }

    public void setCredentialsProvider(CredentialsProvider provider) {
        this.context.setAttribute(ClientContext.CREDS_PROVIDER, provider);
    }
}