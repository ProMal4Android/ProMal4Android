package ch.boye.httpclientandroidlib.client;

import ch.boye.httpclientandroidlib.cookie.Cookie;
import java.util.Date;
import java.util.List;

/* loaded from: classes.dex */
public interface CookieStore {
    void addCookie(Cookie cookie);

    void clear();

    boolean clearExpired(Date date);

    List<Cookie> getCookies();
}