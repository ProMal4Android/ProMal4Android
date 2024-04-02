package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpHeaders;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpRequestInterceptor;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseInterceptor;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.ResponseHandler;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.protocol.RequestAcceptEncoding;
import ch.boye.httpclientandroidlib.client.protocol.ResponseContentEncoding;
import ch.boye.httpclientandroidlib.client.utils.URIUtils;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import java.io.IOException;
import java.net.URI;

/* loaded from: classes.dex */
public class DecompressingHttpClient implements HttpClient {
    private HttpRequestInterceptor acceptEncodingInterceptor;
    private HttpClient backend;
    private HttpResponseInterceptor contentEncodingInterceptor;

    public DecompressingHttpClient(HttpClient backend) {
        this(backend, new RequestAcceptEncoding(), new ResponseContentEncoding());
    }

    DecompressingHttpClient(HttpClient backend, HttpRequestInterceptor requestInterceptor, HttpResponseInterceptor responseInterceptor) {
        this.backend = backend;
        this.acceptEncodingInterceptor = requestInterceptor;
        this.contentEncodingInterceptor = responseInterceptor;
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpParams getParams() {
        return this.backend.getParams();
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public ClientConnectionManager getConnectionManager() {
        return this.backend.getConnectionManager();
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        return execute(getHttpHost(request), request, (HttpContext) null);
    }

    HttpHost getHttpHost(HttpUriRequest request) {
        URI uri = request.getURI();
        return URIUtils.extractHost(uri);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
        return execute(getHttpHost(request), request, context);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
        return execute(target, request, (HttpContext) null);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
        HttpRequest wrapped;
        if (context == null) {
            try {
                context = new BasicHttpContext();
            } catch (HttpException e) {
                throw new ClientProtocolException(e);
            }
        }
        if (request instanceof HttpEntityEnclosingRequest) {
            wrapped = new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest) request);
        } else {
            wrapped = new RequestWrapper(request);
        }
        this.acceptEncodingInterceptor.process(wrapped, context);
        HttpResponse response = this.backend.execute(target, wrapped, context);
        try {
            try {
                this.contentEncodingInterceptor.process(response, context);
                if (Boolean.TRUE.equals(context.getAttribute(ResponseContentEncoding.UNCOMPRESSED))) {
                    response.removeHeaders("Content-Length");
                    response.removeHeaders("Content-Encoding");
                    response.removeHeaders(HttpHeaders.CONTENT_MD5);
                }
                return response;
            } catch (IOException ex) {
                EntityUtils.consume(response.getEntity());
                throw ex;
            } catch (RuntimeException ex2) {
                EntityUtils.consume(response.getEntity());
                throw ex2;
            }
        } catch (HttpException ex3) {
            EntityUtils.consume(response.getEntity());
            throw ex3;
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return (T) execute(getHttpHost(request), request, responseHandler);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        return (T) execute(getHttpHost(request), request, responseHandler, context);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return (T) execute(target, request, responseHandler, null);
    }

    @Override // ch.boye.httpclientandroidlib.client.HttpClient
    public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        HttpResponse response = execute(target, request, context);
        try {
            return responseHandler.handleResponse(response);
        } finally {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        }
    }
}