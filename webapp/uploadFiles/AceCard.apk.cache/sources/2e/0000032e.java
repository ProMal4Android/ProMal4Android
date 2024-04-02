package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.RequestLine;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.protocol.HTTP;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicHttpEntityEnclosingRequest extends BasicHttpRequest implements HttpEntityEnclosingRequest {
    private HttpEntity entity;

    public BasicHttpEntityEnclosingRequest(String method, String uri) {
        super(method, uri);
    }

    public BasicHttpEntityEnclosingRequest(String method, String uri, ProtocolVersion ver) {
        super(method, uri, ver);
    }

    public BasicHttpEntityEnclosingRequest(RequestLine requestline) {
        super(requestline);
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest
    public HttpEntity getEntity() {
        return this.entity;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest
    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest
    public boolean expectContinue() {
        Header expect = getFirstHeader("Expect");
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }
}