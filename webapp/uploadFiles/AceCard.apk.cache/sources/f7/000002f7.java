package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.cookie.CookieSpec;
import ch.boye.httpclientandroidlib.cookie.CookieSpecFactory;
import ch.boye.httpclientandroidlib.cookie.params.CookieSpecPNames;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.util.Collection;

@Immutable
/* loaded from: classes.dex */
public class NetscapeDraftSpecFactory implements CookieSpecFactory {
    @Override // ch.boye.httpclientandroidlib.cookie.CookieSpecFactory
    public CookieSpec newInstance(HttpParams params) {
        if (params != null) {
            String[] patterns = null;
            Collection<?> param = (Collection) params.getParameter(CookieSpecPNames.DATE_PATTERNS);
            if (param != null) {
                String[] patterns2 = new String[param.size()];
                patterns = (String[]) param.toArray(patterns2);
            }
            return new NetscapeDraftSpec(patterns);
        }
        return new NetscapeDraftSpec();
    }
}