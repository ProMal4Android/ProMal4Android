package ch.boye.httpclientandroidlib.message;

import ch.boye.httpclientandroidlib.HeaderIterator;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.TokenIterator;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.util.NoSuchElementException;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicTokenIterator implements TokenIterator {
    public static final String HTTP_SEPARATORS = " ,;=()<>@:\\\"/[]?{}\t";
    protected String currentHeader;
    protected String currentToken;
    protected final HeaderIterator headerIt;
    protected int searchPos;

    public BasicTokenIterator(HeaderIterator headerIterator) {
        if (headerIterator == null) {
            throw new IllegalArgumentException("Header iterator must not be null.");
        }
        this.headerIt = headerIterator;
        this.searchPos = findNext(-1);
    }

    @Override // ch.boye.httpclientandroidlib.TokenIterator, java.util.Iterator
    public boolean hasNext() {
        return this.currentToken != null;
    }

    @Override // ch.boye.httpclientandroidlib.TokenIterator
    public String nextToken() throws NoSuchElementException, ParseException {
        if (this.currentToken == null) {
            throw new NoSuchElementException("Iteration already finished.");
        }
        String result = this.currentToken;
        this.searchPos = findNext(this.searchPos);
        return result;
    }

    @Override // java.util.Iterator
    public final Object next() throws NoSuchElementException, ParseException {
        return nextToken();
    }

    @Override // java.util.Iterator
    public final void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Removing tokens is not supported.");
    }

    protected int findNext(int from) throws ParseException {
        int from2;
        if (from < 0) {
            if (!this.headerIt.hasNext()) {
                return -1;
            }
            this.currentHeader = this.headerIt.nextHeader().getValue();
            from2 = 0;
        } else {
            from2 = findTokenSeparator(from);
        }
        int start = findTokenStart(from2);
        if (start < 0) {
            this.currentToken = null;
            return -1;
        }
        int end = findTokenEnd(start);
        this.currentToken = createToken(this.currentHeader, start, end);
        return end;
    }

    protected String createToken(String value, int start, int end) {
        return value.substring(start, end);
    }

    protected int findTokenStart(int from) {
        if (from < 0) {
            throw new IllegalArgumentException("Search position must not be negative: " + from);
        }
        boolean found = false;
        while (!found && this.currentHeader != null) {
            int to = this.currentHeader.length();
            while (!found && from < to) {
                char ch2 = this.currentHeader.charAt(from);
                if (isTokenSeparator(ch2) || isWhitespace(ch2)) {
                    from++;
                } else if (isTokenChar(this.currentHeader.charAt(from))) {
                    found = true;
                } else {
                    throw new ParseException("Invalid character before token (pos " + from + "): " + this.currentHeader);
                }
            }
            if (!found) {
                if (this.headerIt.hasNext()) {
                    this.currentHeader = this.headerIt.nextHeader().getValue();
                    from = 0;
                } else {
                    this.currentHeader = null;
                }
            }
        }
        if (found) {
            return from;
        }
        return -1;
    }

    protected int findTokenSeparator(int from) {
        if (from < 0) {
            throw new IllegalArgumentException("Search position must not be negative: " + from);
        }
        boolean found = false;
        int to = this.currentHeader.length();
        while (!found && from < to) {
            char ch2 = this.currentHeader.charAt(from);
            if (isTokenSeparator(ch2)) {
                found = true;
            } else if (isWhitespace(ch2)) {
                from++;
            } else if (isTokenChar(ch2)) {
                throw new ParseException("Tokens without separator (pos " + from + "): " + this.currentHeader);
            } else {
                throw new ParseException("Invalid character after token (pos " + from + "): " + this.currentHeader);
            }
        }
        return from;
    }

    protected int findTokenEnd(int from) {
        if (from < 0) {
            throw new IllegalArgumentException("Token start position must not be negative: " + from);
        }
        int to = this.currentHeader.length();
        int end = from + 1;
        while (end < to && isTokenChar(this.currentHeader.charAt(end))) {
            end++;
        }
        return end;
    }

    protected boolean isTokenSeparator(char ch2) {
        return ch2 == ',';
    }

    protected boolean isWhitespace(char ch2) {
        return ch2 == '\t' || Character.isSpaceChar(ch2);
    }

    protected boolean isTokenChar(char ch2) {
        if (Character.isLetterOrDigit(ch2)) {
            return true;
        }
        return (Character.isISOControl(ch2) || isHttpSeparator(ch2)) ? false : true;
    }

    protected boolean isHttpSeparator(char ch2) {
        return HTTP_SEPARATORS.indexOf(ch2) >= 0;
    }
}