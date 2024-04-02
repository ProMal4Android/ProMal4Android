package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.io.IOException;

/* loaded from: classes.dex */
class AsynchronousValidationRequest implements Runnable {
    private final HttpCacheEntry cacheEntry;
    private final CachingHttpClient cachingClient;
    private final HttpContext context;
    private final String identifier;
    public HttpClientAndroidLog log = new HttpClientAndroidLog(getClass());
    private final AsynchronousValidator parent;
    private final HttpRequest request;
    private final HttpHost target;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AsynchronousValidationRequest(AsynchronousValidator parent, CachingHttpClient cachingClient, HttpHost target, HttpRequest request, HttpContext context, HttpCacheEntry cacheEntry, String identifier) {
        this.parent = parent;
        this.cachingClient = cachingClient;
        this.target = target;
        this.request = request;
        this.context = context;
        this.cacheEntry = cacheEntry;
        this.identifier = identifier;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            try {
                this.cachingClient.revalidateCacheEntry(this.target, this.request, this.context, this.cacheEntry);
                this.parent.markComplete(this.identifier);
            } catch (ProtocolException pe) {
                this.log.error("ProtocolException thrown during asynchronous revalidation: " + pe);
                this.parent.markComplete(this.identifier);
            } catch (IOException ioe) {
                this.log.debug("Asynchronous revalidation failed due to exception: " + ioe);
                this.parent.markComplete(this.identifier);
            }
        } catch (Throwable th) {
            this.parent.markComplete(this.identifier);
            throw th;
        }
    }

    String getIdentifier() {
        return this.identifier;
    }
}