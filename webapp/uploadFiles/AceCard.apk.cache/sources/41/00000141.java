package ch.boye.httpclientandroidlib;

/* loaded from: classes.dex */
public interface Header {
    HeaderElement[] getElements() throws ParseException;

    String getName();

    String getValue();
}