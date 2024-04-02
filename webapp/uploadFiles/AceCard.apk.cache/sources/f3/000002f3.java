package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.cookie.CookieSpec;
import ch.boye.httpclientandroidlib.cookie.CookieSpecFactory;
import ch.boye.httpclientandroidlib.params.HttpParams;

@Immutable
/* loaded from: classes.dex */
public class IgnoreSpecFactory implements CookieSpecFactory {
    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpecFactory
    public CookieSpec newInstance(HttpParams params) {
        return new IgnoreSpec();
    }
}