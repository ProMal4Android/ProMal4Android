package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.Consts;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.io.EofSensor;
import ch.boye.httpclientandroidlib.io.HttpTransportMetrics;
import ch.boye.httpclientandroidlib.io.SessionInputBuffer;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class LoggingSessionInputBuffer implements SessionInputBuffer, EofSensor {
    private final String charset;
    private final EofSensor eofSensor;
    private final SessionInputBuffer in;
    private final Wire wire;

    public LoggingSessionInputBuffer(SessionInputBuffer in, Wire wire, String charset) {
        this.in = in;
        this.eofSensor = in instanceof EofSensor ? (EofSensor) in : null;
        this.wire = wire;
        this.charset = charset == null ? Consts.ASCII.name() : charset;
    }

    public LoggingSessionInputBuffer(SessionInputBuffer in, Wire wire) {
        this(in, wire, null);
    }

    @Override // ch.boye.httpclientandroidlib.io.SessionInputBuffer
    public boolean isDataAvailable(int timeout) throws IOException {
        return this.in.isDataAvailable(timeout);
    }

    @Override // ch.boye.httpclientandroidlib.io.SessionInputBuffer
    public int read(byte[] b, int off, int len) throws IOException {
        int l = this.in.read(b, off, len);
        if (this.wire.enabled() && l > 0) {
            this.wire.input(b, off, l);
        }
        return l;
    }

    @Override // ch.boye.httpclientandroidlib.io.SessionInputBuffer
    public int read() throws IOException {
        int l = this.in.read();
        if (this.wire.enabled() && l != -1) {
            this.wire.input(l);
        }
        return l;
    }

    @Override // ch.boye.httpclientandroidlib.io.SessionInputBuffer
    public int read(byte[] b) throws IOException {
        int l = this.in.read(b);
        if (this.wire.enabled() && l > 0) {
            this.wire.input(b, 0, l);
        }
        return l;
    }

    @Override // ch.boye.httpclientandroidlib.io.SessionInputBuffer
    public String readLine() throws IOException {
        String s = this.in.readLine();
        if (this.wire.enabled() && s != null) {
            String tmp = s + "\r\n";
            this.wire.input(tmp.getBytes(this.charset));
        }
        return s;
    }

    @Override // ch.boye.httpclientandroidlib.io.SessionInputBuffer
    public int readLine(CharArrayBuffer buffer) throws IOException {
        int l = this.in.readLine(buffer);
        if (this.wire.enabled() && l >= 0) {
            int pos = buffer.length() - l;
            String s = new String(buffer.buffer(), pos, l);
            String tmp = s + "\r\n";
            this.wire.input(tmp.getBytes(this.charset));
        }
        return l;
    }

    @Override // ch.boye.httpclientandroidlib.io.SessionInputBuffer
    public HttpTransportMetrics getMetrics() {
        return this.in.getMetrics();
    }

    @Override // ch.boye.httpclientandroidlib.io.EofSensor
    public boolean isEof() {
        if (this.eofSensor != null) {
            return this.eofSensor.isEof();
        }
        return false;
    }
}