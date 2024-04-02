package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.util.LangUtils;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicHeaderElement implements HeaderElement, Cloneable {
    private final String name;
    private final NameValuePair[] parameters;
    private final String value;

    public BasicHeaderElement(String name, String value, NameValuePair[] parameters) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        this.name = name;
        this.value = value;
        if (parameters != null) {
            this.parameters = parameters;
        } else {
            this.parameters = new NameValuePair[0];
        }
    }

    public BasicHeaderElement(String name, String value) {
        this(name, value, null);
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElement
    public String getName() {
        return this.name;
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElement
    public String getValue() {
        return this.value;
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElement
    public NameValuePair[] getParameters() {
        return (NameValuePair[]) this.parameters.clone();
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElement
    public int getParameterCount() {
        return this.parameters.length;
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElement
    public NameValuePair getParameter(int index) {
        return this.parameters[index];
    }

    @Override // ch.boye.httpclientandroidlib.HeaderElement
    public NameValuePair getParameterByName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        for (int i = 0; i < this.parameters.length; i++) {
            NameValuePair current = this.parameters[i];
            if (current.getName().equalsIgnoreCase(name)) {
                return current;
            }
        }
        return null;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof HeaderElement) {
            BasicHeaderElement that = (BasicHeaderElement) object;
            return this.name.equals(that.name) && LangUtils.equals(this.value, that.value) && LangUtils.equals((Object[]) this.parameters, (Object[]) that.parameters);
        }
        return false;
    }

    public int hashCode() {
        int hash = LangUtils.hashCode(17, this.name);
        int hash2 = LangUtils.hashCode(hash, this.value);
        for (int i = 0; i < this.parameters.length; i++) {
            hash2 = LangUtils.hashCode(hash2, this.parameters[i]);
        }
        return hash2;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.name);
        if (this.value != null) {
            buffer.append("=");
            buffer.append(this.value);
        }
        for (int i = 0; i < this.parameters.length; i++) {
            buffer.append("; ");
            buffer.append(this.parameters[i]);
        }
        return buffer.toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}