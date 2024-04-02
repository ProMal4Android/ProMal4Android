package org.spongycastle.crypto.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.StreamCipher;

/* loaded from: classes.dex */
public class CipherOutputStream extends FilterOutputStream {
    private byte[] buf;
    private BufferedBlockCipher bufferedBlockCipher;
    private byte[] oneByte;
    private StreamCipher streamCipher;

    public CipherOutputStream(OutputStream os, BufferedBlockCipher cipher) {
        super(os);
        this.oneByte = new byte[1];
        this.bufferedBlockCipher = cipher;
        this.buf = new byte[cipher.getBlockSize()];
    }

    public CipherOutputStream(OutputStream os, StreamCipher cipher) {
        super(os);
        this.oneByte = new byte[1];
        this.streamCipher = cipher;
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(int b) throws IOException {
        this.oneByte[0] = (byte) b;
        if (this.bufferedBlockCipher != null) {
            int len = this.bufferedBlockCipher.processBytes(this.oneByte, 0, 1, this.buf, 0);
            if (len != 0) {
                this.out.write(this.buf, 0, len);
                return;
            }
            return;
        }
        this.out.write(this.streamCipher.returnByte((byte) b));
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream
    public void write(byte[] b, int off, int len) throws IOException {
        if (this.bufferedBlockCipher != null) {
            byte[] buf = new byte[this.bufferedBlockCipher.getOutputSize(len)];
            int outLen = this.bufferedBlockCipher.processBytes(b, off, len, buf, 0);
            if (outLen != 0) {
                this.out.write(buf, 0, outLen);
                return;
            }
            return;
        }
        byte[] buf2 = new byte[len];
        this.streamCipher.processBytes(b, off, len, buf2, 0);
        this.out.write(buf2, 0, len);
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        super.flush();
    }

    @Override // java.io.FilterOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        byte[] buf;
        int outLen;
        try {
            if (this.bufferedBlockCipher != null && (outLen = this.bufferedBlockCipher.doFinal((buf = new byte[this.bufferedBlockCipher.getOutputSize(0)]), 0)) != 0) {
                this.out.write(buf, 0, outLen);
            }
            flush();
            super.close();
        } catch (Exception e) {
            throw new IOException("Error closing stream: " + e.toString());
        }
    }
}