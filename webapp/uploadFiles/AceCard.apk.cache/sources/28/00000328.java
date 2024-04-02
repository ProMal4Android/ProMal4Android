package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.io.Serializable;

@Immutable
/* loaded from: classes.dex */
public class BasicHeader implements Header, Cloneable, Serializable {
    private static final long serialVersionUID = -5427236326487562174L;
    private final String name;
    private final String value;

    public BasicHeader(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        this.value = value;
    }

    @Override // ch.boye.httpclientandroidlib.Header
    public String getName() {
        return this.name;
    }

    @Override // ch.boye.httpclientandroidlib.Header
    public String getValue() {
        return this.value;
    }

    public String toString() {
        return BasicLineFormatter.DEFAULT.formatHeader((CharArrayBuffer) null, this).toString();
    }

    @Override // ch.boye.httpclientandroidlib.Header
    public HeaderElement[] getElements() throws ParseException {
        return this.value != null ? BasicHeaderValueParser.parseElements(this.value, (HeaderValueParser) null) : new HeaderElement[0];
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}