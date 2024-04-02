package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.client.cache.InputLimit;
import ch.boye.httpclientandroidlib.client.cache.Resource;
import ch.boye.httpclientandroidlib.client.cache.ResourceFactory;
import ch.boye.httpclientandroidlib.message.BasicHttpResponse;
import java.io.IOException;
import java.io.InputStream;

@NotThreadSafe
/* loaded from: classes.dex */
class SizeLimitedResponseReader {
    private boolean consumed;
    private InputStream instream;
    private InputLimit limit;
    private final long maxResponseSizeBytes;
    private final HttpRequest request;
    private Resource resource;
    private final ResourceFactory resourceFactory;
    private final HttpResponse response;

    public SizeLimitedResponseReader(ResourceFactory resourceFactory, long maxResponseSizeBytes, HttpRequest request, HttpResponse response) {
        this.resourceFactory = resourceFactory;
        this.maxResponseSizeBytes = maxResponseSizeBytes;
        this.request = request;
        this.response = response;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void readResponse() throws IOException {
        if (!this.consumed) {
            doConsume();
        }
    }

    private void ensureNotConsumed() {
        if (this.consumed) {
            throw new IllegalStateException("Response has already been consumed");
        }
    }

    private void ensureConsumed() {
        if (!this.consumed) {
            throw new IllegalStateException("Response has not been consumed");
        }
    }

    private void doConsume() throws IOException {
        ensureNotConsumed();
        this.consumed = true;
        this.limit = new InputLimit(this.maxResponseSizeBytes);
        HttpEntity entity = this.response.getEntity();
        if (entity != null) {
            String uri = this.request.getRequestLine().getUri();
            this.instream = entity.getContent();
            try {
                this.resource = this.resourceFactory.generate(uri, this.instream, this.limit);
            } finally {
                if (!this.limit.isReached()) {
                    this.instream.close();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLimitReached() {
        ensureConsumed();
        return this.limit.isReached();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Resource getResource() {
        ensureConsumed();
        return this.resource;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HttpResponse getReconstructedResponse() throws IOException {
        ensureConsumed();
        HttpResponse reconstructed = new BasicHttpResponse(this.response.getStatusLine());
        reconstructed.setHeaders(this.response.getAllHeaders());
        CombinedEntity combinedEntity = new CombinedEntity(this.resource, this.instream);
        HttpEntity entity = this.response.getEntity();
        if (entity != null) {
            combinedEntity.setContentType(entity.getContentType());
            combinedEntity.setContentEncoding(entity.getContentEncoding());
            combinedEntity.setChunked(entity.isChunked());
        }
        reconstructed.setEntity(combinedEntity);
        return reconstructed;
    }
}