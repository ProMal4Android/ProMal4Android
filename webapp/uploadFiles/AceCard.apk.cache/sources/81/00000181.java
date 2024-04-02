package ch.boye.httpclientandroidlib.auth.params;

import ch.boye.httpclientandroidlib.params.HttpAbstractParamBean;
import ch.boye.httpclientandroidlib.params.HttpParams;

/* loaded from: classes.dex */
public class AuthParamBean extends HttpAbstractParamBean {
    public AuthParamBean(HttpParams params) {
        super(params);
    }

    public void setCredentialCharset(String charset) {
        AuthParams.setCredentialCharset(this.params, charset);
    }
}