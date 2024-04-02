package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseInterceptor;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class ResponseContent implements HttpResponseInterceptor {
    private final boolean overwrite;

    public ResponseContent() {
        this(false);
    }

    public ResponseContent(boolean overwrite) {
        this.overwrite = overwrite;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponseInterceptor
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (this.overwrite) {
            response.removeHeaders("Transfer-Encoding");
            response.removeHeaders("Content-Length");
        } else if (response.containsHeader("Transfer-Encoding")) {
            throw new ProtocolException("Transfer-encoding header already present");
        } else {
            if (response.containsHeader("Content-Length")) {
                throw new ProtocolException("Content-Length header already present");
            }
        }
        ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            long len = entity.getContentLength();
            if (entity.isChunked() && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                response.addHeader("Transfer-Encoding", HTTP.CHUNK_CODING);
            } else if (len >= 0) {
                response.addHeader("Content-Length", Long.toString(entity.getContentLength()));
            }
            if (entity.getContentType() != null && !response.containsHeader("Content-Type")) {
                response.addHeader(entity.getContentType());
            }
            if (entity.getContentEncoding() != null && !response.containsHeader("Content-Encoding")) {
                response.addHeader(entity.getContentEncoding());
                return;
            }
            return;
        }
        int status = response.getStatusLine().getStatusCode();
        if (status != 204 && status != 304 && status != 205) {
            response.addHeader("Content-Length", "0");
        }
    }
}