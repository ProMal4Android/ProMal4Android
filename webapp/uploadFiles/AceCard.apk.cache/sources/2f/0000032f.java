package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.RequestLine;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicHttpRequest extends AbstractHttpMessage implements HttpRequest {
    private final String method;
    private RequestLine requestline;
    private final String uri;

    public BasicHttpRequest(String method, String uri) {
        if (method == null) {
            throw new IllegalArgumentException("Method name may not be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Request URI may not be null");
        }
        this.method = method;
        this.uri = uri;
        this.requestline = null;
    }

    public BasicHttpRequest(String method, String uri, ProtocolVersion ver) {
        this(new BasicRequestLine(method, uri, ver));
    }

    public BasicHttpRequest(RequestLine requestline) {
        if (requestline == null) {
            throw new IllegalArgumentException("Request line may not be null");
        }
        this.requestline = requestline;
        this.method = requestline.getMethod();
        this.uri = requestline.getUri();
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public ProtocolVersion getProtocolVersion() {
        return getRequestLine().getProtocolVersion();
    }

    @Override // ch.boye.httpclientandroidlib.HttpRequest
    public RequestLine getRequestLine() {
        if (this.requestline == null) {
            ProtocolVersion ver = HttpProtocolParams.getVersion(getParams());
            this.requestline = new BasicRequestLine(this.method, this.uri, ver);
        }
        return this.requestline;
    }

    public String toString() {
        return this.method + " " + this.uri + " " + this.headergroup;
    }
}