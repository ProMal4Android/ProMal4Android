package ch.boye.httpclientandroidlib.client.entity;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.entity.HttpEntityWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
abstract class DecompressingEntity extends HttpEntityWrapper {
    private static final int BUFFER_SIZE = 2048;
    private InputStream content;

    abstract InputStream decorate(InputStream inputStream) throws IOException;

    public DecompressingEntity(HttpEntity wrapped) {
        super(wrapped);
    }

    private InputStream getDecompressingStream() throws IOException {
        InputStream in = this.wrappedEntity.getContent();
        try {
            return decorate(in);
        } catch (IOException ex) {
            in.close();
            throw ex;
        }
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public InputStream getContent() throws IOException {
        if (this.wrappedEntity.isStreaming()) {
            if (this.content == null) {
                this.content = getDecompressingStream();
            }
            return this.content;
        }
        return getDecompressingStream();
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = getContent();
        try {
            byte[] buffer = new byte[2048];
            while (true) {
                int l = instream.read(buffer);
                if (l != -1) {
                    outstream.write(buffer, 0, l);
                } else {
                    return;
                }
            }
        } finally {
            instream.close();
        }
    }
}