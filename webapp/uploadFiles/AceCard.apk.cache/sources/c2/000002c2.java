package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.conn.DnsResolver;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes.dex */
public class InMemoryDnsResolver implements DnsResolver {
    public HttpClientAndroidLog log = new HttpClientAndroidLog(InMemoryDnsResolver.class);
    private Map<String, InetAddress[]> dnsMap = new ConcurrentHashMap();

    public void add(String host, InetAddress... ips) {
        if (host == null) {
            throw new IllegalArgumentException("Host name may not be null");
        }
        if (ips == null) {
            throw new IllegalArgumentException("Array of IP addresses may not be null");
        }
        this.dnsMap.put(host, ips);
    }

    @Override // ch.boye.httpclientandroidlib.conn.DnsResolver
    public InetAddress[] resolve(String host) throws UnknownHostException {
        InetAddress[] resolvedAddresses = this.dnsMap.get(host);
        if (this.log.isInfoEnabled()) {
            this.log.info("Resolving " + host + " to " + Arrays.deepToString(resolvedAddresses));
        }
        if (resolvedAddresses == null) {
            throw new UnknownHostException(host + " cannot be resolved");
        }
        return resolvedAddresses;
    }
}