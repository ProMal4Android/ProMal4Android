package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.AuthOption;
import ch.boye.httpclientandroidlib.auth.AuthScheme;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.AuthenticationException;
import ch.boye.httpclientandroidlib.auth.Credentials;
import ch.boye.httpclientandroidlib.auth.MalformedChallengeException;
import ch.boye.httpclientandroidlib.client.AuthCache;
import ch.boye.httpclientandroidlib.client.AuthenticationHandler;
import ch.boye.httpclientandroidlib.client.AuthenticationStrategy;
import ch.boye.httpclientandroidlib.client.CredentialsProvider;
import ch.boye.httpclientandroidlib.client.params.AuthPolicy;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

@Immutable
@Deprecated
/* loaded from: classes.dex */
class AuthenticationStrategyAdaptor implements AuthenticationStrategy {
    private final AuthenticationHandler handler;
    public HttpClientAndroidLog log = new HttpClientAndroidLog(getClass());

    public AuthenticationStrategyAdaptor(AuthenticationHandler handler) {
        this.handler = handler;
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
        return this.handler.isAuthenticationRequested(response, context);
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        return this.handler.getChallenges(response, context);
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        if (challenges == null) {
            throw new IllegalArgumentException("Map of auth challenges may not be null");
        }
        if (authhost == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        Queue<AuthOption> options = new LinkedList<>();
        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
        if (credsProvider == null) {
            this.log.debug("Credentials provider not set in the context");
        } else {
            try {
                AuthScheme authScheme = this.handler.selectScheme(challenges, response, context);
                String id = authScheme.getSchemeName();
                Header challenge = challenges.get(id.toLowerCase(Locale.US));
                authScheme.processChallenge(challenge);
                AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
                Credentials credentials = credsProvider.getCredentials(authScope);
                if (credentials != null) {
                    options.add(new AuthOption(authScheme, credentials));
                }
            } catch (AuthenticationException ex) {
                if (this.log.isWarnEnabled()) {
                    this.log.warn(ex.getMessage(), ex);
                }
            }
        }
        return options;
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        AuthCache authCache = (AuthCache) context.getAttribute(ClientContext.AUTH_CACHE);
        if (isCachable(authScheme)) {
            if (authCache == null) {
                authCache = new BasicAuthCache();
                context.setAttribute(ClientContext.AUTH_CACHE, authCache);
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
            }
            authCache.put(authhost, authScheme);
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        AuthCache authCache = (AuthCache) context.getAttribute(ClientContext.AUTH_CACHE);
        if (authCache != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Removing from cache '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
            }
            authCache.remove(authhost);
        }
    }

    private boolean isCachable(AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        String schemeName = authScheme.getSchemeName();
        return schemeName.equalsIgnoreCase(AuthPolicy.BASIC) || schemeName.equalsIgnoreCase(AuthPolicy.DIGEST);
    }

    public AuthenticationHandler getHandler() {
        return this.handler;
    }
}