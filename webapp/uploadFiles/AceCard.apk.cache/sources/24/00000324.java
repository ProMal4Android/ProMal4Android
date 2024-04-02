package ch.boye.httpclientandroidlib.io;

/* loaded from: classes.dex */
public interface HttpTransportMetrics {
    long getBytesTransferred();

    void reset();
}