package ch.boye.httpclientandroidlib.client.methods;

import ch.boye.httpclientandroidlib.HttpRequest;
import java.net.URI;

/* loaded from: classes.dex */
public interface HttpUriRequest extends HttpRequest {
    void abort() throws UnsupportedOperationException;

    String getMethod();

    URI getURI();

    boolean isAborted();
}