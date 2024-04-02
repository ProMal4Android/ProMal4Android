package ch.boye.httpclientandroidlib.impl;

import ch.boye.httpclientandroidlib.HttpClientConnection;
import ch.boye.httpclientandroidlib.HttpConnectionMetrics;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpResponseFactory;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.impl.entity.EntityDeserializer;
import ch.boye.httpclientandroidlib.impl.entity.EntitySerializer;
import ch.boye.httpclientandroidlib.impl.entity.LaxContentLengthStrategy;
import ch.boye.httpclientandroidlib.impl.entity.StrictContentLengthStrategy;
import ch.boye.httpclientandroidlib.impl.io.DefaultHttpResponseParser;
import ch.boye.httpclientandroidlib.impl.io.HttpRequestWriter;
import ch.boye.httpclientandroidlib.io.EofSensor;
import ch.boye.httpclientandroidlib.io.HttpMessageParser;
import ch.boye.httpclientandroidlib.io.HttpMessageWriter;
import ch.boye.httpclientandroidlib.io.HttpTransportMetrics;
import ch.boye.httpclientandroidlib.io.SessionInputBuffer;
import ch.boye.httpclientandroidlib.io.SessionOutputBuffer;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.io.IOException;
import java.net.SocketTimeoutException;

@NotThreadSafe
/* loaded from: classes.dex */
public abstract class AbstractHttpClientConnection implements HttpClientConnection {
    private SessionInputBuffer inbuffer = null;
    private SessionOutputBuffer outbuffer = null;
    private EofSensor eofSensor = null;
    private HttpMessageParser<HttpResponse> responseParser = null;
    private HttpMessageWriter<HttpRequest> requestWriter = null;
    private HttpConnectionMetricsImpl metrics = null;
    private final EntitySerializer entityserializer = createEntitySerializer();
    private final EntityDeserializer entitydeserializer = createEntityDeserializer();

    protected abstract void assertOpen() throws IllegalStateException;

    protected EntityDeserializer createEntityDeserializer() {
        return new EntityDeserializer(new LaxContentLengthStrategy());
    }

    protected EntitySerializer createEntitySerializer() {
        return new EntitySerializer(new StrictContentLengthStrategy());
    }

    protected HttpResponseFactory createHttpResponseFactory() {
        return new DefaultHttpResponseFactory();
    }

    protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params) {
        return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
    }

    protected HttpMessageWriter<HttpRequest> createRequestWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new HttpRequestWriter(buffer, null, params);
    }

    protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
        return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void init(SessionInputBuffer inbuffer, SessionOutputBuffer outbuffer, HttpParams params) {
        if (inbuffer == null) {
            throw new IllegalArgumentException("Input session buffer may not be null");
        }
        if (outbuffer == null) {
            throw new IllegalArgumentException("Output session buffer may not be null");
        }
        this.inbuffer = inbuffer;
        this.outbuffer = outbuffer;
        if (inbuffer instanceof EofSensor) {
            this.eofSensor = (EofSensor) inbuffer;
        }
        this.responseParser = createResponseParser(inbuffer, createHttpResponseFactory(), params);
        this.requestWriter = createRequestWriter(outbuffer, params);
        this.metrics = createConnectionMetrics(inbuffer.getMetrics(), outbuffer.getMetrics());
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public boolean isResponseAvailable(int timeout) throws IOException {
        assertOpen();
        try {
            return this.inbuffer.isDataAvailable(timeout);
        } catch (SocketTimeoutException e) {
            return false;
        }
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        this.requestWriter.write(request);
        this.metrics.incrementRequestCount();
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        assertOpen();
        if (request.getEntity() != null) {
            this.entityserializer.serialize(this.outbuffer, request, request.getEntity());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doFlush() throws IOException {
        this.outbuffer.flush();
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void flush() throws IOException {
        assertOpen();
        doFlush();
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        assertOpen();
        HttpResponse response = this.responseParser.parse();
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.metrics.incrementResponseCount();
        }
        return response;
    }

    @Override // ch.boye.httpclientandroidlib.HttpClientConnection
    public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        assertOpen();
        HttpEntity entity = this.entitydeserializer.deserialize(this.inbuffer, response);
        response.setEntity(entity);
    }

    protected boolean isEof() {
        return this.eofSensor != null && this.eofSensor.isEof();
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public boolean isStale() {
        if (isOpen() && !isEof()) {
            try {
                this.inbuffer.isDataAvailable(1);
                return isEof();
            } catch (SocketTimeoutException e) {
                return false;
            } catch (IOException e2) {
                return true;
            }
        }
        return true;
    }

    @Override // ch.boye.httpclientandroidlib.HttpConnection
    public HttpConnectionMetrics getMetrics() {
        return this.metrics;
    }
}