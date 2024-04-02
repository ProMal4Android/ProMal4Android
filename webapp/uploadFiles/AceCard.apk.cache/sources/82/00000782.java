package org.spongycastle.util.test;

/* loaded from: classes.dex */
public final class NumberParsing {
    private NumberParsing() {
    }

    public static long decodeLongFromHex(String longAsString) {
        return (longAsString.charAt(1) == 'x' || longAsString.charAt(1) == 'X') ? Long.parseLong(longAsString.substring(2), 16) : Long.parseLong(longAsString, 16);
    }

    public static int decodeIntFromHex(String intAsString) {
        return (intAsString.charAt(1) == 'x' || intAsString.charAt(1) == 'X') ? Integer.parseInt(intAsString.substring(2), 16) : Integer.parseInt(intAsString, 16);
    }
}