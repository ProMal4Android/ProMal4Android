package ch.boye.httpclientandroidlib.client.entity;

import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class DeflateDecompressingEntity extends DecompressingEntity {
    @Override // ch.boye.httpclientandroidlib.client.entity.DecompressingEntity, ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public /* bridge */ /* synthetic */ InputStream getContent() throws IOException {
        return super.getContent();
    }

    @Override // ch.boye.httpclientandroidlib.client.entity.DecompressingEntity, ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public /* bridge */ /* synthetic */ void writeTo(OutputStream x0) throws IOException {
        super.writeTo(x0);
    }

    public DeflateDecompressingEntity(HttpEntity entity) {
        super(entity);
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x004a, code lost:
        if (r4 != (-1)) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0053, code lost:
        throw new java.io.IOException("Unable to read the response");
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x005e, code lost:
        r6.unread(r5, 0, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:?, code lost:
        return new java.util.zip.InflaterInputStream(r6);
     */
    @Override // ch.boye.httpclientandroidlib.client.entity.DecompressingEntity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    java.io.InputStream decorate(java.io.InputStream r12) throws java.io.IOException {
        /*
            r11 = this;
            r10 = 1
            r9 = 0
            r8 = -1
            r7 = 6
            byte[] r5 = new byte[r7]
            java.io.PushbackInputStream r6 = new java.io.PushbackInputStream
            int r7 = r5.length
            r6.<init>(r12, r7)
            int r2 = r6.read(r5)
            if (r2 != r8) goto L1a
            java.io.IOException r7 = new java.io.IOException
            java.lang.String r8 = "Unable to read the response"
            r7.<init>(r8)
            throw r7
        L1a:
            byte[] r0 = new byte[r10]
            java.util.zip.Inflater r3 = new java.util.zip.Inflater
            r3.<init>()
        L21:
            int r4 = r3.inflate(r0)     // Catch: java.util.zip.DataFormatException -> L35
            if (r4 != 0) goto L4a
            boolean r7 = r3.finished()     // Catch: java.util.zip.DataFormatException -> L35
            if (r7 == 0) goto L44
            java.io.IOException r7 = new java.io.IOException     // Catch: java.util.zip.DataFormatException -> L35
            java.lang.String r8 = "Unable to read the response"
            r7.<init>(r8)     // Catch: java.util.zip.DataFormatException -> L35
            throw r7     // Catch: java.util.zip.DataFormatException -> L35
        L35:
            r1 = move-exception
            r6.unread(r5, r9, r2)
            java.util.zip.InflaterInputStream r7 = new java.util.zip.InflaterInputStream
            java.util.zip.Inflater r8 = new java.util.zip.Inflater
            r8.<init>(r10)
            r7.<init>(r6, r8)
        L43:
            return r7
        L44:
            boolean r7 = r3.needsDictionary()     // Catch: java.util.zip.DataFormatException -> L35
            if (r7 == 0) goto L54
        L4a:
            if (r4 != r8) goto L5e
            java.io.IOException r7 = new java.io.IOException     // Catch: java.util.zip.DataFormatException -> L35
            java.lang.String r8 = "Unable to read the response"
            r7.<init>(r8)     // Catch: java.util.zip.DataFormatException -> L35
            throw r7     // Catch: java.util.zip.DataFormatException -> L35
        L54:
            boolean r7 = r3.needsInput()     // Catch: java.util.zip.DataFormatException -> L35
            if (r7 == 0) goto L21
            r3.setInput(r5)     // Catch: java.util.zip.DataFormatException -> L35
            goto L21
        L5e:
            r7 = 0
            r6.unread(r5, r7, r2)     // Catch: java.util.zip.DataFormatException -> L35
            java.util.zip.InflaterInputStream r7 = new java.util.zip.InflaterInputStream     // Catch: java.util.zip.DataFormatException -> L35
            r7.<init>(r6)     // Catch: java.util.zip.DataFormatException -> L35
            goto L43
        */
        throw new UnsupportedOperationException("Method not decompiled: ch.boye.httpclientandroidlib.client.entity.DeflateDecompressingEntity.decorate(java.io.InputStream):java.io.InputStream");
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