package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.impl.client.RequestWrapper;
import java.util.Map;

@Immutable
/* loaded from: classes.dex */
class ConditionalRequestBuilder {
    private static final HttpClientAndroidLog log = new HttpClientAndroidLog(ConditionalRequestBuilder.class);

    public HttpRequest buildConditionalRequest(HttpRequest request, HttpCacheEntry cacheEntry) throws ProtocolException {
        RequestWrapper wrapperRequest = new RequestWrapper(request);
        wrapperRequest.resetHeaders();
        Header eTag = cacheEntry.getFirstHeader("ETag");
        if (eTag != null) {
            wrapperRequest.setHeader("If-None-Match", eTag.getValue());
        }
        Header lastModified = cacheEntry.getFirstHeader("Last-Modified");
        if (lastModified != null) {
            wrapperRequest.setHeader("If-Modified-Since", lastModified.getValue());
        }
        boolean mustRevalidate = false;
        Header[] arr$ = cacheEntry.getHeaders("Cache-Control");
        for (Header h : arr$) {
            HeaderElement[] arr$2 = h.getElements();
            for (HeaderElement elt : arr$2) {
                if (HeaderConstants.CACHE_CONTROL_MUST_REVALIDATE.equalsIgnoreCase(elt.getName()) || HeaderConstants.CACHE_CONTROL_PROXY_REVALIDATE.equalsIgnoreCase(elt.getName())) {
                    mustRevalidate = true;
                    break;
                }
            }
        }
        if (mustRevalidate) {
            wrapperRequest.addHeader("Cache-Control", "max-age=0");
        }
        return wrapperRequest;
    }

    public HttpRequest buildConditionalRequestFromVariants(HttpRequest request, Map<String, Variant> variants) {
        try {
            RequestWrapper wrapperRequest = new RequestWrapper(request);
            wrapperRequest.resetHeaders();
            StringBuilder etags = new StringBuilder();
            boolean first = true;
            for (String etag : variants.keySet()) {
                if (!first) {
                    etags.append(",");
                }
                first = false;
                etags.append(etag);
            }
            wrapperRequest.setHeader("If-None-Match", etags.toString());
            return wrapperRequest;
        } catch (ProtocolException pe) {
            log.warn("unable to build conditional request", pe);
            return request;
        }
    }

    public HttpRequest buildUnconditionalRequest(HttpRequest request, HttpCacheEntry entry) {
        try {
            RequestWrapper wrapped = new RequestWrapper(request);
            wrapped.resetHeaders();
            wrapped.addHeader("Cache-Control", HeaderConstants.CACHE_CONTROL_NO_CACHE);
            wrapped.addHeader("Pragma", HeaderConstants.CACHE_CONTROL_NO_CACHE);
            wrapped.removeHeaders("If-Range");
            wrapped.removeHeaders("If-Match");
            wrapped.removeHeaders("If-None-Match");
            wrapped.removeHeaders("If-Unmodified-Since");
            wrapped.removeHeaders("If-Modified-Since");
            return wrapped;
        } catch (ProtocolException e) {
            log.warn("unable to build proper unconditional request", e);
            return request;
        }
    }
}