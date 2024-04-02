package ch.boye.httpclientandroidlib.client.methods;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.net.URI;

@NotThreadSafe
/* loaded from: classes.dex */
public class HttpPut extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "PUT";

    public HttpPut() {
    }

    public HttpPut(URI uri) {
        setURI(uri);
    }

    public HttpPut(String uri) {
        setURI(URI.create(uri));
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.HttpRequestBase, ch.boye.httpclientandroidlib.client.methods.HttpUriRequest
    public String getMethod() {
        return "PUT";
    }
}