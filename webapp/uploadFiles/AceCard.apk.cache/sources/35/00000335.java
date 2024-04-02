package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.RequestLine;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.io.Serializable;

@Immutable
/* loaded from: classes.dex */
public class BasicRequestLine implements RequestLine, Cloneable, Serializable {
    private static final long serialVersionUID = 2810581718468737193L;
    private final String method;
    private final ProtocolVersion protoversion;
    private final String uri;

    public BasicRequestLine(String method, String uri, ProtocolVersion version) {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null.");
        }
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null.");
        }
        if (version == null) {
            throw new IllegalArgumentException("Protocol version must not be null.");
        }
        this.method = method;
        this.uri = uri;
        this.protoversion = version;
    }

    @Override // ch.boye.httpclientandroidlib.RequestLine
    public String getMethod() {
        return this.method;
    }

    @Override // ch.boye.httpclientandroidlib.RequestLine
    public ProtocolVersion getProtocolVersion() {
        return this.protoversion;
    }

    @Override // ch.boye.httpclientandroidlib.RequestLine
    public String getUri() {
        return this.uri;
    }

    public String toString() {
        return BasicLineFormatter.DEFAULT.formatRequestLine((CharArrayBuffer) null, this).toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}