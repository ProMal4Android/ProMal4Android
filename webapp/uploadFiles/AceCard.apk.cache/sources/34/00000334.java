package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.util.LangUtils;
import java.io.Serializable;

@Immutable
/* loaded from: classes.dex */
public class BasicNameValuePair implements NameValuePair, Cloneable, Serializable {
    private static final long serialVersionUID = -6437800749411518984L;
    private final String name;
    private final String value;

    public BasicNameValuePair(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        this.value = value;
    }

    @Override // ch.boye.httpclientandroidlib.NameValuePair
    public String getName() {
        return this.name;
    }

    @Override // ch.boye.httpclientandroidlib.NameValuePair
    public String getValue() {
        return this.value;
    }

    public String toString() {
        if (this.value == null) {
            return this.name;
        }
        int len = this.name.length() + 1 + this.value.length();
        StringBuilder buffer = new StringBuilder(len);
        buffer.append(this.name);
        buffer.append("=");
        buffer.append(this.value);
        return buffer.toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof NameValuePair) {
            BasicNameValuePair that = (BasicNameValuePair) object;
            return this.name.equals(that.name) && LangUtils.equals(this.value, that.value);
        }
        return false;
    }

    public int hashCode() {
        int hash = LangUtils.hashCode(17, this.name);
        return LangUtils.hashCode(hash, this.value);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}