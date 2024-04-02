package ch.boye.httpclientandroidlib.impl.entity;

import ch.boye.httpclientandroidlib.HttpException;
import ch.boye.httpclientandroidlib.HttpMessage;
import ch.boye.httpclientandroidlib.ProtocolException;
import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.entity.ContentLengthStrategy;

@Immutable
/* loaded from: classes.dex */
public class DisallowIdentityContentLengthStrategy implements ContentLengthStrategy {
    private final ContentLengthStrategy contentLengthStrategy;

    public DisallowIdentityContentLengthStrategy(ContentLengthStrategy contentLengthStrategy) {
        this.contentLengthStrategy = contentLengthStrategy;
    }

    @Override // ch.boye.httpclientandroidlib.entity.ContentLengthStrategy
    public long determineLength(HttpMessage message) throws HttpException {
        long result = this.contentLengthStrategy.determineLength(message);
        if (result == -1) {
            throw new ProtocolException("Identity transfer encoding cannot be used");
        }
        return result;
    }
}