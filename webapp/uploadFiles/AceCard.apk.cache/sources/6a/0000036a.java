package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class RequestExpectContinue implements HttpRequestInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpRequestInterceptor
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        HttpEntity entity;
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if ((request instanceof HttpEntityEnclosingRequest) && (entity = ((HttpEntityEnclosingRequest) request).getEntity()) != null && entity.getContentLength() != 0) {
            ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            if (HttpProtocolParams.useExpectContinue(request.getParams()) && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                request.addHeader("Expect", HTTP.EXPECT_CONTINUE);
            }
        }
    }
}