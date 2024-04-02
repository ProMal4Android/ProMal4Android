package ch.boye.httpclientandroidlib.conn.params;

import ch.boye.httpclientandroidlib.params.HttpAbstractParamBean;
import ch.boye.httpclientandroidlib.params.HttpParams;

@Deprecated
/* loaded from: classes.dex */
public class ConnConnectionParamBean extends HttpAbstractParamBean {
    public ConnConnectionParamBean(HttpParams params) {
        super(params);
    }

    public void setMaxStatusLineGarbage(int maxStatusLineGarbage) {
        this.params.setIntParameter(ConnConnectionPNames.MAX_STATUS_LINE_GARBAGE, maxStatusLineGarbage);
    }
}