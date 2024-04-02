package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
class TlsInputStream extends InputStream {
    private byte[] buf = new byte[1];
    private TlsProtocolHandler handler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsInputStream(TlsProtocolHandler handler) {
        this.handler = null;
        this.handler = handler;
    }

    @Override // java.io.InputStream
    public int read(byte[] buf, int offset, int len) throws IOException {
        return this.handler.readApplicationData(buf, offset, len);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (read(this.buf) < 0) {
            return -1;
        }
        return this.buf[0] & 255;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.handler.close();
    }
}