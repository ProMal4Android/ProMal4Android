package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.ProtocolVersion;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.util.CharArrayBuffer;
import java.io.Serializable;

@Immutable
/* loaded from: classes.dex */
public class BasicStatusLine implements StatusLine, Cloneable, Serializable {
    private static final long serialVersionUID = -2443303766890459269L;
    private final ProtocolVersion protoVersion;
    private final String reasonPhrase;
    private final int statusCode;

    public BasicStatusLine(ProtocolVersion version, int statusCode, String reasonPhrase) {
        if (version == null) {
            throw new IllegalArgumentException("Protocol version may not be null.");
        }
        if (statusCode < 0) {
            throw new IllegalArgumentException("Status code may not be negative.");
        }
        this.protoVersion = version;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    @Override // ch.boye.httpclientandroidlib.StatusLine
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override // ch.boye.httpclientandroidlib.StatusLine
    public ProtocolVersion getProtocolVersion() {
        return this.protoVersion;
    }

    @Override // ch.boye.httpclientandroidlib.StatusLine
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public String toString() {
        return BasicLineFormatter.DEFAULT.formatStatusLine((CharArrayBuffer) null, this).toString();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}