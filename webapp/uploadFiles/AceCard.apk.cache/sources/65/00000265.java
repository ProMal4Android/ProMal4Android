package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.ThreadSafe;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.ResponseHandler;
import ch.boye.httpclientandroidlib.client.ServiceUnavailableRetryStrategy;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;

@ThreadSafe
/* loaded from: classes.dex */
public class AutoRetryHttpClient implements HttpClient {
    private final HttpClient backend;
    public HttpClientAndroidLog log;
    private final ServiceUnavailableRetryStrategy retryStrategy;

    public AutoRetryHttpClient(HttpClient client, ServiceUnavailableRetryStrategy retryStrategy) {
        this.log = new HttpClientAndroidLog(getClass());
        if (client == null) {
            throw new IllegalArgumentException("HttpClient may not be null");
        }
        if (retryStrategy == null) {
            throw new IllegalArgumentException("ServiceUnavailableRetryStrategy may not be null");
        }
        this.backend = client;
        this.retryStrategy = retryStrategy;
    }

    public AutoRetryHttpClient() {
        this(new DefaultHttpClient(), new DefaultServiceUnavailableRetryStrategy());
    }

    public AutoRetryHttpClient(ServiceUnavailableRetryStrategy config) {
        this(new DefaultHttpClient(), config);
    }

    public AutoRetryHttpClient(HttpClient client) {
        this(client, new DefaultServiceUnavailableRetryStrategy());
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
        return execute(target, request, (HttpContext) null);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        return (T) execute(target, request, responseHandler, null);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
        HttpResponse resp = execute(target, request, context);
        return responseHandler.handleResponse(resp);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpUriRequest request) throws IOException {
        return execute(request, (HttpContext) null);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
        URI uri = request.getURI();
        HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        return execute(httpHost, request, context);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException {
        return (T) execute(request, responseHandler, (HttpContext) null);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException {
        HttpResponse resp = execute(request, context);
        return responseHandler.handleResponse(resp);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        int c = 1;
        while (true) {
            HttpResponse response = this.backend.execute(target, request, context);
            try {
                if (this.retryStrategy.retryRequest(response, c, context)) {
                    EntityUtils.consume(response.getEntity());
                    long nextInterval = this.retryStrategy.getRetryInterval();
                    try {
                        this.log.trace("Wait for " + nextInterval);
                        Thread.sleep(nextInterval);
                        c++;
                    } catch (InterruptedException e) {
                        throw new InterruptedIOException(e.getMessage());
                    }
                } else {
                    return response;
                }
            } catch (RuntimeException ex) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException ioex) {
                    this.log.warn("I/O error consuming response content", ioex);
                }
                throw ex;
            }
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public ClientConnectionManager getConnectionManager() {
        return this.backend.getConnectionManager();
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpParams getParams() {
        return this.backend.getParams();
    }
}