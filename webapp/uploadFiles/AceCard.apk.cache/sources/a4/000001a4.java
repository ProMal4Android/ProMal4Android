package ch.boye.httpclientandroidlib.client.entity;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

/* loaded from: classes.dex */
public class GzipDecompressingEntity extends DecompressingEntity {
    @Override // ch.boye.httpclientandroidlib.client.entity.DecompressingEntity, ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public /* bridge */ /* synthetic */ InputStream getContent() throws IOException {
        return super.getContent();
    }

    @Override // ch.boye.httpclientandroidlib.client.entity.DecompressingEntity, ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public /* bridge */ /* synthetic */ void writeTo(OutputStream x0) throws IOException {
        super.writeTo(x0);
    }

    public GzipDecompressingEntity(HttpEntity entity) {
        super(entity);
    }

    @Override // ch.boye.httpclientandroidlib.client.entity.DecompressingEntity
    InputStream decorate(InputStream wrapped) throws IOException {
        return new GZIPInputStream(wrapped);
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public Header getContentEncoding() {
        return null;
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public long getContentLength() {
        return -1L;
    }
}