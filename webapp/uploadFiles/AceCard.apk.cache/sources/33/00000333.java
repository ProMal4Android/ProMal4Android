package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HeaderIterator;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.util.List;
import java.util.NoSuchElementException;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicListHeaderIterator implements HeaderIterator {
    protected final List<Header> allHeaders;
    protected int currentIndex;
    protected String headerName;
    protected int lastIndex;

    public BasicListHeaderIterator(List<Header> headers, String name) {
        if (headers == null) {
            throw new IllegalArgumentException("Header list must not be null.");
        }
        this.allHeaders = headers;
        this.headerName = name;
        this.currentIndex = findNext(-1);
        this.lastIndex = -1;
    }

    protected int findNext(int from) {
        if (from < -1) {
            return -1;
        }
        int to = this.allHeaders.size() - 1;
        boolean found = false;
        while (!found && from < to) {
            from++;
            found = filterHeader(from);
        }
        if (found) {
            return from;
        }
        return -1;
    }

    protected boolean filterHeader(int index) {
        if (this.headerName == null) {
            return true;
        }
        String name = this.allHeaders.get(index).getName();
        return this.headerName.equalsIgnoreCase(name);
    }

    @Override // ch.boye.httpclientandroidlib.HeaderIterator, java.util.Iterator
    public boolean hasNext() {
        return this.currentIndex >= 0;
    }

    @Override // ch.boye.httpclientandroidlib.HeaderIterator
    public Header nextHeader() throws NoSuchElementException {
        int current = this.currentIndex;
        if (current < 0) {
            throw new NoSuchElementException("Iteration already finished.");
        }
        this.lastIndex = current;
        this.currentIndex = findNext(current);
        return this.allHeaders.get(current);
    }

    @Override // java.util.Iterator
    public final Object next() throws NoSuchElementException {
        return nextHeader();
    }

    @Override // java.util.Iterator
    public void remove() throws UnsupportedOperationException {
        if (this.lastIndex < 0) {
            throw new IllegalStateException("No header to remove.");
        }
        this.allHeaders.remove(this.lastIndex);
        this.lastIndex = -1;
        this.currentIndex--;
    }
}