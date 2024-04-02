package ch.boye.httpclientandroidlib.client.methods;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HeaderIterator;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@NotThreadSafe
/* loaded from: classes.dex */
public class HttpOptions extends HttpRequestBase {
    public static final String METHOD_NAME = "OPTIONS";

    public HttpOptions() {
    }

    public HttpOptions(URI uri) {
        setURI(uri);
    }

    public HttpOptions(String uri) {
        setURI(URI.create(uri));
    }

    @Override // ch.boye.httpclientandroidlib.client.methods.HttpRequestBase, ch.boye.httpclientandroidlib.client.methods.HttpUriRequest
    public String getMethod() {
        return "OPTIONS";
    }

    public Set<String> getAllowedMethods(HttpResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        HeaderIterator it = response.headerIterator("Allow");
        Set<String> methods = new HashSet<>();
        while (it.hasNext()) {
            Header header = it.nextHeader();
            HeaderElement[] elements = header.getElements();
            for (HeaderElement element : elements) {
                methods.add(element.getName());
            }
        }
        return methods;
    }
}