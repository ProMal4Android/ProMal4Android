package org.spongycastle.crypto.tls;

import java.util.Vector;

/* loaded from: classes.dex */
public class CertificateRequest {
    private Vector certificateAuthorities;
    private short[] certificateTypes;

    public CertificateRequest(short[] certificateTypes, Vector certificateAuthorities) {
        this.certificateTypes = certificateTypes;
        this.certificateAuthorities = certificateAuthorities;
    }

    public short[] getCertificateTypes() {
        return this.certificateTypes;
    }

    public Vector getCertificateAuthorities() {
        return this.certificateAuthorities;
    }
}