package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.cookie.CookieOrigin;
import ch.boye.httpclientandroidlib.cookie.MalformedCookieException;
import java.util.Collections;
import java.util.List;

@NotThreadSafe
/* loaded from: classes.dex */
public class IgnoreSpec extends CookieSpecBase {
    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpec
    public int getVersion() {
        return 0;
    }

    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpec
    public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException {
        return Collections.emptyList();
    }

    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpec
    public List<Header> formatCookies(List<Cookie> cookies) {
        return Collections.emptyList();
    }

    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpec
    public Header getVersionHeader() {
        return null;
    }
}