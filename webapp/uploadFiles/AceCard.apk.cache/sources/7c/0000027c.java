package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.auth.AuthProtocolState;
import ch.boye.httpclientandroidlib.auth.AuthState;
import ch.boye.httpclientandroidlib.client.AuthenticationStrategy;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

/* loaded from: classes.dex */
public class HttpAuthenticator {
    public HttpClientAndroidLog log;

    public HttpAuthenticator(HttpClientAndroidLog log) {
        this.log = log == null ? new HttpClientAndroidLog(getClass()) : log;
    }

    public HttpAuthenticator() {
        this(null);
    }

    public boolean isAuthenticationRequested(HttpHost host, HttpResponse response, AuthenticationStrategy authStrategy, AuthState authState, HttpContext context) {
        if (authStrategy.isAuthenticationRequested(host, response, context)) {
            this.log.debug("Authentication required");
            return true;
        }
        switch (authState.getState()) {
            case CHALLENGED:
            case HANDSHAKE:
                this.log.debug("Authentication succeeded");
                authState.setState(AuthProtocolState.SUCCESS);
                authStrategy.authSucceeded(host, authState.getAuthScheme(), context);
                break;
            case SUCCESS:
                break;
            default:
                authState.setState(AuthProtocolState.UNCHALLENGED);
                break;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0050 A[Catch: MalformedChallengeException -> 0x0086, TryCatch #0 {MalformedChallengeException -> 0x0086, blocks: (B:2:0x0000, B:4:0x0008, B:5:0x0024, B:7:0x002e, B:9:0x0037, B:10:0x0047, B:11:0x004a, B:13:0x0050, B:15:0x0056, B:17:0x005e, B:18:0x0076, B:20:0x0082, B:28:0x00b2, B:30:0x00ca, B:32:0x00dc, B:34:0x00ec, B:35:0x0105, B:36:0x010d), top: B:39:0x0000 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00ca A[Catch: MalformedChallengeException -> 0x0086, TryCatch #0 {MalformedChallengeException -> 0x0086, blocks: (B:2:0x0000, B:4:0x0008, B:5:0x0024, B:7:0x002e, B:9:0x0037, B:10:0x0047, B:11:0x004a, B:13:0x0050, B:15:0x0056, B:17:0x005e, B:18:0x0076, B:20:0x0082, B:28:0x00b2, B:30:0x00ca, B:32:0x00dc, B:34:0x00ec, B:35:0x0105, B:36:0x010d), top: B:39:0x0000 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean authenticate(ch.boye.httpclientandroidlib.HttpHost r10, ch.boye.httpclientandroidlib.HttpResponse r11, ch.boye.httpclientandroidlib.client.AuthenticationStrategy r12, ch.boye.httpclientandroidlib.auth.AuthState r13, ch.boye.httpclientandroidlib.protocol.HttpContext r14) {
        /*
            Method dump skipped, instructions count: 292
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.boye.httpclientandroidlib.impl.client.HttpAuthenticator.authenticate(ch.boye.httpclientandroidlib.HttpHost, ch.boye.httpclientandroidlib.HttpResponse, ch.boye.httpclientandroidlib.client.AuthenticationStrategy, ch.boye.httpclientandroidlib.auth.AuthState, ch.boye.httpclientandroidlib.protocol.HttpContext):boolean");
    }
}