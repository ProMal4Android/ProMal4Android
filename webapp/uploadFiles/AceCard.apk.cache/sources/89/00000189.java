package ch.boye.httpclientandroidlib.client;

import ch.boye.httpclientandroidlib.HttpResponse;

/* loaded from: classes.dex */
public interface ConnectionBackoffStrategy {
    boolean shouldBackoff(HttpResponse httpResponse);

    boolean shouldBackoff(Throwable th);
}