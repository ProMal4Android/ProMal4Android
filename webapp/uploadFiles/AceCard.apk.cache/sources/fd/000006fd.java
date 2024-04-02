package org.spongycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;

/* loaded from: classes.dex */
public abstract class DefaultTlsClient implements TlsClient {
    protected TlsCipherFactory cipherFactory;
    protected TlsClientContext context;
    protected int selectedCipherSuite;
    protected int selectedCompressionMethod;

    public DefaultTlsClient() {
        this(new DefaultTlsCipherFactory());
    }

    public DefaultTlsClient(TlsCipherFactory cipherFactory) {
        this.cipherFactory = cipherFactory;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public void init(TlsClientContext context) {
        this.context = context;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public ProtocolVersion getClientVersion() {
        return ProtocolVersion.TLSv10;
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public int[] getCipherSuites() {
        return new int[]{57, 56, 51, 50, 22, 19, 53, 47, 10};
    }

    @Override // org.spongycastle.crypto.tls.TlsClient
    public Hashtable getClientExtensions() {
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
            case 10:
            case CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA /* 47 */:
            case 53:
                return createRSAKeyExchange();
            case 13:
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA /* 48 */:
            case CipherSuite.TLS_DH_DSS_WITH_AES_256_CBC_SHA /* 54 */:
                return createDHKeyExchange(7);
            case 16:
            case CipherSuite.TLS_DH_RSA_WITH_AES_128_CBC_SHA /* 49 */:
            case 55:
                return createDHKeyExchange(9);
            case 19:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA /* 50 */:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA /* 56 */:
                return createDHEKeyExchange(3);
            case 22:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA /* 51 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA /* 57 */:
                return createDHEKeyExchange(5);
            case CipherSuite.TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA /* 49155 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA /* 49156 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA /* 49157 */:
                return createECDHKeyExchange(16);
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA /* 49160 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA /* 49161 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA /* 49162 */:
                return createECDHEKeyExchange(17);
            case CipherSuite.TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA /* 49165 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_128_CBC_SHA /* 49166 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_256_CBC_SHA /* 49167 */:
                return createECDHKeyExchange(18);
            case CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA /* 49170 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA /* 49171 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA /* 49172 */:
                return createECDHEKeyExchange(19);
            default:
                throw new TlsFatalAlert((short) 80);
        }
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
            case 10:
            case 13:
            case 16:
            case 19:
            case 22:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA /* 49155 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA /* 49160 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA /* 49165 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA /* 49170 */:
                return this.cipherFactory.createCipher(this.context, 7, 2);
            case CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA /* 47 */:
            case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA /* 48 */:
            case CipherSuite.TLS_DH_RSA_WITH_AES_128_CBC_SHA /* 49 */:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA /* 50 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA /* 51 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA /* 49156 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA /* 49161 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_128_CBC_SHA /* 49166 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA /* 49171 */:
                return this.cipherFactory.createCipher(this.context, 8, 2);
            case 53:
            case CipherSuite.TLS_DH_DSS_WITH_AES_256_CBC_SHA /* 54 */:
            case 55:
            case CipherSuite.TLS_DHE_DSS_WITH_AES_256_CBC_SHA /* 56 */:
            case CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA /* 57 */:
            case CipherSuite.TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA /* 49157 */:
            case CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA /* 49162 */:
            case CipherSuite.TLS_ECDH_RSA_WITH_AES_256_CBC_SHA /* 49167 */:
            case CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA /* 49172 */:
                return this.cipherFactory.createCipher(this.context, 9, 2);
            default:
                throw new TlsFatalAlert((short) 80);
        }
    }

    protected TlsKeyExchange createDHKeyExchange(int keyExchange) {
        return new TlsDHKeyExchange(this.context, keyExchange);
    }

    protected TlsKeyExchange createDHEKeyExchange(int keyExchange) {
        return new TlsDHEKeyExchange(this.context, keyExchange);
    }

    protected TlsKeyExchange createECDHKeyExchange(int keyExchange) {
        return new TlsECDHKeyExchange(this.context, keyExchange);
    }

    protected TlsKeyExchange createECDHEKeyExchange(int keyExchange) {
        return new TlsECDHEKeyExchange(this.context, keyExchange);
    }

    protected TlsKeyExchange createRSAKeyExchange() {
        return new TlsRSAKeyExchange(this.context);
    }
}