package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.ConnectionReuseStrategy;
import ch.boye.httpclientandroidlib.annotation.ThreadSafe;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoutePlanner;
import ch.boye.httpclientandroidlib.impl.DefaultConnectionReuseStrategy;
import ch.boye.httpclientandroidlib.impl.NoConnectionReuseStrategy;
import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import ch.boye.httpclientandroidlib.impl.conn.ProxySelectorRoutePlanner;
import ch.boye.httpclientandroidlib.impl.conn.SchemeRegistryFactory;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.net.ProxySelector;

@ThreadSafe
/* loaded from: classes.dex */
public class SystemDefaultHttpClient extends DefaultHttpClient {
    public SystemDefaultHttpClient(HttpParams params) {
        super(null, params);
    }

    public SystemDefaultHttpClient() {
        super(null, null);
    }

    @Override // ch.boye.httpclientandroidlib.impl.client.AbstractHttpClient
    protected ClientConnectionManager createClientConnectionManager() {
        PoolingClientConnectionManager connmgr = new PoolingClientConnectionManager(SchemeRegistryFactory.createSystemDefault());
        String s = System.getProperty("http.keepAlive", "true");
        if ("true".equalsIgnoreCase(s)) {
            String s2 = System.getProperty("http.maxConnections", "5");
            int max = Integer.parseInt(s2);
            connmgr.setDefaultMaxPerRoute(max);
            connmgr.setMaxTotal(max * 2);
        }
        return connmgr;
    }

    @Override // ch.boye.httpclientandroidlib.impl.client.AbstractHttpClient
    protected HttpRoutePlanner createHttpRoutePlanner() {
        return new ProxySelectorRoutePlanner(getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
    }

    @Override // ch.boye.httpclientandroidlib.impl.client.AbstractHttpClient
    protected ConnectionReuseStrategy createConnectionReuseStrategy() {
        String s = System.getProperty("http.keepAlive", "true");
        return "true".equalsIgnoreCase(s) ? new DefaultConnectionReuseStrategy() : new NoConnectionReuseStrategy();
    }
}