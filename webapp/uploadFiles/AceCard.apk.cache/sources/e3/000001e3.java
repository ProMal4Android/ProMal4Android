package ch.boye.httpclientandroidlib.conn;

import ch.boye.httpclientandroidlib.HttpClientConnection;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;

/* loaded from: classes.dex */
public interface ManagedClientConnection extends HttpClientConnection, HttpRoutedConnection, ConnectionReleaseTrigger {
    @Override // ch.boye.httpclientandroidlib.conn.HttpRoutedConnection
    HttpRoute getRoute();

    @Override // ch.boye.httpclientandroidlib.conn.HttpRoutedConnection
    SSLSession getSSLSession();

    Object getState();

    boolean isMarkedReusable();

    @Override // ch.boye.httpclientandroidlib.conn.HttpRoutedConnection
    boolean isSecure();

    void layerProtocol(HttpContext httpContext, HttpParams httpParams) throws IOException;

    void markReusable();

    void open(HttpRoute httpRoute, HttpContext httpContext, HttpParams httpParams) throws IOException;

    void setIdleDuration(long j, TimeUnit timeUnit);

    void setState(Object obj);

    void tunnelProxy(HttpHost httpHost, boolean z, HttpParams httpParams) throws IOException;

    void tunnelTarget(boolean z, HttpParams httpParams) throws IOException;

    void unmarkReusable();
}