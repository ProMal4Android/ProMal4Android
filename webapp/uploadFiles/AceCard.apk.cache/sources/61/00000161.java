package ch.boye.httpclientandroidlib;

/* loaded from: classes.dex */
public interface StatusLine {
    ProtocolVersion getProtocolVersion();

    String getReasonPhrase();

    int getStatusCode();
}