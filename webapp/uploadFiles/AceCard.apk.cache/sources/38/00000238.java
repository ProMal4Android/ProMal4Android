package ch.boye.httpclientandroidlib.entity.mime.content;

import ch.boye.httpclientandroidlib.entity.mime.MIME;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class ByteArrayBody extends AbstractContentBody {
    private final byte[] data;
    private final String filename;

    public ByteArrayBody(byte[] data, String mimeType, String filename) {
        super(mimeType);
        if (data == null) {
            throw new IllegalArgumentException("byte[] may not be null");
        }
        this.data = data;
        this.filename = filename;
    }

    public ByteArrayBody(byte[] data, String filename) {
        this(data, "application/octet-stream", filename);
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentBody
    public String getFilename() {
        return this.filename;
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentBody
    public void writeTo(OutputStream out) throws IOException {
        out.write(this.data);
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentDescriptor
    public String getCharset() {
        return null;
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentDescriptor
    public String getTransferEncoding() {
        return MIME.ENC_BINARY;
    }

    @Override // ch.boye.httpclientandroidlib.entity.mime.content.ContentDescriptor
    public long getContentLength() {
        return this.data.length;
    }
}