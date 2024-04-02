package ch.boye.httpclientandroidlib.impl.entity;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpMessage;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.entity.ContentLengthStrategy;
import ch.boye.httpclientandroidlib.params.CoreProtocolPNames;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.HTTP;

@Immutable
/* loaded from: classes.dex */
public class LaxContentLengthStrategy implements ContentLengthStrategy {
    private final int implicitLen;

    public LaxContentLengthStrategy(int implicitLen) {
        this.implicitLen = implicitLen;
    }

    public LaxContentLengthStrategy() {
        this(-1);
    }

    @Override // ch.boye.httpclientandroidlib.entity.ContentLengthStrategy
    public long determineLength(HttpMessage message) throws HttpException {
        if (message == null) {
            throw new IllegalArgumentException("HTTP message may not be null");
        }
        HttpParams params = message.getParams();
        boolean strict = params.isParameterTrue(CoreProtocolPNames.STRICT_TRANSFER_ENCODING);
        Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
        if (transferEncodingHeader != null) {
            try {
                HeaderElement[] encodings = transferEncodingHeader.getElements();
                if (strict) {
                    for (HeaderElement headerElement : encodings) {
                        String encoding = headerElement.getName();
                        if (encoding != null && encoding.length() > 0 && !encoding.equalsIgnoreCase(HTTP.CHUNK_CODING) && !encoding.equalsIgnoreCase(HTTP.IDENTITY_CODING)) {
                            throw new ProtocolException("Unsupported transfer encoding: " + encoding);
                        }
                    }
                }
                int len = encodings.length;
                if (HTTP.IDENTITY_CODING.equalsIgnoreCase(transferEncodingHeader.getValue())) {
                    return -1L;
                }
                if (len > 0 && HTTP.CHUNK_CODING.equalsIgnoreCase(encodings[len - 1].getName())) {
                    return -2L;
                }
                if (strict) {
                    throw new ProtocolException("Chunk-encoding must be the last one applied");
                }
                return -1L;
            } catch (ParseException px) {
                throw new ProtocolException("Invalid Transfer-Encoding header value: " + transferEncodingHeader, px);
            }
        }
        Header contentLengthHeader = message.getFirstHeader("Content-Length");
        if (contentLengthHeader != null) {
            long contentlen = -1;
            Header[] headers = message.getHeaders("Content-Length");
            if (strict && headers.length > 1) {
                throw new ProtocolException("Multiple content length headers");
            }
            for (int i = headers.length - 1; i >= 0; i--) {
                Header header = headers[i];
                try {
                    contentlen = Long.parseLong(header.getValue());
                    break;
                } catch (NumberFormatException e) {
                    if (strict) {
                        throw new ProtocolException("Invalid content length: " + header.getValue());
                    }
                }
            }
            if (contentlen < 0) {
                return -1L;
            }
            return contentlen;
        }
        return this.implicitLen;
    }
}