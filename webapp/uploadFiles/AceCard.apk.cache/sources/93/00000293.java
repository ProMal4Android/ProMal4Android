package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

@Immutable
/* loaded from: classes.dex */
class CacheEntity implements HttpEntity, Serializable {
    private static final long serialVersionUID = -3467082284120936233L;
    private final HttpCacheEntry cacheEntry;

    public CacheEntity(HttpCacheEntry cacheEntry) {
        this.cacheEntry = cacheEntry;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public Header getContentType() {
        return this.cacheEntry.getFirstHeader("Content-Type");
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public Header getContentEncoding() {
        return this.cacheEntry.getFirstHeader("Content-Encoding");
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isChunked() {
        return false;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isRepeatable() {
        return true;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public long getContentLength() {
        return this.cacheEntry.getResource().length();
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public InputStream getContent() throws IOException {
        return this.cacheEntry.getResource().getInputStream();
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = this.cacheEntry.getResource().getInputStream();
        try {
            IOUtils.copy(instream, outstream);
        } finally {
            instream.close();
        }
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isStreaming() {
        return false;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public void consumeContent() throws IOException {
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}