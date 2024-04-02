package ch.boye.httpclientandroidlib.client.protocol;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;
import java.util.Collection;

@Immutable
/* loaded from: classes.dex */
public class RequestDefaultHeaders implements HttpRequestInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        Collection<Header> defHeaders;
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        String method = request.getRequestLine().getMethod();
        if (!method.equalsIgnoreCase("CONNECT") && (defHeaders = (Collection) request.getParams().getParameter(ClientPNames.DEFAULT_HEADERS)) != null) {
            for (Header defHeader : defHeaders) {
                request.addHeader(defHeader);
            }
        }
    }
}