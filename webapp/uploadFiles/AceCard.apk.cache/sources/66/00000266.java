package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.auth.AuthScheme;
import ch.boye.httpclientandroidlib.client.AuthCache;
import java.util.HashMap;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicAuthCache implements AuthCache {
    private final HashMap<HttpHost, AuthScheme> map = new HashMap<>();

    protected HttpHost getKey(HttpHost host) {
        if (host.getPort() <= 0) {
            int port = host.getSchemeName().equalsIgnoreCase("https") ? 443 : 80;
            return new HttpHost(host.getHostName(), port, host.getSchemeName());
        }
        return host;
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthCache
    public void put(HttpHost host, AuthScheme authScheme) {
        if (host == null) {
            throw new IllegalArgumentException("HTTP host may not be null");
        }
        this.map.put(getKey(host), authScheme);
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthCache
    public AuthScheme get(HttpHost host) {
        if (host == null) {
            throw new IllegalArgumentException("HTTP host may not be null");
        }
        return this.map.get(getKey(host));
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthCache
    public void remove(HttpHost host) {
        if (host == null) {
            throw new IllegalArgumentException("HTTP host may not be null");
        }
        this.map.remove(getKey(host));
    }

    @Override // ch.boye.httpclientandroidlib.client.AuthCache
    public void clear() {
        this.map.clear();
    }

    public String toString() {
        return this.map.toString();
    }
}