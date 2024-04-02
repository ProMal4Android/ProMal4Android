package ch.boye.httpclientandroidlib;

/* loaded from: classes.dex */
public interface RequestLine {
    String getMethod();

    ProtocolVersion getProtocolVersion();

    String getUri();
}