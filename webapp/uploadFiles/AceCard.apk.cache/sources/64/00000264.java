package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.FormattedHeader;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.AuthOption;
import ch.boye.httpclientandroidlib.auth.AuthScheme;
import ch.boye.httpclientandroidlib.auth.AuthSchemeRegistry;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.Credentials;
import ch.boye.httpclientandroidlib.auth.MalformedChallengeException;
import ch.boye.httpclientandroidlib.client.AuthCache;
import ch.boye.httpclientandroidlib.client.AuthenticationStrategy;
import ch.boye.httpclientandroidlib.client.CredentialsProvider;
import ch.boye.httpclientandroidlib.client.params.AuthPolicy;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

@Immutable
/* loaded from: classes.dex */
class AuthenticationStrategyImpl implements AuthenticationStrategy {
    private static final List<String> DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList(AuthPolicy.SPNEGO, AuthPolicy.KERBEROS, AuthPolicy.NTLM, AuthPolicy.DIGEST, AuthPolicy.BASIC));
    private final int challengeCode;
    private final String headerName;
    public HttpClientAndroidLog log = new HttpClientAndroidLog(getClass());
    private final String prefParamName;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AuthenticationStrategyImpl(int challengeCode, String headerName, String prefParamName) {
        this.challengeCode = challengeCode;
        this.headerName = headerName;
        this.prefParamName = prefParamName;
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        int status = response.getStatusLine().getStatusCode();
        return status == this.challengeCode;
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
        CharArrayBuffer buffer;
        int pos;
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        Header[] headers = response.getHeaders(this.headerName);
        Map<String, Header> map = new HashMap<>(headers.length);
        for (Header header : headers) {
            if (header instanceof FormattedHeader) {
                buffer = ((FormattedHeader) header).getBuffer();
                pos = ((FormattedHeader) header).getValuePos();
            } else {
                String s = header.getValue();
                if (s == null) {
                    throw new MalformedChallengeException("Header value is null");
                }
                buffer = new CharArrayBuffer(s.length());
                buffer.append(s);
                pos = 0;
            }
            while (pos < buffer.length() && HTTP.isWhitespace(buffer.charAt(pos))) {
                pos++;
            }
            int beginIndex = pos;
            while (pos < buffer.length() && !HTTP.isWhitespace(buffer.charAt(pos))) {
                pos++;
            }
            int endIndex = pos;
            map.put(buffer.substring(beginIndex, endIndex).toLowerCase(Locale.US), header);
        }
        return map;
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
        AuthSchemeRegistry registry = (AuthSchemeRegistry) context.getAttribute(ClientContext.AUTHSCHEME_REGISTRY);
        if (registry == null) {
            this.log.debug("Auth scheme registry not set in the context");
        } else {
            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
            if (credsProvider == null) {
                this.log.debug("Credentials provider not set in the context");
            } else {
                List<String> authPrefs = (List) response.getParams().getParameter(this.prefParamName);
                if (authPrefs == null) {
                    authPrefs = DEFAULT_SCHEME_PRIORITY;
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Authentication schemes in the order of preference: " + authPrefs);
                }
                for (String id : authPrefs) {
                    Header challenge = challenges.get(id.toLowerCase(Locale.US));
                    if (challenge != null) {
                        try {
                            AuthScheme authScheme = registry.getAuthScheme(id, response.getParams());
                            authScheme.processChallenge(challenge);
                            AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
                            Credentials credentials = credsProvider.getCredentials(authScope);
                            if (credentials != null) {
                                options.add(new AuthOption(authScheme, credentials));
                            }
                        } catch (IllegalStateException e) {
                            if (this.log.isWarnEnabled()) {
                                this.log.warn("Authentication scheme " + id + " not supported");
                            }
                        }
                    } else if (this.log.isDebugEnabled()) {
                        this.log.debug("Challenge for " + id + " authentication scheme not available");
                    }
                }
            }
        }
        return options;
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        if (authhost == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        if (authScheme == null) {
            throw new IllegalArgumentException("Auth scheme may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        if (isCachable(authScheme)) {
            AuthCache authCache = (AuthCache) context.getAttribute(ClientContext.AUTH_CACHE);
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

    protected boolean isCachable(AuthScheme authScheme) {
        if (authScheme == null || !authScheme.isComplete()) {
            return false;
        }
        String schemeName = authScheme.getSchemeName();
        return schemeName.equalsIgnoreCase(AuthPolicy.BASIC) || schemeName.equalsIgnoreCase(AuthPolicy.DIGEST);
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
        if (authhost == null) {
            throw new IllegalArgumentException("Host may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        AuthCache authCache = (AuthCache) context.getAttribute(ClientContext.AUTH_CACHE);
        if (authCache != null) {
            if (this.log.isDebugEnabled()) {
                this.log.debug("Clearing cached auth scheme for " + authhost);
            }
            authCache.remove(authhost);
        }
    }
}