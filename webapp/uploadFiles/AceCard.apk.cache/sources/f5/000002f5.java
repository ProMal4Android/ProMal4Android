package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.message.BasicHeaderElement;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.message.ParserCursor;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.util.ArrayList;
import java.util.List;

@Immutable
/* loaded from: classes.dex */
public class NetscapeDraftHeaderParser {
    public static final NetscapeDraftHeaderParser DEFAULT = new NetscapeDraftHeaderParser();

    public HeaderElement parseHeader(CharArrayBuffer buffer, ParserCursor cursor) throws ParseException {
        if (buffer == null) {
            throw new IllegalArgumentException("Char array buffer may not be null");
        }
        if (cursor == null) {
            throw new IllegalArgumentException("Parser cursor may not be null");
        }
        NameValuePair nvp = parseNameValuePair(buffer, cursor);
        List<NameValuePair> params = new ArrayList<>();
        while (!cursor.atEnd()) {
            NameValuePair param = parseNameValuePair(buffer, cursor);
            params.add(param);
        }
        return new BasicHeaderElement(nvp.getName(), nvp.getValue(), (NameValuePair[]) params.toArray(new NameValuePair[params.size()]));
    }

    private NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor) {
        String name;
        boolean terminated = false;
        int pos = cursor.getPos();
        int indexFrom = cursor.getPos();
        int indexTo = cursor.getUpperBound();
        while (true) {
            if (pos < indexTo) {
                char ch2 = buffer.charAt(pos);
                if (ch2 == '=') {
                    break;
                } else if (ch2 == ';') {
                    terminated = true;
                    break;
                } else {
                    pos++;
                }
            } else {
                break;
            }
        }
        if (pos == indexTo) {
            terminated = true;
            name = buffer.substringTrimmed(indexFrom, indexTo);
        } else {
            name = buffer.substringTrimmed(indexFrom, pos);
            pos++;
        }
        if (terminated) {
            cursor.updatePos(pos);
            return new BasicNameValuePair(name, null);
        }
        int i1 = pos;
        while (true) {
            if (pos >= indexTo) {
                break;
            } else if (buffer.charAt(pos) == ';') {
                terminated = true;
                break;
            } else {
                pos++;
            }
        }
        int i2 = pos;
        while (i1 < i2 && HTTP.isWhitespace(buffer.charAt(i1))) {
            i1++;
        }
        while (i2 > i1 && HTTP.isWhitespace(buffer.charAt(i2 - 1))) {
            i2--;
        }
        String value = buffer.substring(i1, i2);
        if (terminated) {
            pos++;
        }
        cursor.updatePos(pos);
        return new BasicNameValuePair(name, value);
    }
}