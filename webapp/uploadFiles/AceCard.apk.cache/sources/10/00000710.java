package org.spongycastle.crypto.tls;

import java.io.IOException;

/* loaded from: classes.dex */
public interface TlsAuthentication {
    TlsCredentials getClientCredentials(CertificateRequest certificateRequest) throws IOException;

    void notifyServerCertificate(Certificate certificate) throws IOException;
}