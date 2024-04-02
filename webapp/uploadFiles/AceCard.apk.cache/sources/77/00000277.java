package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.ServiceUnavailableRetryStrategy;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

@Immutable
/* loaded from: classes.dex */
public class DefaultServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {
    private final int maxRetries;
    private final long retryInterval;

    public DefaultServiceUnavailableRetryStrategy(int maxRetries, int retryInterval) {
        if (maxRetries < 1) {
            throw new IllegalArgumentException("MaxRetries must be greater than 1");
        }
        if (retryInterval < 1) {
            throw new IllegalArgumentException("Retry interval must be greater than 1");
        }
        this.maxRetries = maxRetries;
        this.retryInterval = retryInterval;
    }

    public DefaultServiceUnavailableRetryStrategy() {
        this(1, 1000);
    }

    @Override // ch.boye.httpclientandroidlib.client.ServiceUnavailableRetryStrategy
    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
        return executionCount <= this.maxRetries && response.getStatusLine().getStatusCode() == 503;
    }

    @Override // ch.boye.httpclientandroidlib.client.ServiceUnavailableRetryStrategy
    public long getRetryInterval() {
        return this.retryInterval;
    }
}