package ch.boye.httpclientandroidlib.entity;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicHttpEntity extends AbstractHttpEntity {
    private InputStream content;
    private long length = -1;

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public long getContentLength() {
        return this.length;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public InputStream getContent() throws IllegalStateException {
        if (this.content == null) {
            throw new IllegalStateException("Content has not been provided");
        }
        return this.content;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isRepeatable() {
        return false;
    }

    public void setContentLength(long len) {
        this.length = len;
    }

    public void setContent(InputStream instream) {
        this.content = instream;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = getContent();
        try {
            byte[] tmp = new byte[2048];
            while (true) {
                int l = instream.read(tmp);
                if (l != -1) {
                    outstream.write(tmp, 0, l);
                } else {
                    return;
                }
            }
        } finally {
            instream.close();
        }
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isStreaming() {
        return this.content != null;
    }

    @Override // ch.boye.httpclientandroidlib.entity.AbstractHttpEntity, ch.boye.httpclientandroidlib.HttpEntity
    @Deprecated
    public void consumeContent() throws IOException {
        if (this.content != null) {
            this.content.close();
        }
    }
}