package ch.boye.httpclientandroidlib.impl.entity;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpMessage;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.entity.ContentLengthStrategy;
import ch.boye.httpclientandroidlib.protocol.HTTP;

@Immutable
/* loaded from: classes.dex */
public class StrictContentLengthStrategy implements ContentLengthStrategy {
    private final int implicitLen;

    public StrictContentLengthStrategy(int implicitLen) {
        this.implicitLen = implicitLen;
    }

    public StrictContentLengthStrategy() {
        this(-1);
    }

    @Override // ch.boye.httpclientandroidlib.entity.ContentLengthStrategy
    public long determineLength(HttpMessage message) throws HttpException {
        if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        }
        Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            String s = transferEncodingHeader.getValue();
            if (HTTP.CHUNK_CODING.equalsIgnoreCase(s)) {
                if (message.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                    throw new ProtocolException("Chunked transfer encoding not allowed for " + message.getProtocolVersion());
                }
                return -2L;
            } else if (HTTP.IDENTITY_CODING.equalsIgnoreCase(s)) {
                return -1L;
            } else {
                throw new ProtocolException("Unsupported transfer encoding: " + s);
            }
        }
        Header contentLengthHeader = message.getFirstHeader("Content-Length");
        if (contentLengthHeader != null) {
            String s2 = contentLengthHeader.getValue();
            try {
                long len = Long.parseLong(s2);
                if (len < 0) {
                    throw new ProtocolException("Negative content length: " + s2);
                }
                return len;
            } catch (NumberFormatException e) {
                throw new ProtocolException("Invalid content length: " + s2);
            }
        }
        return this.implicitLen;
    }
}