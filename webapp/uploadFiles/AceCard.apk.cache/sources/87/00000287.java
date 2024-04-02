package ch.boye.httpclientandroidlib.impl.client;

/* loaded from: classes.dex */
class SystemClock implements Clock {
    @Override // ch.boye.httpclientandroidlib.impl.client.Clock
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}