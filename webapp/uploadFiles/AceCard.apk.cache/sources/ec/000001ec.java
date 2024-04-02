package ch.boye.httpclientandroidlib.conn.params;

import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;

/* loaded from: classes.dex */
public interface ConnPerRoute {
    int getMaxForRoute(HttpRoute httpRoute);
}