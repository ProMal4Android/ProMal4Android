package org.spongycastle.crypto.tls;

import java.io.OutputStream;

/* loaded from: classes.dex */
public class TlsNullCompression implements TlsCompression {
    @Override // org.spongycastle.crypto.tls.TlsCompression
    public OutputStream compress(OutputStream output) {
        return output;
    }

    @Override // org.spongycastle.crypto.tls.TlsCompression
    public OutputStream decompress(OutputStream output) {
        return output;
    }
}