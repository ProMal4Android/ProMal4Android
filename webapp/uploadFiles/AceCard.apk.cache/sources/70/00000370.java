package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseInterceptor;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class ResponseServer implements HttpResponseInterceptor {
    @Override // ch.boye.httpclientandroidlib.HttpResponseInterceptor
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        String s;
        if (response == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (!response.containsHeader("Server") && (s = (String) response.getParams().getParameter(CoreProtocolPNames.ORIGIN_SERVER)) != null) {
            response.addHeader("Server", s);
        }
    }
}