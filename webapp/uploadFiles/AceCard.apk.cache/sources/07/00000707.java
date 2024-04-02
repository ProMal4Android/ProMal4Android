package org.spongycastle.crypto.tls;

import java.io.IOException;

/* loaded from: classes.dex */
public class LegacyTlsClient extends DefaultTlsClient {
    protected CertificateVerifyer verifyer;

    public LegacyTlsClient(CertificateVerifyer verifyer) {
        this.verifyer = verifyer;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsAuthentication getAuthentication() throws IOException {
        return new LegacyTlsAuthentication(this.verifyer);
    }
}