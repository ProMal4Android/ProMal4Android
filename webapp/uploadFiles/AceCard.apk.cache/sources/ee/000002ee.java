package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.annotation.Immutable;

@Immutable
/* loaded from: classes.dex */
public class DateParseException extends Exception {
    private static final long serialVersionUID = 4417696455000643370L;

    public DateParseException() {
    }

    public DateParseException(String message) {
        super(message);
    }
}