package ch.boye.httpclientandroidlib.client.protocol;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHeaders;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class RequestAcceptEncoding implements HttpRequestInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (!request.containsHeader(HttpHeaders.ACCEPT_ENCODING)) {
            request.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate");
        }
    }
}