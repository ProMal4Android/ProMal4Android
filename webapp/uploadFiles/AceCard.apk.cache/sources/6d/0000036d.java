package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseInterceptor;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class ResponseConnControl implements HttpResponseInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpResponseInterceptor
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        int status = response.getStatusLine().getStatusCode();
        if (status == 400 || status == 408 || status == 411 || status == 413 || status == 414 || status == 503 || status == 501) {
            response.setHeader("Connection", HTTP.CONN_CLOSE);
            return;
        }
        Header explicit = response.getFirstHeader("Connection");
        if (explicit == null || !HTTP.CONN_CLOSE.equalsIgnoreCase(explicit.getValue())) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
                if (entity.getContentLength() < 0 && (!entity.isChunked() || ver.lessEquals(HttpVersion.HTTP_1_0))) {
                    response.setHeader("Connection", HTTP.CONN_CLOSE);
                    return;
                }
            }
            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
            if (request != null) {
                Header header = request.getFirstHeader("Connection");
                if (header != null) {
                    response.setHeader("Connection", header.getValue());
                } else if (request.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                    response.setHeader("Connection", HTTP.CONN_CLOSE);
                }
            }
        }
    }
}