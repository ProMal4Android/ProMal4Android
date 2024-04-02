package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpEntityEnclosingRequest;
import ch.boye.httpclientandroidlib.HttpRequest;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.cache.HeaderConstants;
import ch.boye.httpclientandroidlib.entity.AbstractHttpEntity;
import ch.boye.httpclientandroidlib.entity.ContentType;
import ch.boye.httpclientandroidlib.impl.client.RequestWrapper;
import ch.boye.httpclientandroidlib.message.BasicHeader;
import ch.boye.httpclientandroidlib.message.BasicHttpResponse;
import ch.boye.httpclientandroidlib.message.BasicStatusLine;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
@Immutable
/* loaded from: classes.dex */
public class RequestProtocolCompliance {
    private static final List<String> disallowedWithNoCache = Arrays.asList(HeaderConstants.CACHE_CONTROL_MIN_FRESH, HeaderConstants.CACHE_CONTROL_MAX_STALE, "max-age");

    public List<RequestProtocolError> requestIsFatallyNonCompliant(HttpRequest request) {
        List<RequestProtocolError> theErrors = new ArrayList<>();
        RequestProtocolError anError = requestHasWeakETagAndRange(request);
        if (anError != null) {
            theErrors.add(anError);
        }
        RequestProtocolError anError2 = requestHasWeekETagForPUTOrDELETEIfMatch(request);
        if (anError2 != null) {
            theErrors.add(anError2);
        }
        RequestProtocolError anError3 = requestContainsNoCacheDirectiveWithFieldName(request);
        if (anError3 != null) {
            theErrors.add(anError3);
        }
        return theErrors;
    }

    public HttpRequest makeRequestCompliant(HttpRequest request) throws ClientProtocolException {
        if (requestMustNotHaveEntity(request)) {
            ((HttpEntityEnclosingRequest) request).setEntity(null);
        }
        verifyRequestWithExpectContinueFlagHas100continueHeader(request);
        verifyOPTIONSRequestWithBodyHasContentType(request);
        decrementOPTIONSMaxForwardsIfGreaterThen0(request);
        stripOtherFreshnessDirectivesWithNoCache(request);
        if (requestVersionIsTooLow(request)) {
            return upgradeRequestTo(request, HttpVersion.HTTP_1_1);
        }
        if (requestMinorVersionIsTooHighMajorVersionsMatch(request)) {
            return downgradeRequestTo(request, HttpVersion.HTTP_1_1);
        }
        return request;
    }

    private void stripOtherFreshnessDirectivesWithNoCache(HttpRequest request) {
        List<HeaderElement> outElts = new ArrayList<>();
        boolean shouldStrip = false;
        Header[] arr$ = request.getHeaders("Cache-Control");
        for (Header h : arr$) {
            HeaderElement[] arr$2 = h.getElements();
            for (HeaderElement elt : arr$2) {
                if (!disallowedWithNoCache.contains(elt.getName())) {
                    outElts.add(elt);
                }
                if (HeaderConstants.CACHE_CONTROL_NO_CACHE.equals(elt.getName())) {
                    shouldStrip = true;
                }
            }
        }
        if (shouldStrip) {
            request.removeHeaders("Cache-Control");
            request.setHeader("Cache-Control", buildHeaderFromElements(outElts));
        }
    }

    private String buildHeaderFromElements(List<HeaderElement> outElts) {
        StringBuilder newHdr = new StringBuilder("");
        boolean first = true;
        for (HeaderElement elt : outElts) {
            if (!first) {
                newHdr.append(",");
            } else {
                first = false;
            }
            newHdr.append(elt.toString());
        }
        return newHdr.toString();
    }

    private boolean requestMustNotHaveEntity(HttpRequest request) {
        return "TRACE".equals(request.getRequestLine().getMethod()) && (request instanceof HttpEntityEnclosingRequest);
    }

    private void decrementOPTIONSMaxForwardsIfGreaterThen0(HttpRequest request) {
        Header maxForwards;
        if ("OPTIONS".equals(request.getRequestLine().getMethod()) && (maxForwards = request.getFirstHeader("Max-Forwards")) != null) {
            request.removeHeaders("Max-Forwards");
            int currentMaxForwards = Integer.parseInt(maxForwards.getValue());
            request.setHeader("Max-Forwards", Integer.toString(currentMaxForwards - 1));
        }
    }

    private void verifyOPTIONSRequestWithBodyHasContentType(HttpRequest request) {
        if ("OPTIONS".equals(request.getRequestLine().getMethod()) && (request instanceof HttpEntityEnclosingRequest)) {
            addContentTypeHeaderIfMissing((HttpEntityEnclosingRequest) request);
        }
    }

    private void addContentTypeHeaderIfMissing(HttpEntityEnclosingRequest request) {
        if (request.getEntity().getContentType() == null) {
            ((AbstractHttpEntity) request.getEntity()).setContentType(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
        }
    }

    private void verifyRequestWithExpectContinueFlagHas100continueHeader(HttpRequest request) {
        if (request instanceof HttpEntityEnclosingRequest) {
            if (((HttpEntityEnclosingRequest) request).expectContinue() && ((HttpEntityEnclosingRequest) request).getEntity() != null) {
                add100ContinueHeaderIfMissing(request);
                return;
            } else {
                remove100ContinueHeaderIfExists(request);
                return;
            }
        }
        remove100ContinueHeaderIfExists(request);
    }

    private void remove100ContinueHeaderIfExists(HttpRequest request) {
        boolean hasHeader = false;
        Header[] expectHeaders = request.getHeaders("Expect");
        List<HeaderElement> expectElementsThatAreNot100Continue = new ArrayList<>();
        for (Header h : expectHeaders) {
            HeaderElement[] arr$ = h.getElements();
            for (HeaderElement elt : arr$) {
                if (!HTTP.EXPECT_CONTINUE.equalsIgnoreCase(elt.getName())) {
                    expectElementsThatAreNot100Continue.add(elt);
                } else {
                    hasHeader = true;
                }
            }
            if (hasHeader) {
                request.removeHeader(h);
                for (HeaderElement elt2 : expectElementsThatAreNot100Continue) {
                    BasicHeader newHeader = new BasicHeader("Expect", elt2.getName());
                    request.addHeader(newHeader);
                }
                return;
            }
            expectElementsThatAreNot100Continue = new ArrayList<>();
        }
    }

    private void add100ContinueHeaderIfMissing(HttpRequest request) {
        boolean hasHeader = false;
        Header[] arr$ = request.getHeaders("Expect");
        for (Header h : arr$) {
            HeaderElement[] arr$2 = h.getElements();
            for (HeaderElement elt : arr$2) {
                if (HTTP.EXPECT_CONTINUE.equalsIgnoreCase(elt.getName())) {
                    hasHeader = true;
                }
            }
        }
        if (!hasHeader) {
            request.addHeader("Expect", HTTP.EXPECT_CONTINUE);
        }
    }

    private HttpRequest upgradeRequestTo(HttpRequest request, ProtocolVersion version) throws ClientProtocolException {
        try {
            RequestWrapper newRequest = new RequestWrapper(request);
            newRequest.setProtocolVersion(version);
            return newRequest;
        } catch (ProtocolException pe) {
            throw new ClientProtocolException(pe);
        }
    }

    private HttpRequest downgradeRequestTo(HttpRequest request, ProtocolVersion version) throws ClientProtocolException {
        try {
            RequestWrapper newRequest = new RequestWrapper(request);
            newRequest.setProtocolVersion(version);
            return newRequest;
        } catch (ProtocolException pe) {
            throw new ClientProtocolException(pe);
        }
    }

    protected boolean requestMinorVersionIsTooHighMajorVersionsMatch(HttpRequest request) {
        ProtocolVersion requestProtocol = request.getProtocolVersion();
        return requestProtocol.getMajor() == HttpVersion.HTTP_1_1.getMajor() && requestProtocol.getMinor() > HttpVersion.HTTP_1_1.getMinor();
    }

    protected boolean requestVersionIsTooLow(HttpRequest request) {
        return request.getProtocolVersion().compareToVersion(HttpVersion.HTTP_1_1) < 0;
    }

    public HttpResponse getErrorForRequest(RequestProtocolError errorCheck) {
        switch (errorCheck) {
            case BODY_BUT_NO_LENGTH_ERROR:
                return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_LENGTH_REQUIRED, ""));
            case WEAK_ETAG_AND_RANGE_ERROR:
                return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "Weak eTag not compatible with byte range"));
            case WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR:
                return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "Weak eTag not compatible with PUT or DELETE requests"));
            case NO_CACHE_DIRECTIVE_WITH_FIELD_NAME:
                return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "No-Cache directive MUST NOT include a field name"));
            default:
                throw new IllegalStateException("The request was compliant, therefore no error can be generated for it.");
        }
    }

    private RequestProtocolError requestHasWeakETagAndRange(HttpRequest request) {
        Header ifRange;
        String method = request.getRequestLine().getMethod();
        if ("GET".equals(method)) {
            Header range = request.getFirstHeader("Range");
            if (range == null || (ifRange = request.getFirstHeader("If-Range")) == null) {
                return null;
            }
            String val = ifRange.getValue();
            if (val.startsWith("W/")) {
                return RequestProtocolError.WEAK_ETAG_AND_RANGE_ERROR;
            }
            return null;
        }
        return null;
    }

    private RequestProtocolError requestHasWeekETagForPUTOrDELETEIfMatch(HttpRequest request) {
        String method = request.getRequestLine().getMethod();
        if ("PUT".equals(method) || "DELETE".equals(method)) {
            Header ifMatch = request.getFirstHeader("If-Match");
            if (ifMatch != null) {
                String val = ifMatch.getValue();
                if (val.startsWith("W/")) {
                    return RequestProtocolError.WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR;
                }
                return null;
            }
            Header ifNoneMatch = request.getFirstHeader("If-None-Match");
            if (ifNoneMatch != null) {
                String val2 = ifNoneMatch.getValue();
                if (val2.startsWith("W/")) {
                    return RequestProtocolError.WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private RequestProtocolError requestContainsNoCacheDirectiveWithFieldName(HttpRequest request) {
        Header[] arr$ = request.getHeaders("Cache-Control");
        for (Header h : arr$) {
            HeaderElement[] arr$2 = h.getElements();
            for (HeaderElement elt : arr$2) {
                if (HeaderConstants.CACHE_CONTROL_NO_CACHE.equalsIgnoreCase(elt.getName()) && elt.getValue() != null) {
                    return RequestProtocolError.NO_CACHE_DIRECTIVE_WITH_FIELD_NAME;
                }
            }
        }
        return null;
    }
}