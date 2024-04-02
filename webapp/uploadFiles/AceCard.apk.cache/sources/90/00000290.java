package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.annotation.ThreadSafe;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheUpdateCallback;
import java.io.IOException;

@ThreadSafe
/* loaded from: classes.dex */
public class BasicHttpCacheStorage implements HttpCacheStorage {
    private final CacheMap entries;

    public BasicHttpCacheStorage(CacheConfig config) {
        this.entries = new CacheMap(config.getMaxCacheEntries());
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage
    public synchronized void putEntry(String url, HttpCacheEntry entry) throws IOException {
        this.entries.put(url, entry);
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage
    public synchronized HttpCacheEntry getEntry(String url) throws IOException {
        return this.entries.get(url);
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage
    public synchronized void removeEntry(String url) throws IOException {
        this.entries.remove(url);
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage
    public synchronized void updateEntry(String url, HttpCacheUpdateCallback callback) throws IOException {
        HttpCacheEntry existingEntry = this.entries.get(url);
        this.entries.put(url, callback.update(existingEntry));
    }
}