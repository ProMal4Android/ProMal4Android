package org.spongycastle.crypto.tls;

import java.io.IOException;

/* loaded from: classes.dex */
public class LegacyTlsAuthentication implements TlsAuthentication {
    protected CertificateVerifyer verifyer;

    public LegacyTlsAuthentication(CertificateVerifyer verifyer) {
        this.verifyer = verifyer;
    }

    @Override // org.spongycastle.crypto.tls.TlsAuthentication
    public void notifyServerCertificate(Certificate serverCertificate) throws IOException {
        if (!this.verifyer.isValid(serverCertificate.getCerts())) {
            throw new TlsFatalAlert((short) 90);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsAuthentication
    public TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException {
        return null;
    }
}