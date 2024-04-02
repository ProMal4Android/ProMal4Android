package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Immutable
/* loaded from: classes.dex */
public class HeapResource implements Resource {
    private static final long serialVersionUID = -2078599905620463394L;
    private final byte[] b;

    public HeapResource(byte[] b) {
        this.b = b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getByteArray() {
        return this.b;
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.Resource
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.b);
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.Resource
    public long length() {
        return this.b.length;
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.Resource
    public void dispose() {
    }
}