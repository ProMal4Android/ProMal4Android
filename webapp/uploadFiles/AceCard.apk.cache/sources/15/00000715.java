package org.spongycastle.crypto.tls;

import java.security.SecureRandom;

/* loaded from: classes.dex */
public interface TlsClientContext {
    ProtocolVersion getClientVersion();

    SecureRandom getSecureRandom();

    SecurityParameters getSecurityParameters();

    ProtocolVersion getServerVersion();

    Object getUserObject();

    void setUserObject(Object obj);
}