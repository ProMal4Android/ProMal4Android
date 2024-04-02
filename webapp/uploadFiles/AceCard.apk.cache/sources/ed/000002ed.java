package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.cookie.CookieAttributeHandler;
import ch.boye.httpclientandroidlib.cookie.CookieOrigin;
import ch.boye.httpclientandroidlib.cookie.MalformedCookieException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@NotThreadSafe
/* loaded from: classes.dex */
public abstract class CookieSpecBase extends AbstractCookieSpec {
    /* JADX INFO: Access modifiers changed from: protected */
    public static String getDefaultPath(CookieOrigin origin) {
        String defaultPath = origin.getPath();
        int lastSlashIndex = defaultPath.lastIndexOf(47);
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                lastSlashIndex = 1;
            }
            return defaultPath.substring(0, lastSlashIndex);
        }
        return defaultPath;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String getDefaultDomain(CookieOrigin origin) {
        return origin.getHost();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Cookie> parse(HeaderElement[] elems, CookieOrigin origin) throws MalformedCookieException {
        List<Cookie> cookies = new ArrayList<>(elems.length);
        for (HeaderElement headerelement : elems) {
            String name = headerelement.getName();
            String value = headerelement.getValue();
            if (name == null || name.length() == 0) {
                throw new MalformedCookieException("Cookie name may not be empty");
            }
            BasicClientCookie cookie = new BasicClientCookie(name, value);
            cookie.setPath(getDefaultPath(origin));
            cookie.setDomain(getDefaultDomain(origin));
            NameValuePair[] attribs = headerelement.getParameters();
            for (int j = attribs.length - 1; j >= 0; j--) {
                NameValuePair attrib = attribs[j];
                String s = attrib.getName().toLowerCase(Locale.ENGLISH);
                cookie.setAttribute(s, attrib.getValue());
                CookieAttributeHandler handler = findAttribHandler(s);
                if (handler != null) {
                    handler.parse(cookie, attrib.getValue());
                }
            }
            cookies.add(cookie);
        }
        return cookies;
    }

    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpec
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        }
        for (CookieAttributeHandler handler : getAttribHandlers()) {
            handler.validate(cookie, origin);
        }
    }

    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpec
    public boolean match(Cookie cookie, CookieOrigin origin) {
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (origin == null) {
            throw new IllegalArgumentException("Cookie origin may not be null");
        }
        for (CookieAttributeHandler handler : getAttribHandlers()) {
            if (!handler.match(cookie, origin)) {
                return false;
            }
        }
        return true;
    }
}