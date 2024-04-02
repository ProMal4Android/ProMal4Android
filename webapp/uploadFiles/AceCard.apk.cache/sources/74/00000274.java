package ch.boye.httpclientandroidlib.impl.client;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.CircularRedirectException;
import ch.boye.httpclientandroidlib.client.RedirectStrategy;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpEntityEnclosingRequestBase;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpHead;
import ch.boye.httpclientandroidlib.client.methods.HttpOptions;
import ch.boye.httpclientandroidlib.client.methods.HttpPatch;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpTrace;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.utils.URIUtils;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;
import java.net.URI;
import java.net.URISyntaxException;

@Immutable
/* loaded from: classes.dex */
public class DefaultRedirectStrategy implements RedirectStrategy {
    public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    private static final String[] REDIRECT_METHODS = {"GET", "HEAD"};
    public HttpClientAndroidLog log = new HttpClientAndroidLog(getClass());

    @Override // ch.boye.httpclientandroidlib.client.RedirectStrategy
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        int statusCode = response.getStatusLine().getStatusCode();
        String method = request.getRequestLine().getMethod();
        Header locationHeader = response.getFirstHeader("location");
        switch (statusCode) {
            case HttpStatus.SC_MOVED_PERMANENTLY /* 301 */:
            case HttpStatus.SC_TEMPORARY_REDIRECT /* 307 */:
                return isRedirectable(method);
            case HttpStatus.SC_MOVED_TEMPORARILY /* 302 */:
                return isRedirectable(method) && locationHeader != null;
            case HttpStatus.SC_SEE_OTHER /* 303 */:
                return true;
            case HttpStatus.SC_NOT_MODIFIED /* 304 */:
            case HttpStatus.SC_USE_PROXY /* 305 */:
            case 306:
            default:
                return false;
        }
    }

    public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null");
        }
        Header locationHeader = response.getFirstHeader("location");
        if (locationHeader == null) {
            throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
        }
        String location = locationHeader.getValue();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Redirect requested to location '" + location + "'");
        }
        URI uri = createLocationURI(location);
        HttpParams params = request.getParams();
        try {
            URI uri2 = URIUtils.rewriteURI(uri);
            if (!uri2.isAbsolute()) {
                if (params.isParameterTrue(ClientPNames.REJECT_RELATIVE_REDIRECT)) {
                    throw new ProtocolException("Relative redirect location '" + uri2 + "' not allowed");
                }
                HttpHost target = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
                if (target == null) {
                    throw new IllegalStateException("Target host not available in the HTTP context");
                }
                URI requestURI = new URI(request.getRequestLine().getUri());
                URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
                uri2 = URIUtils.resolve(absoluteRequestURI, uri2);
            }
            RedirectLocations redirectLocations = (RedirectLocations) context.getAttribute(REDIRECT_LOCATIONS);
            if (redirectLocations == null) {
                redirectLocations = new RedirectLocations();
                context.setAttribute(REDIRECT_LOCATIONS, redirectLocations);
            }
            if (params.isParameterFalse(ClientPNames.ALLOW_CIRCULAR_REDIRECTS) && redirectLocations.contains(uri2)) {
                throw new CircularRedirectException("Circular redirect to '" + uri2 + "'");
            }
            redirectLocations.add(uri2);
            return uri2;
        } catch (URISyntaxException ex) {
            throw new ProtocolException(ex.getMessage(), ex);
        }
    }

    protected URI createLocationURI(String location) throws ProtocolException {
        try {
            return new URI(location).normalize();
        } catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }
    }

    protected boolean isRedirectable(String method) {
        String[] arr$ = REDIRECT_METHODS;
        for (String m : arr$) {
            if (m.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    @Override // ch.boye.httpclientandroidlib.client.RedirectStrategy
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        URI uri = getLocationURI(request, response, context);
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        if (method.equalsIgnoreCase("GET")) {
            return new HttpGet(uri);
        }
        int status = response.getStatusLine().getStatusCode();
        if (status == 307) {
            if (method.equalsIgnoreCase(HttpPost.METHOD_NAME)) {
                return copyEntity(new HttpPost(uri), request);
            }
            if (method.equalsIgnoreCase("PUT")) {
                return copyEntity(new HttpPut(uri), request);
            }
            if (method.equalsIgnoreCase("DELETE")) {
                return new HttpDelete(uri);
            }
            if (method.equalsIgnoreCase("TRACE")) {
                return new HttpTrace(uri);
            }
            if (method.equalsIgnoreCase("OPTIONS")) {
                return new HttpOptions(uri);
            }
            if (method.equalsIgnoreCase(HttpPatch.METHOD_NAME)) {
                return copyEntity(new HttpPatch(uri), request);
            }
        }
        return new HttpGet(uri);
    }

    private HttpUriRequest copyEntity(HttpEntityEnclosingRequestBase redirect, HttpRequest original) {
        if (original instanceof HttpEntityEnclosingRequest) {
            redirect.setEntity(((HttpEntityEnclosingRequest) original).getEntity());
        }
        return redirect;
    }
}