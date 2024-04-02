package ch.boye.httpclientandroidlib;

import java.io.IOException;

/* loaded from: classes.dex */
public class ConnectionClosedException extends IOException {
    private static final long serialVersionUID = 617550366255636674L;

    public ConnectionClosedException(String message) {
        super(message);
    }
}