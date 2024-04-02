package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.RedirectHandler;
import ch.boye.httpclientandroidlib.client.RedirectStrategy;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpHead;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.net.URI;

@Immutable
@Deprecated
/* loaded from: classes.dex */
class DefaultRedirectStrategyAdaptor implements RedirectStrategy {
    private final RedirectHandler handler;

    public DefaultRedirectStrategyAdaptor(RedirectHandler handler) {
        this.handler = handler;
    }

    @Override // ch.boye.httpclientandroidlib.client.RedirectStrategy
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        return this.handler.isRedirectRequested(response, context);
    }

    @Override // ch.boye.httpclientandroidlib.client.RedirectStrategy
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI uri = this.handler.getLocationURI(response, context);
        String method = request.getRequestLine().getMethod();
        return method.equalsIgnoreCase("HEAD") ? new HttpHead(uri) : new HttpGet(uri);
    }

    public RedirectHandler getHandler() {
        return this.handler;
    }
}