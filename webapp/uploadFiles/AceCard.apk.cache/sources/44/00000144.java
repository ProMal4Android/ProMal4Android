package ch.boye.httpclientandroidlib;

import java.util.Iterator;

/* loaded from: classes.dex */
public interface HeaderIterator extends Iterator<Object> {
    @Override // java.util.Iterator
    boolean hasNext();

    Header nextHeader();
}