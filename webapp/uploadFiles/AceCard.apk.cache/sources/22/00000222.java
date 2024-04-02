package ch.boye.httpclientandroidlib.entity;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.message.BasicHeader;
import java.io.IOException;

@NotThreadSafe
/* loaded from: classes.dex */
public abstract class AbstractHttpEntity implements HttpEntity {
    protected boolean chunked;
    protected Header contentEncoding;
    protected Header contentType;

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public Header getContentType() {
        return this.contentType;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public Header getContentEncoding() {
        return this.contentEncoding;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isChunked() {
        return this.chunked;
    }

    public void setContentType(Header contentType) {
        this.contentType = contentType;
    }

    public void setContentType(String ctString) {
        Header h = null;
        if (ctString != null) {
            h = new BasicHeader("Content-Type", ctString);
        }
        setContentType(h);
    }

    public void setContentEncoding(Header contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public void setContentEncoding(String ceString) {
        Header h = null;
        if (ceString != null) {
            h = new BasicHeader("Content-Encoding", ceString);
        }
        setContentEncoding(h);
    }

    public void setChunked(boolean b) {
        this.chunked = b;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    @Deprecated
    public void consumeContent() throws IOException {
    }
}