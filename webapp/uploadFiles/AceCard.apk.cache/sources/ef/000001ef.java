package ch.boye.httpclientandroidlib.conn.params;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.params.HttpAbstractParamBean;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.net.InetAddress;

@NotThreadSafe
/* loaded from: classes.dex */
public class ConnRouteParamBean extends HttpAbstractParamBean {
    public ConnRouteParamBean(HttpParams params) {
        super(params);
    }

    public void setDefaultProxy(HttpHost defaultProxy) {
        this.params.setParameter(ConnRoutePNames.DEFAULT_PROXY, defaultProxy);
    }

    public void setLocalAddress(InetAddress address) {
        this.params.setParameter(ConnRoutePNames.LOCAL_ADDRESS, address);
    }

    public void setForcedRoute(HttpRoute route) {
        this.params.setParameter(ConnRoutePNames.FORCED_ROUTE, route);
    }
}