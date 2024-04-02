package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class RequestConnControl implements HttpRequestInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        String method = request.getRequestLine().getMethod();
        if (!method.equalsIgnoreCase("CONNECT") && !request.containsHeader("Connection")) {
            request.addHeader("Connection", HTTP.CONN_KEEP_ALIVE);
        }
    }
}