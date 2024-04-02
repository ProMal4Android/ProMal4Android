package ch.boye.httpclientandroidlib.impl.io;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.io.IOException;
import java.net.Socket;

@NotThreadSafe
/* loaded from: classes.dex */
public class SocketOutputBuffer extends AbstractSessionOutputBuffer {
    public SocketOutputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
        if (socket == null) {
            throw new IllegalArgumentException("Socket may not be null");
        }
        buffersize = buffersize < 0 ? socket.getSendBufferSize() : buffersize;
        init(socket.getOutputStream(), buffersize < 1024 ? 1024 : buffersize, params);
    }
}