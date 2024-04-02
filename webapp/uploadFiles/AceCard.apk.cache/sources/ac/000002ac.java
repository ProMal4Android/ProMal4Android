package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.client.cache.Resource;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

@Immutable
/* loaded from: classes.dex */
class ResourceReference extends PhantomReference<HttpCacheEntry> {
    private final Resource resource;

    public ResourceReference(HttpCacheEntry entry, ReferenceQueue<HttpCacheEntry> q) {
        super(entry, q);
        if (entry.getResource() == null) {
            throw new IllegalArgumentException("Resource may not be null");
        }
        this.resource = entry.getResource();
    }

    public Resource getResource() {
        return this.resource;
    }

    public int hashCode() {
        return this.resource.hashCode();
    }

    public boolean equals(Object obj) {
        return this.resource.equals(obj);
    }
}