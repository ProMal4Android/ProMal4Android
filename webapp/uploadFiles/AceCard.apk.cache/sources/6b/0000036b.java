package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.HttpConnection;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpInetConnection;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.IOException;
import java.net.InetAddress;

@Immutable
/* loaded from: classes.dex */
public class RequestTargetHost implements HttpRequestInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
        String method = request.getRequestLine().getMethod();
        if ((!method.equalsIgnoreCase("CONNECT") || !ver.lessEquals(HttpVersion.HTTP_1_0)) && !request.containsHeader("Host")) {
            HttpHost targethost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            if (targethost == null) {
                HttpConnection conn = (HttpConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
                if (conn instanceof HttpInetConnection) {
                    InetAddress address = ((HttpInetConnection) conn).getRemoteAddress();
                    int port = ((HttpInetConnection) conn).getRemotePort();
                    if (address != null) {
                        targethost = new HttpHost(address.getHostName(), port);
                    }
                }
                if (targethost == null) {
                    if (!ver.lessEquals(HttpVersion.HTTP_1_0)) {
                        throw new ProtocolException("Target host missing");
                    }
                    return;
                }
            }
            request.addHeader("Host", targethost.toHostString());
        }
    }
}