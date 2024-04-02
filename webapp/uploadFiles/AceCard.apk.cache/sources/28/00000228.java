package ch.boye.httpclientandroidlib.entity;

import ch.boye.httpclientandroidlib.Consts;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.utils.URLEncodedUtils;
import ch.boye.httpclientandroidlib.message.BasicHeaderValueParser;
import ch.boye.httpclientandroidlib.message.HeaderValueParser;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Locale;

@Immutable
/* loaded from: classes.dex */
public final class ContentType implements Serializable {
    private static final long serialVersionUID = -7768694718232371896L;
    private final Charset charset;
    private final String mimeType;
    public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
    public static final ContentType APPLICATION_FORM_URLENCODED = create(URLEncodedUtils.CONTENT_TYPE, Consts.ISO_8859_1);
    public static final ContentType APPLICATION_JSON = create("application/json", Consts.UTF_8);
    public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset) null);
    public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
    public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
    public static final ContentType APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
    public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
    public static final ContentType TEXT_HTML = create("text/html", Consts.ISO_8859_1);
    public static final ContentType TEXT_PLAIN = create(HTTP.PLAIN_TEXT_TYPE, Consts.ISO_8859_1);
    public static final ContentType TEXT_XML = create("text/xml", Consts.ISO_8859_1);
    public static final ContentType WILDCARD = create("*/*", (Charset) null);
    public static final ContentType DEFAULT_TEXT = TEXT_PLAIN;
    public static final ContentType DEFAULT_BINARY = APPLICATION_OCTET_STREAM;

    ContentType(String mimeType, Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.mimeType);
        if (this.charset != null) {
            buf.append(HTTP.CHARSET_PARAM);
            buf.append(this.charset.name());
        }
        return buf.toString();
    }

    private static boolean valid(String s) {
        for (int i = 0; i < s.length(); i++) {
            char ch2 = s.charAt(i);
            if (ch2 == '\"' || ch2 == ',' || ch2 == ';') {
                return false;
            }
        }
        return true;
    }

    public static ContentType create(String mimeType, Charset charset) {
        if (mimeType == null) {
            throw new IllegalArgumentException("MIME type may not be null");
        }
        String type = mimeType.trim().toLowerCase(Locale.US);
        if (type.length() == 0) {
            throw new IllegalArgumentException("MIME type may not be empty");
        }
        if (!valid(type)) {
            throw new IllegalArgumentException("MIME type may not contain reserved characters");
        }
        return new ContentType(type, charset);
    }

    public static ContentType create(String mimeType) {
        return new ContentType(mimeType, null);
    }

    public static ContentType create(String mimeType, String charset) throws UnsupportedCharsetException {
        return create(mimeType, (charset == null || charset.length() <= 0) ? null : Charset.forName(charset));
    }

    private static ContentType create(HeaderElement helem) {
        String mimeType = helem.getName();
        String charset = null;
        NameValuePair param = helem.getParameterByName("charset");
        if (param != null) {
            charset = param.getValue();
        }
        return create(mimeType, charset);
    }

    public static ContentType parse(String s) throws ParseException, UnsupportedCharsetException {
        if (s == null) {
            throw new IllegalArgumentException("Content type may not be null");
        }
        HeaderElement[] elements = BasicHeaderValueParser.parseElements(s, (HeaderValueParser) null);
        if (elements.length > 0) {
            return create(elements[0]);
        }
        throw new ParseException("Invalid content type: " + s);
    }

    public static ContentType get(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        Header header;
        if (entity == null || (header = entity.getContentType()) == null) {
            return null;
        }
        HeaderElement[] elements = header.getElements();
        if (elements.length > 0) {
            return create(elements[0]);
        }
        return null;
    }

    public static ContentType getOrDefault(HttpEntity entity) throws ParseException, UnsupportedCharsetException {
        ContentType contentType = get(entity);
        return contentType != null ? contentType : DEFAULT_TEXT;
    }
}