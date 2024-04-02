package ch.boye.httpclientandroidlib.client;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import java.io.IOException;

@Immutable
/* loaded from: classes.dex */
public class ClientProtocolException extends IOException {
    private static final long serialVersionUID = -5596590843227115865L;

    public ClientProtocolException() {
    }

    public ClientProtocolException(String s) {
        super(s);
    }

    public ClientProtocolException(Throwable cause) {
        initCause(cause);
    }

    public ClientProtocolException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}