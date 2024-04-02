package ch.boye.httpclientandroidlib.io;

import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.io.IOException;

/* loaded from: classes.dex */
public interface SessionOutputBuffer {
    void flush() throws IOException;

    HttpTransportMetrics getMetrics();

    void write(int i) throws IOException;

    void write(byte[] bArr) throws IOException;

    void write(byte[] bArr, int i, int i2) throws IOException;

    void writeLine(CharArrayBuffer charArrayBuffer) throws IOException;

    void writeLine(String str) throws IOException;
}