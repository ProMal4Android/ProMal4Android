package ch.boye.httpclientandroidlib.conn;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.InterruptedIOException;

@Immutable
/* loaded from: classes.dex */
public class ConnectTimeoutException extends InterruptedIOException {
    private static final long serialVersionUID = -4816682903149535989L;

    public ConnectTimeoutException() {
    }

    public ConnectTimeoutException(String message) {
        super(message);
    }
}