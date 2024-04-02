package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class RequestUserAgent implements HttpRequestInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        String useragent;
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (!request.containsHeader("User-Agent") && (useragent = HttpProtocolParams.getUserAgent(request.getParams())) != null) {
            request.addHeader("User-Agent", useragent);
        }
    }
}