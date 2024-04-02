package ch.boye.httpclientandroidlib.impl.auth;

import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.auth.ChallengeState;
import ch.boye.httpclientandroidlib.auth.MalformedChallengeException;
import ch.boye.httpclientandroidlib.message.BasicHeaderValueParser;
import ch.boye.httpclientandroidlib.message.HeaderValueParser;
import ch.boye.httpclientandroidlib.message.ParserCursor;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@NotThreadSafe
/* loaded from: classes.dex */
public abstract class RFC2617Scheme extends AuthSchemeBase {
    private final Map<String, String> params;

    public RFC2617Scheme(ChallengeState challengeState) {
        super(challengeState);
        this.params = new HashMap();
    }

    public RFC2617Scheme() {
        this(null);
    }

    @Override // ch.boye.httpclientandroidlib.impl.auth.AuthSchemeBase
    protected void parseChallenge(CharArrayBuffer buffer, int pos, int len) throws MalformedChallengeException {
        HeaderValueParser parser = BasicHeaderValueParser.DEFAULT;
        ParserCursor cursor = new ParserCursor(pos, buffer.length());
        HeaderElement[] elements = parser.parseElements(buffer, cursor);
        if (elements.length == 0) {
            throw new MalformedChallengeException("Authentication challenge is empty");
        }
        this.params.clear();
        for (HeaderElement element : elements) {
            this.params.put(element.getName(), element.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, String> getParameters() {
        return this.params;
    }

    @Override // ch.boye.httpclientandroidlib.auth.AuthScheme
    public String getParameter(String name) {
        if (name == null) {
            return null;
        }
        return this.params.get(name.toLowerCase(Locale.ENGLISH));
    }

    @Override // ch.boye.httpclientandroidlib.auth.AuthScheme
    public String getRealm() {
        return getParameter("realm");
    }
}