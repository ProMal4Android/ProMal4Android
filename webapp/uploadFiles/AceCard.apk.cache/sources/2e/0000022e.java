package ch.boye.httpclientandroidlib.entity;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

@NotThreadSafe
/* loaded from: classes.dex */
public class StringEntity extends AbstractHttpEntity implements Cloneable {
    protected final byte[] content;

    public StringEntity(String string, ContentType contentType) {
        if (string == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        Charset charset = contentType != null ? contentType.getCharset() : null;
        charset = charset == null ? HTTP.DEF_CONTENT_CHARSET : charset;
        try {
            this.content = string.getBytes(charset.name());
            if (contentType != null) {
                setContentType(contentType.toString());
            }
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedCharsetException(charset.name());
        }
    }

    @Deprecated
    public StringEntity(String string, String mimeType, String charset) throws UnsupportedEncodingException {
        if (string == null) {
            throw new IllegalArgumentException("Source string may not be null");
        }
        mimeType = mimeType == null ? HTTP.PLAIN_TEXT_TYPE : mimeType;
        charset = charset == null ? "ISO-8859-1" : charset;
        this.content = string.getBytes(charset);
        setContentType(mimeType + HTTP.CHARSET_PARAM + charset);
    }

    public StringEntity(String string, String charset) throws UnsupportedEncodingException {
        this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }

    public StringEntity(String string, Charset charset) {
        this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
    }

    public StringEntity(String string) throws UnsupportedEncodingException {
        this(string, ContentType.DEFAULT_TEXT);
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isRepeatable() {
        return true;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public long getContentLength() {
        return this.content.length;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        outstream.write(this.content);
        outstream.flush();
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isStreaming() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}