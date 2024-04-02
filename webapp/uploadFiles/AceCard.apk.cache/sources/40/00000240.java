package ch.boye.httpclientandroidlib.impl;

import ch.boye.httpclientandroidlib.ConnectionReuseStrategy;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderIterator;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.TokenIterator;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.message.BasicTokenIterator;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

@Immutable
/* loaded from: classes.dex */
public class DefaultConnectionReuseStrategy implements ConnectionReuseStrategy {
    @Override // ch.boye.httpclientandroidlib.ConnectionReuseStrategy
    public boolean keepAlive(HttpResponse response, HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("HTTP context may not be null.");
        }
        ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
        Header teh = response.getFirstHeader("Transfer-Encoding");
        if (teh != null) {
            if (!HTTP.CHUNK_CODING.equalsIgnoreCase(teh.getValue())) {
                return false;
            }
        } else if (canResponseHaveBody(response)) {
            Header[] clhs = response.getHeaders("Content-Length");
            if (clhs.length == 1) {
                Header clh = clhs[0];
                try {
                    int contentLen = Integer.parseInt(clh.getValue());
                    if (contentLen < 0) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                return false;
            }
        }
        HeaderIterator hit = response.headerIterator("Connection");
        if (!hit.hasNext()) {
            hit = response.headerIterator("Proxy-Connection");
        }
        if (hit.hasNext()) {
            try {
                TokenIterator ti = createTokenIterator(hit);
                boolean keepalive = false;
                while (ti.hasNext()) {
                    String token = ti.nextToken();
                    if (HTTP.CONN_CLOSE.equalsIgnoreCase(token)) {
                        return false;
                    }
                    if (HTTP.CONN_KEEP_ALIVE.equalsIgnoreCase(token)) {
                        keepalive = true;
                    }
                }
                if (keepalive) {
                    return true;
                }
            } catch (ParseException e2) {
                return false;
            }
        }
        return !ver.lessEquals(HttpVersion.HTTP_1_0);
    }

    protected TokenIterator createTokenIterator(HeaderIterator hit) {
        return new BasicTokenIterator(hit);
    }

    private boolean canResponseHaveBody(HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return (status < 200 || status == 204 || status == 304 || status == 205) ? false : true;
    }
}