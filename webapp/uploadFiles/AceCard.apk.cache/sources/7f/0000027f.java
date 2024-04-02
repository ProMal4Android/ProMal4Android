package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ConnectionBackoffStrategy;

/* loaded from: classes.dex */
public class NullBackoffStrategy implements ConnectionBackoffStrategy {
    @Override // ch.boye.httpclientandroidlib.client.ConnectionBackoffStrategy
    public boolean shouldBackoff(Throwable t) {
        return false;
    }

    @Override // ch.boye.httpclientandroidlib.client.ConnectionBackoffStrategy
    public boolean shouldBackoff(HttpResponse resp) {
        return false;
    }
}