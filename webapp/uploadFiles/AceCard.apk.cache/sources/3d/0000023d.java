package ch.boye.httpclientandroidlib.entity.mime.content;

import ch.boye.httpclientandroidlib.entity.mime.MIME;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public class StringBody extends AbstractContentBody {
    private final Charset charset;
    private final byte[] content;

    public static StringBody create(String text, String mimeType, Charset charset) throws IllegalArgumentException {
        try {
            return new StringBody(text, mimeType, charset);
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalArgumentException("Charset " + charset + " is not supported", ex);
        }
    }

    public static StringBody create(String text, Charset charset) throws IllegalArgumentException {
        return create(text, null, charset);
    }

    public static StringBody create(String text) throws IllegalArgumentException {
        return create(text, null, null);
    }

    public StringBody(String text, String mimeType, Charset charset) throws UnsupportedEncodingException {
        super(mimeType);
        if (text == null) {
            throw new IllegalArgumentException("Text may not be null");
        }
        charset = charset == null ? Charset.forName("US-ASCII") : charset;
        this.content = text.getBytes(charset.name());
        this.charset = charset;
    }

    public StringBody(String text, Charset charset) throws UnsupportedEncodingException {
        this(text, HTTP.PLAIN_TEXT_TYPE, charset);
    }

    public StringBody(String text) throws UnsupportedEncodingException {
        this(text, HTTP.PLAIN_TEXT_TYPE, null);
    }

    public Reader getReader() {
        return new InputStreamReader(new ByteArrayInputStream(this.content), this.charset);
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentBody
    public void writeTo(OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new ByteArrayInputStream(this.content);
        byte[] tmp = new byte[4096];
        while (true) {
            int l = in.read(tmp);
            if (l != -1) {
                out.write(tmp, 0, l);
            } else {
                out.flush();
                return;
            }
        }
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentDescriptor
    public String getTransferEncoding() {
        return MIME.ENC_8BIT;
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentDescriptor
    public String getCharset() {
        return this.charset.name();
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentDescriptor
    public long getContentLength() {
        return this.content.length;
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentBody
    public String getFilename() {
        return null;
    }
}