package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.conn.DnsResolver;
import java.net.InetAddress;
import java.net.UnknownHostException;

/* loaded from: classes.dex */
public class SystemDefaultDnsResolver implements DnsResolver {
    @Override // ch.boye.httpclientandroidlib.conn.DnsResolver
    public InetAddress[] resolve(String host) throws UnknownHostException {
        return InetAddress.getAllByName(host);
    }
}