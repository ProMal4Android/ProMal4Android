package ch.boye.httpclientandroidlib.entity.mime;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.ContentBody;
import ch.boye.httpclientandroidlib.message.BasicHeader;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Random;

/* loaded from: classes.dex */
public class MultipartEntity implements HttpEntity {
    private static final char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final ch.boye.httpclientandroidlib.Header contentType;
    private volatile boolean dirty;
    private long length;
    private final HttpMultipart multipart;

    public MultipartEntity(HttpMultipartMode mode, String boundary, Charset charset) {
        boundary = boundary == null ? generateBoundary() : boundary;
        this.multipart = new HttpMultipart("form-data", charset, boundary, mode == null ? HttpMultipartMode.STRICT : mode);
        this.contentType = new BasicHeader("Content-Type", generateContentType(boundary, charset));
        this.dirty = true;
    }

    public MultipartEntity(HttpMultipartMode mode) {
        this(mode, null, null);
    }

    public MultipartEntity() {
        this(HttpMultipartMode.STRICT, null, null);
    }

    protected String generateContentType(String boundary, Charset charset) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("multipart/form-data; boundary=");
        buffer.append(boundary);
        if (charset != null) {
            buffer.append(HTTP.CHARSET_PARAM);
            buffer.append(charset.name());
        }
        return buffer.toString();
    }

    protected String generateBoundary() {
        StringBuilder buffer = new StringBuilder();
        Random rand = new Random();
        int count = rand.nextInt(11) + 30;
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

    public void addPart(FormBodyPart bodyPart) {
        this.multipart.addBodyPart(bodyPart);
        this.dirty = true;
    }

    public void addPart(String name, ContentBody contentBody) {
        addPart(new FormBodyPart(name, contentBody));
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isRepeatable() {
        for (FormBodyPart part : this.multipart.getBodyParts()) {
            ContentBody body = part.getBody();
            if (body.getContentLength() < 0) {
                return false;
            }
        }
        return true;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isChunked() {
        return !isRepeatable();
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public boolean isStreaming() {
        return !isRepeatable();
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public long getContentLength() {
        if (this.dirty) {
            this.length = this.multipart.getTotalLength();
            this.dirty = false;
        }
        return this.length;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public ch.boye.httpclientandroidlib.Header getContentType() {
        return this.contentType;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public ch.boye.httpclientandroidlib.Header getContentEncoding() {
        return null;
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public void consumeContent() throws IOException, UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
        }
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Multipart form entity does not implement #getContent()");
    }

    @Override // ch.boye.httpclientandroidlib.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        this.multipart.writeTo(outstream);
    }
}