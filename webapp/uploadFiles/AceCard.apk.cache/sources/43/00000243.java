package ch.boye.httpclientandroidlib.impl;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseFactory;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.ReasonPhraseCatalog;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.message.BasicHttpResponse;
import ch.boye.httpclientandroidlib.message.BasicStatusLine;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.util.Locale;

@Immutable
/* loaded from: classes.dex */
public class DefaultHttpResponseFactory implements HttpResponseFactory {
    protected final ReasonPhraseCatalog reasonCatalog;

    public DefaultHttpResponseFactory(ReasonPhraseCatalog catalog) {
        if (catalog == null) {
            throw new IllegalArgumentException("Reason phrase catalog must not be null.");
        }
        this.reasonCatalog = catalog;
    }

    public DefaultHttpResponseFactory() {
        this(EnglishReasonPhraseCatalog.INSTANCE);
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponseFactory
    public HttpResponse newHttpResponse(ProtocolVersion ver, int status, HttpContext context) {
        if (ver == null) {
            throw new IllegalArgumentException("HTTP version may not be null");
        }
        Locale loc = determineLocale(context);
        String reason = this.reasonCatalog.getReason(status, loc);
        StatusLine statusline = new BasicStatusLine(ver, status, reason);
        return new BasicHttpResponse(statusline, this.reasonCatalog, loc);
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponseFactory
    public HttpResponse newHttpResponse(StatusLine statusline, HttpContext context) {
        if (statusline == null) {
            throw new IllegalArgumentException("Status line may not be null");
        }
        Locale loc = determineLocale(context);
        return new BasicHttpResponse(statusline, this.reasonCatalog, loc);
    }

    protected Locale determineLocale(HttpContext context) {
        return Locale.getDefault();
    }
}