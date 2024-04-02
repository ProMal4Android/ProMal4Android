package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.ReasonPhraseCatalog;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.util.Locale;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicHttpResponse extends AbstractHttpMessage implements HttpResponse {
    private HttpEntity entity;
    private Locale locale;
    private ReasonPhraseCatalog reasonCatalog;
    private StatusLine statusline;

    public BasicHttpResponse(StatusLine statusline, ReasonPhraseCatalog catalog, Locale locale) {
        if (statusline == null) {
            throw new IllegalArgumentException("Status line may not be null.");
        }
        this.statusline = statusline;
        this.reasonCatalog = catalog;
        this.locale = locale == null ? Locale.getDefault() : locale;
    }

    public BasicHttpResponse(StatusLine statusline) {
        this(statusline, (ReasonPhraseCatalog) null, (Locale) null);
    }

    public BasicHttpResponse(ProtocolVersion ver, int code, String reason) {
        this(new BasicStatusLine(ver, code, reason), (ReasonPhraseCatalog) null, (Locale) null);
    }

    @Override // ch.boye.httpclientandroidlib.HttpMessage
    public ProtocolVersion getProtocolVersion() {
        return this.statusline.getProtocolVersion();
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public StatusLine getStatusLine() {
        return this.statusline;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public HttpEntity getEntity() {
        return this.entity;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public Locale getLocale() {
        return this.locale;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusLine(StatusLine statusline) {
        if (statusline == null) {
            throw new IllegalArgumentException("Status line may not be null");
        }
        this.statusline = statusline;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusLine(ProtocolVersion ver, int code) {
        this.statusline = new BasicStatusLine(ver, code, getReason(code));
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
        this.statusline = new BasicStatusLine(ver, code, reason);
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setStatusCode(int code) {
        ProtocolVersion ver = this.statusline.getProtocolVersion();
        this.statusline = new BasicStatusLine(ver, code, getReason(code));
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setReasonPhrase(String reason) {
        if (reason != null && (reason.indexOf(10) >= 0 || reason.indexOf(13) >= 0)) {
            throw new IllegalArgumentException("Line break in reason phrase.");
        }
        this.statusline = new BasicStatusLine(this.statusline.getProtocolVersion(), this.statusline.getStatusCode(), reason);
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    @Override // ch.boye.httpclientandroidlib.HttpResponse
    public void setLocale(Locale loc) {
        if (loc == null) {
            throw new IllegalArgumentException("Locale may not be null.");
        }
        this.locale = loc;
        int code = this.statusline.getStatusCode();
        this.statusline = new BasicStatusLine(this.statusline.getProtocolVersion(), code, getReason(code));
    }

    protected String getReason(int code) {
        if (this.reasonCatalog == null) {
            return null;
        }
        return this.reasonCatalog.getReason(code, this.locale);
    }

    public String toString() {
        return this.statusline + " " + this.headergroup;
    }
}