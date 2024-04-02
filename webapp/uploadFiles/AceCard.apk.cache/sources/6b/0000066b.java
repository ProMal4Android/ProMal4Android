package org.spongycastle.crypto.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.StreamCipher;

/* loaded from: classes.dex */
public class CipherInputStream extends FilterInputStream {
    private static final int INPUT_BUF_SIZE = 2048;
    private byte[] buf;
    private int bufOff;
    private BufferedBlockCipher bufferedBlockCipher;
    private boolean finalized;
    private byte[] inBuf;
    private int maxBuf;
    private StreamCipher streamCipher;

    public CipherInputStream(InputStream is, BufferedBlockCipher cipher) {
        super(is);
        this.bufferedBlockCipher = cipher;
        this.buf = new byte[cipher.getOutputSize(2048)];
        this.inBuf = new byte[2048];
    }

    public CipherInputStream(InputStream is, StreamCipher cipher) {
        super(is);
        this.streamCipher = cipher;
        this.buf = new byte[2048];
        this.inBuf = new byte[2048];
    }

    private int nextChunk() throws IOException {
        int available;
        int available2 = super.available();
        if (available2 <= 0) {
            available2 = 1;
        }
        if (available2 > this.inBuf.length) {
            available = super.read(this.inBuf, 0, this.inBuf.length);
        } else {
            available = super.read(this.inBuf, 0, available2);
        }
        if (available < 0) {
            if (this.finalized) {
                return -1;
            }
            try {
                if (this.bufferedBlockCipher != null) {
                    this.maxBuf = this.bufferedBlockCipher.doFinal(this.buf, 0);
                } else {
                    this.maxBuf = 0;
                }
                this.bufOff = 0;
                this.finalized = true;
                if (this.bufOff == this.maxBuf) {
                    return -1;
                }
            } catch (Exception e) {
                throw new IOException("error processing stream: " + e.toString());
            }
        } else {
            this.bufOff = 0;
            try {
                if (this.bufferedBlockCipher != null) {
                    this.maxBuf = this.bufferedBlockCipher.processBytes(this.inBuf, 0, available, this.buf, 0);
                } else {
                    this.streamCipher.processBytes(this.inBuf, 0, available, this.buf, 0);
                    this.maxBuf = available;
                }
                if (this.maxBuf == 0) {
                    return nextChunk();
                }
            } catch (Exception e2) {
                throw new IOException("error processing stream: " + e2.toString());
            }
        }
        return this.maxBuf;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read() throws IOException {
        if (this.bufOff != this.maxBuf || nextChunk() >= 0) {
            byte[] bArr = this.buf;
            int i = this.bufOff;
            this.bufOff = i + 1;
            return bArr[i] & 255;
        }
        return -1;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.bufOff == this.maxBuf && nextChunk() < 0) {
            return -1;
        }
        int available = this.maxBuf - this.bufOff;
        if (len > available) {
            System.arraycopy(this.buf, this.bufOff, b, off, available);
            this.bufOff = this.maxBuf;
            return available;
        }
        System.arraycopy(this.buf, this.bufOff, b, off, len);
        this.bufOff += len;
        return len;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0L;
        }
        int available = this.maxBuf - this.bufOff;
        if (n > available) {
            this.bufOff = this.maxBuf;
            return available;
        }
        this.bufOff += (int) n;
        return (int) n;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public int available() throws IOException {
        return this.maxBuf - this.bufOff;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public boolean markSupported() {
        return false;
    }
}