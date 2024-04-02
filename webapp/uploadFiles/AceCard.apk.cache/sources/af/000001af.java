package ch.boye.httpclientandroidlib.client.methods;

import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.RequestLine;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.client.utils.CloneUtils;
import ch.boye.httpclientandroidlib.conn.ClientConnectionRequest;
import ch.boye.httpclientandroidlib.conn.ConnectionReleaseTrigger;
import ch.boye.httpclientandroidlib.message.AbstractHttpMessage;
import ch.boye.httpclientandroidlib.message.BasicRequestLine;
import ch.boye.httpclientandroidlib.message.HeaderGroup;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@NotThreadSafe
/* loaded from: classes.dex */
public abstract class HttpRequestBase extends AbstractHttpMessage implements HttpUriRequest, AbortableHttpRequest, Cloneable {
    private Lock abortLock = new ReentrantLock();
    private volatile boolean aborted;
    private ClientConnectionRequest connRequest;
    private ConnectionReleaseTrigger releaseTrigger;
    private URI uri;

    public abstract String getMethod();

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public ProtocolVersion getProtocolVersion() {
        return HttpProtocolParams.getVersion(getParams());
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.HttpUriRequest
    public URI getURI() {
        return this.uri;
    }

    @Override // ch.boye.httpclientandroidlib.HttpRequest
    public RequestLine getRequestLine() {
        String method = getMethod();
        ProtocolVersion ver = getProtocolVersion();
        URI uri = getURI();
        String uritext = null;
        if (uri != null) {
            uritext = uri.toASCIIString();
        }
        uritext = (uritext == null || uritext.length() == 0) ? "/" : "/";
        return new BasicRequestLine(method, uritext, ver);
    }

    public void setURI(URI uri) {
        this.uri = uri;
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.AbortableHttpRequest
    public void setConnectionRequest(ClientConnectionRequest connRequest) throws IOException {
        if (this.aborted) {
            throw new IOException("Request already aborted");
        }
        this.abortLock.lock();
        try {
            this.connRequest = connRequest;
        } finally {
            this.abortLock.unlock();
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.AbortableHttpRequest
    public void setReleaseTrigger(ConnectionReleaseTrigger releaseTrigger) throws IOException {
        if (this.aborted) {
            throw new IOException("Request already aborted");
        }
        this.abortLock.lock();
        try {
            this.releaseTrigger = releaseTrigger;
        } finally {
            this.abortLock.unlock();
        }
    }

    private void cleanup() {
        if (this.connRequest != null) {
            this.connRequest.abortRequest();
            this.connRequest = null;
        }
        if (this.releaseTrigger != null) {
            try {
                this.releaseTrigger.abortConnection();
            } catch (IOException e) {
            }
            this.releaseTrigger = null;
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.HttpUriRequest, ch.boye.httpclientandroidlib.client.methods.AbortableHttpRequest
    public void abort() {
        if (!this.aborted) {
            this.abortLock.lock();
            try {
                this.aborted = true;
                cleanup();
            } finally {
                this.abortLock.unlock();
            }
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.HttpUriRequest
    public boolean isAborted() {
        return this.aborted;
    }

    public void reset() {
        this.abortLock.lock();
        try {
            cleanup();
            this.aborted = false;
        } finally {
            this.abortLock.unlock();
        }
    }

    public void releaseConnection() {
        reset();
    }

    public Object clone() throws CloneNotSupportedException {
        HttpRequestBase clone = (HttpRequestBase) super.clone();
        clone.abortLock = new ReentrantLock();
        clone.aborted = false;
        clone.releaseTrigger = null;
        clone.connRequest = null;
        clone.headergroup = (HeaderGroup) CloneUtils.clone(this.headergroup);
        clone.params = (HttpParams) CloneUtils.clone(this.params);
        return clone;
    }

    public String toString() {
        return getMethod() + " " + getURI() + " " + getProtocolVersion();
    }
}