package ch.boye.httpclientandroidlib.client;

import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;

/* loaded from: classes.dex */
public interface BackoffManager {
    void backOff(HttpRoute httpRoute);

    void probe(HttpRoute httpRoute);
}