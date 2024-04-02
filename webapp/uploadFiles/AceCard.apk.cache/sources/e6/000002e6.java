package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.cookie.CookieAttributeHandler;
import ch.boye.httpclientandroidlib.cookie.CookieOrigin;
import ch.boye.httpclientandroidlib.cookie.CookieRestrictionViolationException;
import ch.boye.httpclientandroidlib.cookie.MalformedCookieException;
import ch.boye.httpclientandroidlib.cookie.SetCookie;

@Immutable
/* loaded from: classes.dex */
public class BasicPathHandler implements CookieAttributeHandler {
    @Override // ch.boye.httpclientandroidlib.cookie.CookieAttributeHandler
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        value = (value == null || value.trim().length() == 0) ? "/" : "/";
        cookie.setPath(value);
    }

    @Override // ch.boye.httpclientandroidlib.cookie.CookieAttributeHandler
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        if (!match(cookie, origin)) {
            throw new CookieRestrictionViolationException("Illegal path attribute \"" + cookie.getPath() + "\". Path of origin: \"" + origin.getPath() + "\"");
        }
    }

    @Override // ch.boye.httpclientandroidlib.cookie.CookieAttributeHandler
    public boolean match(Cookie cookie, CookieOrigin origin) {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        }
        String targetpath = origin.getPath();
        String topmostPath = cookie.getPath();
        if (topmostPath == null) {
            topmostPath = "/";
        }
        if (topmostPath.length() > 1 && topmostPath.endsWith("/")) {
            topmostPath = topmostPath.substring(0, topmostPath.length() - 1);
        }
        boolean match = targetpath.startsWith(topmostPath);
        if (!match || targetpath.length() == topmostPath.length() || topmostPath.endsWith("/")) {
            return match;
        }
        return targetpath.charAt(topmostPath.length()) == '/';
    }
}