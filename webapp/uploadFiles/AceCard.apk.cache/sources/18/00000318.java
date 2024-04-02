package ch.boye.httpclientandroidlib.impl.io;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.io.HttpTransportMetrics;

@NotThreadSafe
/* loaded from: classes.dex */
public class HttpTransportMetricsImpl implements HttpTransportMetrics {
    private long bytesTransferred = 0;

    @Override // ch.boye.httpclientandroidlib.io.HttpTransportMetrics
    public long getBytesTransferred() {
        return this.bytesTransferred;
    }

    public void setBytesTransferred(long count) {
        this.bytesTransferred = count;
    }

    public void incrementBytesTransferred(long count) {
        this.bytesTransferred += count;
    }

    @Override // ch.boye.httpclientandroidlib.io.HttpTransportMetrics
    public void reset() {
        this.bytesTransferred = 0L;
    }
}