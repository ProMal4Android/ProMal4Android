package org.spongycastle.crypto.tls;

import java.security.SecureRandom;

/* loaded from: classes.dex */
class TlsClientContextImpl implements TlsClientContext {
    private SecureRandom secureRandom;
    private SecurityParameters securityParameters;
    private ProtocolVersion clientVersion = null;
    private ProtocolVersion serverVersion = null;
    private Object userObject = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TlsClientContextImpl(SecureRandom secureRandom, SecurityParameters securityParameters) {
        this.secureRandom = secureRandom;
        this.securityParameters = securityParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsClientContext
    public SecureRandom getSecureRandom() {
        return this.secureRandom;
    }

    @Override // org.spongycastle.crypto.tls.TlsClientContext
    public SecurityParameters getSecurityParameters() {
        return this.securityParameters;
    }

    @Override // org.spongycastle.crypto.tls.TlsClientContext
    public ProtocolVersion getClientVersion() {
        return this.clientVersion;
    }

    public void setClientVersion(ProtocolVersion clientVersion) {
        this.clientVersion = clientVersion;
    }

    @Override // org.spongycastle.crypto.tls.TlsClientContext
    public ProtocolVersion getServerVersion() {
        return this.serverVersion;
    }

    public void setServerVersion(ProtocolVersion serverVersion) {
        this.serverVersion = serverVersion;
    }

    @Override // org.spongycastle.crypto.tls.TlsClientContext
    public Object getUserObject() {
        return this.userObject;
    }

    @Override // org.spongycastle.crypto.tls.TlsClientContext
    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }
}