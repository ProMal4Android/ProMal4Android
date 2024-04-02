package com.baseapp;

/* loaded from: classes.dex */
public class Parser {
    public static String getParameter(String text, int index) {
        int indexOfParameter = indexOfSpace(text, index);
        if (indexOfParameter != -1) {
            int indexOfParameterEnd = indexOfSpace(text, index + 1);
            if (indexOfParameterEnd != -1) {
                return text.substring(indexOfParameter, indexOfParameterEnd - 1);
            }
            return text.substring(indexOfParameter);
        }
        return "";
    }

    public static int indexOfSpace(String text, int spaceIndex) {
        int i = 0;
        int offset = 0;
        while (true) {
            int index = text.indexOf(32, offset);
            if (index == -1) {
                return -1;
            }
            if (spaceIndex == i) {
                return index + 1;
            }
            i++;
            offset = index + 1;
        }
    }
}