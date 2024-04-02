package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class PSKTlsClient implements TlsClient {
    protected TlsCipherFactory cipherFactory;
    protected TlsClientContext context;
    protected TlsPSKIdentity pskIdentity;
    protected int selectedCipherSuite;
    protected int selectedCompressionMethod;

    public PSKTlsClient(TlsPSKIdentity pskIdentity) {
        this(new DefaultTlsCipherFactory(), pskIdentity);
    }

    public PSKTlsClient(TlsCipherFactory cipherFactory, TlsPSKIdentity pskIdentity) {
        this.cipherFactory = cipherFactory;
        this.pskIdentity = pskIdentity;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public ProtocolVersion getClientVersion() {
        return ProtocolVersion.TLSv10;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void init(TlsClientContext context) {
        this.context = context;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public int[] getCipherSuites() {
        return new int[]{CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA, CipherSuite.TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA, CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA, CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA};
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public Hashtable getClientExtensions() throws IOException {
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public short[] getCompressionMethods() {
        return new short[]{0};
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifyServerVersion(ProtocolVersion serverVersion) throws IOException {
        if (!ProtocolVersion.TLSv10.equals(serverVersion)) {
            throw new TlsFatalAlert((short) 47);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifySessionID(byte[] sessionID) {
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifySelectedCipherSuite(int selectedCipherSuite) {
        this.selectedCipherSuite = selectedCipherSuite;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifySelectedCompressionMethod(short selectedCompressionMethod) {
        this.selectedCompressionMethod = selectedCompressionMethod;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void notifySecureRenegotiation(boolean secureRenegotiation) throws IOException {
        if (!secureRenegotiation) {
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void processServerExtensions(Hashtable serverExtensions) {
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsKeyExchange getKeyExchange() throws IOException {
        switch (this.selectedCipherSuite) {
            case CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA /* 139 */:
            case CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA /* 140 */:
            case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA /* 141 */:
                return createPSKKeyExchange(13);
            case CipherSuite.TLS_DHE_PSK_WITH_RC4_128_SHA /* 142 */:
            case CipherSuite.TLS_RSA_PSK_WITH_RC4_128_SHA /* 146 */:
            default:
                throw new TlsFatalAlert((short) 80);
            case CipherSuite.TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA /* 143 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA /* 144 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA /* 145 */:
                return createPSKKeyExchange(14);
            case CipherSuite.TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA /* 147 */:
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA /* 148 */:
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA /* 149 */:
                return createPSKKeyExchange(15);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsAuthentication getAuthentication() throws IOException {
        return null;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsCompression getCompression() throws IOException {
        switch (this.selectedCompressionMethod) {
            case 0:
                return new TlsNullCompression();
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public TlsCipher getCipher() throws IOException {
        switch (this.selectedCipherSuite) {
            case CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA /* 139 */:
            case CipherSuite.TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA /* 143 */:
            case CipherSuite.TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA /* 147 */:
                return this.cipherFactory.createCipher(this.context, 7, 2);
            case CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA /* 140 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA /* 144 */:
            case CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA /* 148 */:
                return this.cipherFactory.createCipher(this.context, 8, 2);
            case CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA /* 141 */:
            case CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA /* 145 */:
            case CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA /* 149 */:
                return this.cipherFactory.createCipher(this.context, 9, 2);
            case CipherSuite.TLS_DHE_PSK_WITH_RC4_128_SHA /* 142 */:
            case CipherSuite.TLS_RSA_PSK_WITH_RC4_128_SHA /* 146 */:
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    protected TlsKeyExchange createPSKKeyExchange(int keyExchange) {
        return new TlsPSKKeyExchange(this.context, keyExchange, this.pskIdentity);
    }
}