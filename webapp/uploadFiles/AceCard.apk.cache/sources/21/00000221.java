package ch.boye.httpclientandroidlib.cookie.params;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.params.HttpAbstractParamBean;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.util.Collection;

@NotThreadSafe
/* loaded from: classes.dex */
public class CookieSpecParamBean extends HttpAbstractParamBean {
    public CookieSpecParamBean(HttpParams params) {
        super(params);
    }

    public void setDatePatterns(Collection<String> patterns) {
        this.params.setParameter(CookieSpecPNames.DATE_PATTERNS, patterns);
    }

    public void setSingleHeader(boolean singleHeader) {
        this.params.setBooleanParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, singleHeader);
    }
}