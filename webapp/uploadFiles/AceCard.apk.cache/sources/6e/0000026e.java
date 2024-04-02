package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ConnectionBackoffStrategy;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

/* loaded from: classes.dex */
public class DefaultBackoffStrategy implements ConnectionBackoffStrategy {
    @Override // ch.boye.httpclientandroidlib.client.ConnectionBackoffStrategy
    public boolean shouldBackoff(Throwable t) {
        return (t instanceof SocketTimeoutException) || (t instanceof ConnectException);
    }

    @Override // ch.boye.httpclientandroidlib.client.ConnectionBackoffStrategy
    public boolean shouldBackoff(HttpResponse resp) {
        return resp.getStatusLine().getStatusCode() == 503;
    }
}