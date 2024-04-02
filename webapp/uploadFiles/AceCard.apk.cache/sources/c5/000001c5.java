package ch.boye.httpclientandroidlib.client.protocol;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseInterceptor;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.entity.DeflateDecompressingEntity;
import ch.boye.httpclientandroidlib.client.entity.GzipDecompressingEntity;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;
import java.util.Locale;

@Immutable
/* loaded from: classes.dex */
public class ResponseContentEncoding implements HttpResponseInterceptor {
    public static final String UNCOMPRESSED = "http.client.response.uncompressed";

    @Override // ch.boye.httpclientandroidlib.HttpResponseInterceptor
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        Header ceheader;
        HttpEntity entity = response.getEntity();
        if (entity != null && (ceheader = entity.getContentEncoding()) != null) {
            HeaderElement[] codecs = ceheader.getElements();
            int len$ = codecs.length;
            if (0 < len$) {
                HeaderElement codec = codecs[0];
                String codecname = codec.getName().toLowerCase(Locale.US);
                if ("gzip".equals(codecname) || "x-gzip".equals(codecname)) {
                    response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                    if (context != null) {
                        context.setAttribute(UNCOMPRESSED, true);
                    }
                } else if ("deflate".equals(codecname)) {
                    response.setEntity(new DeflateDecompressingEntity(response.getEntity()));
                    if (context != null) {
                        context.setAttribute(UNCOMPRESSED, true);
                    }
                } else if (!HTTP.IDENTITY_CODING.equals(codecname)) {
                    throw new HttpException("Unsupported Content-Coding: " + codec.getName());
                }
            }
        }
    }
}