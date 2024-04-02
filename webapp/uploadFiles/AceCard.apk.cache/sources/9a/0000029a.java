package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpHeaders;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.impl.cookie.DateUtils;
import ch.boye.httpclientandroidlib.message.BasicHeader;
import ch.boye.httpclientandroidlib.message.BasicHttpResponse;
import java.util.Date;

@Immutable
/* loaded from: classes.dex */
class CachedHttpResponseGenerator {
    private final CacheValidityPolicy validityStrategy;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedHttpResponseGenerator(CacheValidityPolicy validityStrategy) {
        this.validityStrategy = validityStrategy;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CachedHttpResponseGenerator() {
        this(new CacheValidityPolicy());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpResponse generateResponse(HttpCacheEntry entry) {
        Date now = new Date();
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, entry.getStatusCode(), entry.getReasonPhrase());
        response.setHeaders(entry.getAllHeaders());
        if (entry.getResource() != null) {
            HttpEntity entity = new CacheEntity(entry);
            addMissingContentLengthHeader(response, entity);
            response.setEntity(entity);
        }
        long age = this.validityStrategy.getCurrentAgeSecs(entry, now);
        if (age > 0) {
            if (age >= 2147483647L) {
                response.setHeader("Age", "2147483648");
            } else {
                response.setHeader("Age", "" + ((int) age));
            }
        }
        return response;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpResponse generateNotModifiedResponse(HttpCacheEntry entry) {
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, (int) HttpStatus.SC_NOT_MODIFIED, "Not Modified");
        Header dateHeader = entry.getFirstHeader("Date");
        if (dateHeader == null) {
            dateHeader = new BasicHeader("Date", DateUtils.formatDate(new Date()));
        }
        response.addHeader(dateHeader);
        Header etagHeader = entry.getFirstHeader("ETag");
        if (etagHeader != null) {
            response.addHeader(etagHeader);
        }
        Header contentLocationHeader = entry.getFirstHeader(HttpHeaders.CONTENT_LOCATION);
        if (contentLocationHeader != null) {
            response.addHeader(contentLocationHeader);
        }
        Header expiresHeader = entry.getFirstHeader("Expires");
        if (expiresHeader != null) {
            response.addHeader(expiresHeader);
        }
        Header cacheControlHeader = entry.getFirstHeader("Cache-Control");
        if (cacheControlHeader != null) {
            response.addHeader(cacheControlHeader);
        }
        Header varyHeader = entry.getFirstHeader("Vary");
        if (varyHeader != null) {
            response.addHeader(varyHeader);
        }
        return response;
    }

    private void addMissingContentLengthHeader(HttpResponse response, HttpEntity entity) {
        if (!transferEncodingIsPresent(response)) {
            Header contentLength = response.getFirstHeader("Content-Length");
            if (contentLength == null) {
                Header contentLength2 = new BasicHeader("Content-Length", Long.toString(entity.getContentLength()));
                response.setHeader(contentLength2);
            }
        }
    }

    private boolean transferEncodingIsPresent(HttpResponse response) {
        Header hdr = response.getFirstHeader("Transfer-Encoding");
        return hdr != null;
    }
}