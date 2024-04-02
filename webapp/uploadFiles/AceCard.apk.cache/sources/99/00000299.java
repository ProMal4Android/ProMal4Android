package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;

@Immutable
/* loaded from: classes.dex */
class CacheableRequestPolicy {
    public HttpClientAndroidLog log = new HttpClientAndroidLog(getClass());

    public boolean isServableFromCache(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        ProtocolVersion pv = request.getRequestLine().getProtocolVersion();
        if (HttpVersion.HTTP_1_1.compareToVersion(pv) != 0) {
            this.log.trace("non-HTTP/1.1 request was not serveable from cache");
            return false;
        } else if (!method.equals("GET")) {
            this.log.trace("non-GET request was not serveable from cache");
            return false;
        } else if (request.getHeaders("Pragma").length > 0) {
            this.log.trace("request with Pragma header was not serveable from cache");
            return false;
        } else {
            Header[] cacheControlHeaders = request.getHeaders("Cache-Control");
            for (Header cacheControl : cacheControlHeaders) {
                HeaderElement[] arr$ = cacheControl.getElements();
                for (HeaderElement cacheControlElement : arr$) {
                    if (HeaderConstants.CACHE_CONTROL_NO_STORE.equalsIgnoreCase(cacheControlElement.getName())) {
                        this.log.trace("Request with no-store was not serveable from cache");
                        return false;
                    } else if (HeaderConstants.CACHE_CONTROL_NO_CACHE.equalsIgnoreCase(cacheControlElement.getName())) {
                        this.log.trace("Request with no-cache was not serveable from cache");
                        return false;
                    }
                }
            }
            this.log.trace("Request was serveable from cache");
            return true;
        }
    }
}