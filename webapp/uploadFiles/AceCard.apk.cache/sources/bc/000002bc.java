package ch.boye.httpclientandroidlib.impl.conn;

import ch.boye.httpclientandroidlib.HttpMessage;
import ch.boye.httpclientandroidlib.HttpResponseFactory;
import ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog;
import ch.boye.httpclientandroidlib.annotation.ThreadSafe;
import ch.boye.httpclientandroidlib.conn.params.ConnConnectionPNames;
import ch.boye.httpclientandroidlib.impl.io.AbstractMessageParser;
import ch.boye.httpclientandroidlib.io.SessionInputBuffer;
import ch.boye.httpclientandroidlib.message.LineParser;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;

@ThreadSafe
/* loaded from: classes.dex */
public class DefaultResponseParser extends AbstractMessageParser<HttpMessage> {
    private final CharArrayBuffer lineBuf;
    public HttpClientAndroidLog log;
    private final int maxGarbageLines;
    private final HttpResponseFactory responseFactory;

    public DefaultResponseParser(SessionInputBuffer buffer, LineParser parser, HttpResponseFactory responseFactory, HttpParams params) {
        super(buffer, parser, params);
        this.log = new HttpClientAndroidLog(getClass());
        if (responseFactory == null) {
            throw new IllegalArgumentException("Response factory may not be null");
        }
        this.responseFactory = responseFactory;
        this.lineBuf = new CharArrayBuffer(128);
        this.maxGarbageLines = getMaxGarbageLines(params);
    }

    protected int getMaxGarbageLines(HttpParams params) {
        return params.getIntParameter(ConnConnectionPNames.MAX_STATUS_LINE_GARBAGE, Integer.MAX_VALUE);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x004d, code lost:
        throw new ch.boye.httpclientandroidlib.ProtocolException("The server failed to respond with a valid HTTP response");
     */
    @Override // ch.boye.httpclientandroidlib.impl.io.AbstractMessageParser
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected ch.boye.httpclientandroidlib.HttpMessage parseHead(ch.boye.httpclientandroidlib.io.SessionInputBuffer r9) throws java.io.IOException, ch.boye.httpclientandroidlib.HttpException {
        /*
            r8 = this;
            r7 = -1
            r0 = 0
            r1 = 0
        L3:
            ch.boye.httpclientandroidlib.util.CharArrayBuffer r4 = r8.lineBuf
            r4.clear()
            ch.boye.httpclientandroidlib.util.CharArrayBuffer r4 = r8.lineBuf
            int r2 = r9.readLine(r4)
            if (r2 != r7) goto L1a
            if (r0 != 0) goto L1a
            ch.boye.httpclientandroidlib.NoHttpResponseException r4 = new ch.boye.httpclientandroidlib.NoHttpResponseException
            java.lang.String r5 = "The target server failed to respond"
            r4.<init>(r5)
            throw r4
        L1a:
            ch.boye.httpclientandroidlib.message.ParserCursor r1 = new ch.boye.httpclientandroidlib.message.ParserCursor
            r4 = 0
            ch.boye.httpclientandroidlib.util.CharArrayBuffer r5 = r8.lineBuf
            int r5 = r5.length()
            r1.<init>(r4, r5)
            ch.boye.httpclientandroidlib.message.LineParser r4 = r8.lineParser
            ch.boye.httpclientandroidlib.util.CharArrayBuffer r5 = r8.lineBuf
            boolean r4 = r4.hasProtocolVersion(r5, r1)
            if (r4 == 0) goto L40
            ch.boye.httpclientandroidlib.message.LineParser r4 = r8.lineParser
            ch.boye.httpclientandroidlib.util.CharArrayBuffer r5 = r8.lineBuf
            ch.boye.httpclientandroidlib.StatusLine r3 = r4.parseStatusLine(r5, r1)
            ch.boye.httpclientandroidlib.HttpResponseFactory r4 = r8.responseFactory
            r5 = 0
            ch.boye.httpclientandroidlib.HttpResponse r4 = r4.newHttpResponse(r3, r5)
            return r4
        L40:
            if (r2 == r7) goto L46
            int r4 = r8.maxGarbageLines
            if (r0 < r4) goto L4e
        L46:
            ch.boye.httpclientandroidlib.ProtocolException r4 = new ch.boye.httpclientandroidlib.ProtocolException
            java.lang.String r5 = "The server failed to respond with a valid HTTP response"
            r4.<init>(r5)
            throw r4
        L4e:
            ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog r4 = r8.log
            boolean r4 = r4.isDebugEnabled()
            if (r4 == 0) goto L74
            ch.boye.httpclientandroidlib.androidextra.HttpClientAndroidLog r4 = r8.log
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Garbage in response: "
            java.lang.StringBuilder r5 = r5.append(r6)
            ch.boye.httpclientandroidlib.util.CharArrayBuffer r6 = r8.lineBuf
            java.lang.String r6 = r6.toString()
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r5 = r5.toString()
            r4.debug(r5)
        L74:
            int r0 = r0 + 1
            goto L3
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.boye.httpclientandroidlib.impl.conn.DefaultResponseParser.parseHead(ch.boye.httpclientandroidlib.io.SessionInputBuffer):ch.boye.httpclientandroidlib.HttpMessage");
    }
}