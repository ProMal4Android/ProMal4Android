package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.AuthScheme;
import ch.boye.httpclientandroidlib.auth.MalformedChallengeException;
import ch.boye.httpclientandroidlib.auth.params.AuthPNames;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.util.Map;
import java.util.Queue;

@Immutable
/* loaded from: classes.dex */
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl {
    @Override // ch.boye.httpclientandroidlib.impl.client.AuthenticationStrategyImpl, ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public /* bridge */ /* synthetic */ void authFailed(HttpHost x0, AuthScheme x1, HttpContext x2) {
        super.authFailed(x0, x1, x2);
    }

    @Override // ch.boye.httpclientandroidlib.impl.client.AuthenticationStrategyImpl, ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public /* bridge */ /* synthetic */ void authSucceeded(HttpHost x0, AuthScheme x1, HttpContext x2) {
        super.authSucceeded(x0, x1, x2);
    }

    @Override // ch.boye.httpclientandroidlib.impl.client.AuthenticationStrategyImpl, ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public /* bridge */ /* synthetic */ Map getChallenges(HttpHost x0, HttpResponse x1, HttpContext x2) throws MalformedChallengeException {
        return super.getChallenges(x0, x1, x2);
    }

    @Override // ch.boye.httpclientandroidlib.impl.client.AuthenticationStrategyImpl, ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public /* bridge */ /* synthetic */ boolean isAuthenticationRequested(HttpHost x0, HttpResponse x1, HttpContext x2) {
        return super.isAuthenticationRequested(x0, x1, x2);
    }

    @Override // ch.boye.httpclientandroidlib.impl.client.AuthenticationStrategyImpl, ch.boye.httpclientandroidlib.client.AuthenticationStrategy
    public /* bridge */ /* synthetic */ Queue select(Map x0, HttpHost x1, HttpResponse x2, HttpContext x3) throws MalformedChallengeException {
        return super.select(x0, x1, x2, x3);
    }

    public ProxyAuthenticationStrategy() {
        super(HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED, "Proxy-Authenticate", AuthPNames.PROXY_AUTH_PREF);
    }
}