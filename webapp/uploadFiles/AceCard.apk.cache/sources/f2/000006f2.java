package org.spongycastle.crypto.tls;

/* loaded from: classes.dex */
public class ByteQueue {
    private static final int INITBUFSIZE = 1024;
    private byte[] databuf = new byte[1024];
    private int skipped = 0;
    private int available = 0;

    public static final int nextTwoPow(int i) {
        int i2 = i | (i >> 1);
        int i3 = i2 | (i2 >> 2);
        int i4 = i3 | (i3 >> 4);
        int i5 = i4 | (i4 >> 8);
        return (i5 | (i5 >> 16)) + 1;
    }

    public void read(byte[] buf, int offset, int len, int skip) {
        if (this.available - skip < len) {
            throw new TlsRuntimeException("Not enough data to read");
        }
        if (buf.length - offset < len) {
            throw new TlsRuntimeException("Buffer size of " + buf.length + " is too small for a read of " + len + " bytes");
        }
        System.arraycopy(this.databuf, this.skipped + skip, buf, offset, len);
    }

    public void addData(byte[] data, int offset, int len) {
        if (this.skipped + this.available + len > this.databuf.length) {
            byte[] tmp = new byte[nextTwoPow(data.length)];
            System.arraycopy(this.databuf, this.skipped, tmp, 0, this.available);
            this.skipped = 0;
            this.databuf = tmp;
        }
        System.arraycopy(data, offset, this.databuf, this.skipped + this.available, len);
        this.available += len;
    }

    public void removeData(int i) {
        if (i > this.available) {
            throw new TlsRuntimeException("Cannot remove " + i + " bytes, only got " + this.available);
        }
        this.available -= i;
        this.skipped += i;
        if (this.skipped > this.databuf.length / 2) {
            System.arraycopy(this.databuf, this.skipped, this.databuf, 0, this.available);
            this.skipped = 0;
        }
    }

    public int size() {
        return this.available;
    }
}