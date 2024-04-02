package org.spongycastle.util;

/* loaded from: classes.dex */
public interface Selector extends Cloneable {
    Object clone();

    boolean match(Object obj);
}