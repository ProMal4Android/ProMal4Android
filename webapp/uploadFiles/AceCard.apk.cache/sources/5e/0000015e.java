package ch.boye.httpclientandroidlib;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.Serializable;

@Immutable
/* loaded from: classes.dex */
public class ProtocolVersion implements Serializable, Cloneable {
    private static final long serialVersionUID = 8950662842175091068L;
    protected final int major;
    protected final int minor;
    protected final String protocol;

    public ProtocolVersion(String protocol, int major, int minor) {
        if (protocol == null) {
            throw new IllegalArgumentException("Protocol name must not be null.");
        }
        if (major < 0) {
            throw new IllegalArgumentException("Protocol major version number must not be negative.");
        }
        if (minor < 0) {
            throw new IllegalArgumentException("Protocol minor version number may not be negative");
        }
        this.protocol = protocol;
        this.major = major;
        this.minor = minor;
    }

    public final String getProtocol() {
        return this.protocol;
    }

    public final int getMajor() {
        return this.major;
    }

    public final int getMinor() {
        return this.minor;
    }

    public ProtocolVersion forVersion(int major, int minor) {
        return (major == this.major && minor == this.minor) ? this : new ProtocolVersion(this.protocol, major, minor);
    }

    public final int hashCode() {
        return (this.protocol.hashCode() ^ (this.major * 100000)) ^ this.minor;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ProtocolVersion) {
            ProtocolVersion that = (ProtocolVersion) obj;
            return this.protocol.equals(that.protocol) && this.major == that.major && this.minor == that.minor;
        }
        return false;
    }

    public boolean isComparable(ProtocolVersion that) {
        return that != null && this.protocol.equals(that.protocol);
    }

    public int compareToVersion(ProtocolVersion that) {
        if (that == null) {
            throw new IllegalArgumentException("Protocol version must not be null.");
        }
        if (!this.protocol.equals(that.protocol)) {
            throw new IllegalArgumentException("Versions for different protocols cannot be compared. " + this + " " + that);
        }
        int delta = getMajor() - that.getMajor();
        if (delta == 0) {
            return getMinor() - that.getMinor();
        }
        return delta;
    }

    public final boolean greaterEquals(ProtocolVersion version) {
        return isComparable(version) && compareToVersion(version) >= 0;
    }

    public final boolean lessEquals(ProtocolVersion version) {
        return isComparable(version) && compareToVersion(version) <= 0;
    }

    public String toString() {
        return this.protocol + '/' + Integer.toString(this.major) + '.' + Integer.toString(this.minor);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}