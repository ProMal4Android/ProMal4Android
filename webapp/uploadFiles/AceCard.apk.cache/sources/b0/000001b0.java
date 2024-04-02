package ch.boye.httpclientandroidlib.client.methods;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.net.URI;

@NotThreadSafe
/* loaded from: classes.dex */
public class HttpTrace extends HttpRequestBase {
    public static final String METHOD_NAME = "TRACE";

    public HttpTrace() {
    }

    public HttpTrace(URI uri) {
        setURI(uri);
    }

    public HttpTrace(String uri) {
        setURI(URI.create(uri));
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.HttpRequestBase, ch.boye.httpclientandroidlib.client.methods.HttpUriRequest
    public String getMethod() {
        return "TRACE";
    }
}