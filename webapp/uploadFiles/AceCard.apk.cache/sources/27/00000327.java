package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderIterator;
import ch.boye.httpclientandroidlib.HttpMessage;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpParams;

@NotThreadSafe
/* loaded from: classes.dex */
public abstract class AbstractHttpMessage implements HttpMessage {
    protected HeaderGroup headergroup;
    protected HttpParams params;

    protected AbstractHttpMessage(HttpParams params) {
        this.headergroup = new HeaderGroup();
        this.params = params;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractHttpMessage() {
        this(null);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public boolean containsHeader(String name) {
        return this.headergroup.containsHeader(name);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public Header[] getHeaders(String name) {
        return this.headergroup.getHeaders(name);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public Header getFirstHeader(String name) {
        return this.headergroup.getFirstHeader(name);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public Header getLastHeader(String name) {
        return this.headergroup.getLastHeader(name);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public Header[] getAllHeaders() {
        return this.headergroup.getAllHeaders();
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void addHeader(Header header) {
        this.headergroup.addHeader(header);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void addHeader(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Header name may not be null");
        }
        this.headergroup.addHeader(new BasicHeader(name, value));
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void setHeader(Header header) {
        this.headergroup.updateHeader(header);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void setHeader(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Header name may not be null");
        }
        this.headergroup.updateHeader(new BasicHeader(name, value));
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void setHeaders(Header[] headers) {
        this.headergroup.setHeaders(headers);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void removeHeader(Header header) {
        this.headergroup.removeHeader(header);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void removeHeaders(String name) {
        if (name != null) {
            HeaderIterator i = this.headergroup.iterator();
            while (i.hasNext()) {
                Header header = i.nextHeader();
                if (name.equalsIgnoreCase(header.getName())) {
                    i.remove();
                }
            }
        }
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public HeaderIterator headerIterator() {
        return this.headergroup.iterator();
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public HeaderIterator headerIterator(String name) {
        return this.headergroup.iterator(name);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = new BasicHttpParams();
        }
        return this.params;
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public void setParams(HttpParams params) {
        if (params == null) {
            throw new IllegalArgumentException("HTTP parameters may not be null");
        }
        this.params = params;
    }
}