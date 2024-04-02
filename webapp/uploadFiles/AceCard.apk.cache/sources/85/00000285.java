package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;

@NotThreadSafe
/* loaded from: classes.dex */
public class RoutedRequest {
    protected final RequestWrapper request;
    protected final HttpRoute route;

    public RoutedRequest(RequestWrapper req, HttpRoute route) {
        this.request = req;
        this.route = route;
    }

    public final RequestWrapper getRequest() {
        return this.request;
    }

    public final HttpRoute getRoute() {
        return this.route;
    }
}