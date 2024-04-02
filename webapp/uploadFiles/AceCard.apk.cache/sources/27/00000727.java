package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
class TlsOutputStream extends OutputStream {
    private byte[] buf = new byte[1];
    private TlsProtocolHandler handler;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsOutputStream(TlsProtocolHandler handler) {
        this.handler = handler;
    }

    @Override // java.io.OutputStream
    public void write(byte[] buf, int offset, int len) throws IOException {
        this.handler.writeData(buf, offset, len);
    }

    @Override // java.io.OutputStream
    public void write(int arg0) throws IOException {
        this.buf[0] = (byte) arg0;
        write(this.buf, 0, 1);
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.handler.close();
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.handler.flush();
    }
}