package ch.boye.httpclientandroidlib.impl.auth;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.androidextra.Base64;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.auth.AuthenticationException;
import ch.boye.httpclientandroidlib.auth.ChallengeState;
import ch.boye.httpclientandroidlib.auth.Credentials;
import ch.boye.httpclientandroidlib.auth.MalformedChallengeException;
import ch.boye.httpclientandroidlib.auth.params.AuthParams;
import ch.boye.httpclientandroidlib.message.BufferedHeader;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import ch.boye.httpclientandroidlib.util.EncodingUtils;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicScheme extends RFC2617Scheme {
    private boolean complete;

    public BasicScheme(ChallengeState challengeState) {
        super(challengeState);
        this.complete = false;
    }

    public BasicScheme() {
        this(null);
    }

    @Override // ch.boye.httpclientandroidlib.auth.AuthScheme
    public String getSchemeName() {
        return "basic";
    }

    @Override // ch.boye.httpclientandroidlib.impl.auth.AuthSchemeBase, ch.boye.httpclientandroidlib.auth.AuthScheme
    public void processChallenge(Header header) throws MalformedChallengeException {
        super.processChallenge(header);
        this.complete = true;
    }

    @Override // ch.boye.httpclientandroidlib.auth.AuthScheme
    public boolean isComplete() {
        return this.complete;
    }

    @Override // ch.boye.httpclientandroidlib.auth.AuthScheme
    public boolean isConnectionBased() {
        return false;
    }

    @Override // ch.boye.httpclientandroidlib.auth.AuthScheme
    @Deprecated
    public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
        return authenticate(credentials, request, new BasicHttpContext());
    }

    @Override // ch.boye.httpclientandroidlib.impl.auth.AuthSchemeBase, ch.boye.httpclientandroidlib.auth.ContextAwareAuthScheme
    public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context) throws AuthenticationException {
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        String charset = AuthParams.getCredentialCharset(request.getParams());
        return authenticate(credentials, charset, isProxy());
    }

    public static Header authenticate(Credentials credentials, String charset, boolean proxy) {
        if (credentials == null) {
            throw new IllegalArgumentException("Credentials may not be null");
        }
        if (charset == null) {
            throw new IllegalArgumentException("charset may not be null");
        }
        StringBuilder tmp = new StringBuilder();
        tmp.append(credentials.getUserPrincipal().getName());
        tmp.append(":");
        tmp.append(credentials.getPassword() == null ? "null" : credentials.getPassword());
        byte[] base64password = Base64.encode(EncodingUtils.getBytes(tmp.toString(), charset), 2);
        CharArrayBuffer buffer = new CharArrayBuffer(32);
        if (proxy) {
            buffer.append("Proxy-Authorization");
        } else {
            buffer.append("Authorization");
        }
        buffer.append(": Basic ");
        buffer.append(base64password, 0, base64password.length);
        return new BufferedHeader(buffer);
    }
}