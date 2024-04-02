package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Immutable
/* loaded from: classes.dex */
public class Wire {
    public HttpClientAndroidLog log;

    public Wire(HttpClientAndroidLog log) {
        this.log = log;
    }

    private void wire(String header, InputStream instream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        while (true) {
            int ch2 = instream.read();
            if (ch2 == -1) {
                break;
            } else if (ch2 == 13) {
                buffer.append("[\\r]");
            } else if (ch2 == 10) {
                buffer.append("[\\n]\"");
                buffer.insert(0, "\"");
                buffer.insert(0, header);
                this.log.debug(buffer.toString());
                buffer.setLength(0);
            } else if (ch2 < 32 || ch2 > 127) {
                buffer.append("[0x");
                buffer.append(Integer.toHexString(ch2));
                buffer.append("]");
            } else {
                buffer.append((char) ch2);
            }
        }
        if (buffer.length() > 0) {
            buffer.append('\"');
            buffer.insert(0, '\"');
            buffer.insert(0, header);
            this.log.debug(buffer.toString());
        }
    }

    public boolean enabled() {
        return this.log.isDebugEnabled();
    }

    public void output(InputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        wire(">> ", outstream);
    }

    public void input(InputStream instream) throws IOException {
        if (instream == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        wire("<< ", instream);
    }

    public void output(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        wire(">> ", new ByteArrayInputStream(b, off, len));
    }

    public void input(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        wire("<< ", new ByteArrayInputStream(b, off, len));
    }

    public void output(byte[] b) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        wire(">> ", new ByteArrayInputStream(b));
    }

    public void input(byte[] b) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        wire("<< ", new ByteArrayInputStream(b));
    }

    public void output(int b) throws IOException {
        output(new byte[]{(byte) b});
    }

    public void input(int b) throws IOException {
        input(new byte[]{(byte) b});
    }

    @Deprecated
    public void output(String s) throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        output(s.getBytes());
    }

    @Deprecated
    public void input(String s) throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        input(s.getBytes());
    }
}