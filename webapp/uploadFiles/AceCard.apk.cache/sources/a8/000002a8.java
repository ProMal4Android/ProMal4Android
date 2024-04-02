package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderIterator;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpStatus;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.message.AbstractHttpMessage;
import ch.boye.httpclientandroidlib.message.BasicStatusLine;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.util.Locale;

@Immutable
/* loaded from: classes.dex */
final class OptionsHttp11Response extends AbstractHttpMessage implements HttpResponse {
    private final StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_NOT_IMPLEMENTED, "");
    private final ProtocolVersion version = HttpVersion.HTTP_1_1;

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusLine(StatusLine statusline) {
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusLine(ProtocolVersion ver, int code) {
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusCode(int code) throws IllegalStateException {
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setReasonPhrase(String reason) throws IllegalStateException {
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public HttpEntity getEntity() {
        return null;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setEntity(HttpEntity entity) {
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public Locale getLocale() {
        return null;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setLocale(Locale loc) {
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public ProtocolVersion getProtocolVersion() {
        return this.version;
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public boolean containsHeader(String name) {
        return this.headergroup.containsHeader(name);
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public Header[] getHeaders(String name) {
        return this.headergroup.getHeaders(name);
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public Header getFirstHeader(String name) {
        return this.headergroup.getFirstHeader(name);
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public Header getLastHeader(String name) {
        return this.headergroup.getLastHeader(name);
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public Header[] getAllHeaders() {
        return this.headergroup.getAllHeaders();
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void addHeader(Header header) {
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void addHeader(String name, String value) {
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void setHeader(Header header) {
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void setHeader(String name, String value) {
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void setHeaders(Header[] headers) {
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void removeHeader(Header header) {
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void removeHeaders(String name) {
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public HeaderIterator headerIterator() {
        return this.headergroup.iterator();
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public HeaderIterator headerIterator(String name) {
        return this.headergroup.iterator(name);
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = new BasicHttpParams();
        }
        return this.params;
    }

    @Override // ch.boye.httpclientandroidlib.message.AbstractHttpMessage, ch.boye.httpclientandroidlib.HttpMessage
    public void setParams(HttpParams params) {
    }
}