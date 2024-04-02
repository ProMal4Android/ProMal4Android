package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AsynchronousValidator {
    private final CacheKeyGenerator cacheKeyGenerator;
    private final CachingHttpClient cachingClient;
    private final ExecutorService executor;
    public HttpClientAndroidLog log;
    private final Set<String> queued;

    public AsynchronousValidator(CachingHttpClient cachingClient, CacheConfig config) {
        this(cachingClient, new ThreadPoolExecutor(config.getAsynchronousWorkersCore(), config.getAsynchronousWorkersMax(), config.getAsynchronousWorkerIdleLifetimeSecs(), TimeUnit.SECONDS, new ArrayBlockingQueue(config.getRevalidationQueueSize())));
    }

    AsynchronousValidator(CachingHttpClient cachingClient, ExecutorService executor) {
        this.log = new HttpClientAndroidLog(getClass());
        this.cachingClient = cachingClient;
        this.executor = executor;
        this.queued = new HashSet();
        this.cacheKeyGenerator = new CacheKeyGenerator();
    }

    public synchronized void revalidateCacheEntry(HttpHost target, HttpRequest request, HttpContext context, HttpCacheEntry entry) {
        String uri = this.cacheKeyGenerator.getVariantURI(target, request, entry);
        if (!this.queued.contains(uri)) {
            AsynchronousValidationRequest revalidationRequest = new AsynchronousValidationRequest(this, this.cachingClient, target, request, context, entry, uri);
            try {
                this.executor.execute(revalidationRequest);
                this.queued.add(uri);
            } catch (RejectedExecutionException ree) {
                this.log.debug("Revalidation for [" + uri + "] not scheduled: " + ree);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void markComplete(String identifier) {
        this.queued.remove(identifier);
    }

    Set<String> getScheduledIdentifiers() {
        return Collections.unmodifiableSet(this.queued);
    }

    ExecutorService getExecutor() {
        return this.executor;
    }
}