package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.FormattedHeader;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HeaderElementIterator;
import ch.boye.httpclientandroidlib.HeaderIterator;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.util.NoSuchElementException;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicHeaderElementIterator implements HeaderElementIterator {
    private CharArrayBuffer buffer;
    private HeaderElement currentElement;
    private ParserCursor cursor;
    private final HeaderIterator headerIt;
    private final HeaderValueParser parser;

    public BasicHeaderElementIterator(HeaderIterator headerIterator, HeaderValueParser parser) {
        this.currentElement = null;
        this.buffer = null;
        this.cursor = null;
        if (headerIterator == null) {
            throw new IllegalArgumentException("Header iterator may not be null");
        }
        if (parser == null) {
            throw new IllegalArgumentException("Parser may not be null");
        }
        this.headerIt = headerIterator;
        this.parser = parser;
    }

    public BasicHeaderElementIterator(HeaderIterator headerIterator) {
        this(headerIterator, BasicHeaderValueParser.DEFAULT);
    }

    private void bufferHeaderValue() {
        this.cursor = null;
        this.buffer = null;
        while (this.headerIt.hasNext()) {
            Header h = this.headerIt.nextHeader();
            if (h instanceof FormattedHeader) {
                this.buffer = ((FormattedHeader) h).getBuffer();
                this.cursor = new ParserCursor(0, this.buffer.length());
                this.cursor.updatePos(((FormattedHeader) h).getValuePos());
                return;
            }
            String value = h.getValue();
            if (value != null) {
                this.buffer = new CharArrayBuffer(value.length());
                this.buffer.append(value);
                this.cursor = new ParserCursor(0, this.buffer.length());
                return;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0028  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseNextElement() {
        /*
            r5 = this;
            r4 = 0
        L1:
            ch.boye.httpclientandroidlib.HeaderIterator r1 = r5.headerIt
            boolean r1 = r1.hasNext()
            if (r1 != 0) goto Ld
            ch.boye.httpclientandroidlib.message.ParserCursor r1 = r5.cursor
            if (r1 == 0) goto L44
        Ld:
            ch.boye.httpclientandroidlib.message.ParserCursor r1 = r5.cursor
            if (r1 == 0) goto L19
            ch.boye.httpclientandroidlib.message.ParserCursor r1 = r5.cursor
            boolean r1 = r1.atEnd()
            if (r1 == 0) goto L1c
        L19:
            r5.bufferHeaderValue()
        L1c:
            ch.boye.httpclientandroidlib.message.ParserCursor r1 = r5.cursor
            if (r1 == 0) goto L1
        L20:
            ch.boye.httpclientandroidlib.message.ParserCursor r1 = r5.cursor
            boolean r1 = r1.atEnd()
            if (r1 != 0) goto L45
            ch.boye.httpclientandroidlib.message.HeaderValueParser r1 = r5.parser
            ch.boye.httpclientandroidlib.util.CharArrayBuffer r2 = r5.buffer
            ch.boye.httpclientandroidlib.message.ParserCursor r3 = r5.cursor
            ch.boye.httpclientandroidlib.HeaderElement r0 = r1.parseHeaderElement(r2, r3)
            java.lang.String r1 = r0.getName()
            int r1 = r1.length()
            if (r1 != 0) goto L42
            java.lang.String r1 = r0.getValue()
            if (r1 == 0) goto L20
        L42:
            r5.currentElement = r0
        L44:
            return
        L45:
            ch.boye.httpclientandroidlib.message.ParserCursor r1 = r5.cursor
            boolean r1 = r1.atEnd()
            if (r1 == 0) goto L1
            r5.cursor = r4
            r5.buffer = r4
            goto L1
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.boye.httpclientandroidlib.message.BasicHeaderElementIterator.parseNextElement():void");
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElementIterator, java.util.Iterator
    public boolean hasNext() {
        if (this.currentElement == null) {
            parseNextElement();
        }
        return this.currentElement != null;
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElementIterator
    public HeaderElement nextElement() throws NoSuchElementException {
        if (this.currentElement == null) {
            parseNextElement();
        }
        if (this.currentElement == null) {
            throw new NoSuchElementException("No more header elements available");
        }
        HeaderElement element = this.currentElement;
        this.currentElement = null;
        return element;
    }

    @Override // java.util.Iterator
    public final Object next() throws NoSuchElementException {
        return nextElement();
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Remove not supported");
    }
}