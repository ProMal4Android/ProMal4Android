package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.crypto.prng.ThreadedSeedGenerator;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class TlsProtocolHandler {
    private static final short CS_CERTIFICATE_REQUEST_RECEIVED = 5;
    private static final short CS_CERTIFICATE_VERIFY_SEND = 8;
    private static final short CS_CLIENT_CHANGE_CIPHER_SPEC_SEND = 9;
    private static final short CS_CLIENT_FINISHED_SEND = 10;
    private static final short CS_CLIENT_HELLO_SEND = 1;
    private static final short CS_CLIENT_KEY_EXCHANGE_SEND = 7;
    private static final short CS_DONE = 12;
    private static final short CS_SERVER_CERTIFICATE_RECEIVED = 3;
    private static final short CS_SERVER_CHANGE_CIPHER_SPEC_RECEIVED = 11;
    private static final short CS_SERVER_HELLO_DONE_RECEIVED = 6;
    private static final short CS_SERVER_HELLO_RECEIVED = 2;
    private static final short CS_SERVER_KEY_EXCHANGE_RECEIVED = 4;
    private static final String TLS_ERROR_MESSAGE = "Internal TLS error, this could be an attack";
    private ByteQueue alertQueue;
    private boolean appDataReady;
    private ByteQueue applicationDataQueue;
    private TlsAuthentication authentication;
    private CertificateRequest certificateRequest;
    private ByteQueue changeCipherSpecQueue;
    private Hashtable clientExtensions;
    private boolean closed;
    private short connection_state;
    private boolean failedWithError;
    private ByteQueue handshakeQueue;
    private TlsKeyExchange keyExchange;
    private int[] offeredCipherSuites;
    private short[] offeredCompressionMethods;
    private SecureRandom random;
    private RecordStream rs;
    private SecurityParameters securityParameters;
    private TlsClient tlsClient;
    private TlsClientContextImpl tlsClientContext;
    private TlsInputStream tlsInputStream;
    private TlsOutputStream tlsOutputStream;
    private static final Integer EXT_RenegotiationInfo = new Integer(65281);
    private static final byte[] emptybuf = new byte[0];

    private static SecureRandom createSecureRandom() {
        ThreadedSeedGenerator tsg = new ThreadedSeedGenerator();
        SecureRandom random = new SecureRandom();
        random.setSeed(tsg.generateSeed(20, true));
        return random;
    }

    public TlsProtocolHandler(InputStream is, OutputStream os) {
        this(is, os, createSecureRandom());
    }

    public TlsProtocolHandler(InputStream is, OutputStream os, SecureRandom sr) {
        this.applicationDataQueue = new ByteQueue();
        this.changeCipherSpecQueue = new ByteQueue();
        this.alertQueue = new ByteQueue();
        this.handshakeQueue = new ByteQueue();
        this.tlsInputStream = null;
        this.tlsOutputStream = null;
        this.closed = false;
        this.failedWithError = false;
        this.appDataReady = false;
        this.securityParameters = null;
        this.tlsClientContext = null;
        this.tlsClient = null;
        this.offeredCipherSuites = null;
        this.offeredCompressionMethods = null;
        this.keyExchange = null;
        this.authentication = null;
        this.certificateRequest = null;
        this.connection_state = (short) 0;
        this.rs = new RecordStream(this, is, os);
        this.random = sr;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processData(short protocol, byte[] buf, int offset, int len) throws IOException {
        switch (protocol) {
            case 20:
                this.changeCipherSpecQueue.addData(buf, offset, len);
                processChangeCipherSpec();
                return;
            case 21:
                this.alertQueue.addData(buf, offset, len);
                processAlert();
                return;
            case 22:
                this.handshakeQueue.addData(buf, offset, len);
                processHandshake();
                return;
            case 23:
                if (!this.appDataReady) {
                    failWithError((short) 2, (short) 10);
                }
                this.applicationDataQueue.addData(buf, offset, len);
                processApplicationData();
                return;
            default:
                return;
        }
    }

    private void processHandshake() throws IOException {
        boolean read;
        do {
            read = false;
            if (this.handshakeQueue.size() >= 4) {
                byte[] beginning = new byte[4];
                this.handshakeQueue.read(beginning, 0, 4, 0);
                ByteArrayInputStream bis = new ByteArrayInputStream(beginning);
                short type = TlsUtils.readUint8(bis);
                int len = TlsUtils.readUint24(bis);
                if (this.handshakeQueue.size() >= len + 4) {
                    byte[] buf = new byte[len];
                    this.handshakeQueue.read(buf, 0, len, 4);
                    this.handshakeQueue.removeData(len + 4);
                    switch (type) {
                        case 0:
                        case 20:
                            break;
                        default:
                            this.rs.updateHandshakeData(beginning, 0, 4);
                            this.rs.updateHandshakeData(buf, 0, len);
                            break;
                    }
                    processHandshakeMessage(type, buf);
                    read = true;
                    continue;
                } else {
                    continue;
                }
            }
        } while (read);
    }

    private void processHandshakeMessage(short type, byte[] buf) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(buf);
        switch (type) {
            case 0:
                if (this.connection_state == 12) {
                    sendAlert((short) 1, (short) 100);
                    return;
                }
                return;
            case 2:
                switch (this.connection_state) {
                    case 1:
                        ProtocolVersion server_version = TlsUtils.readVersion(is);
                        ProtocolVersion client_version = this.tlsClientContext.getClientVersion();
                        if (server_version.getFullVersion() > client_version.getFullVersion()) {
                            failWithError((short) 2, (short) 47);
                        }
                        this.tlsClientContext.setServerVersion(server_version);
                        this.tlsClient.notifyServerVersion(server_version);
                        this.securityParameters.serverRandom = new byte[32];
                        TlsUtils.readFully(this.securityParameters.serverRandom, is);
                        byte[] sessionID = TlsUtils.readOpaque8(is);
                        if (sessionID.length > 32) {
                            failWithError((short) 2, (short) 47);
                        }
                        this.tlsClient.notifySessionID(sessionID);
                        int selectedCipherSuite = TlsUtils.readUint16(is);
                        if (!arrayContains(this.offeredCipherSuites, selectedCipherSuite) || selectedCipherSuite == 255) {
                            failWithError((short) 2, (short) 47);
                        }
                        this.tlsClient.notifySelectedCipherSuite(selectedCipherSuite);
                        short selectedCompressionMethod = TlsUtils.readUint8(is);
                        if (!arrayContains(this.offeredCompressionMethods, selectedCompressionMethod)) {
                            failWithError((short) 2, (short) 47);
                        }
                        this.tlsClient.notifySelectedCompressionMethod(selectedCompressionMethod);
                        Hashtable serverExtensions = new Hashtable();
                        if (is.available() > 0) {
                            byte[] extBytes = TlsUtils.readOpaque16(is);
                            ByteArrayInputStream ext = new ByteArrayInputStream(extBytes);
                            while (ext.available() > 0) {
                                Integer extType = new Integer(TlsUtils.readUint16(ext));
                                byte[] extValue = TlsUtils.readOpaque16(ext);
                                if (!extType.equals(EXT_RenegotiationInfo) && this.clientExtensions.get(extType) == null) {
                                    failWithError((short) 2, AlertDescription.unsupported_extension);
                                }
                                if (serverExtensions.containsKey(extType)) {
                                    failWithError((short) 2, (short) 47);
                                }
                                serverExtensions.put(extType, extValue);
                            }
                        }
                        assertEmpty(is);
                        boolean secure_negotiation = serverExtensions.containsKey(EXT_RenegotiationInfo);
                        if (secure_negotiation) {
                            byte[] renegExtValue = (byte[]) serverExtensions.get(EXT_RenegotiationInfo);
                            if (!Arrays.constantTimeAreEqual(renegExtValue, createRenegotiationInfo(emptybuf))) {
                                failWithError((short) 2, (short) 40);
                            }
                        }
                        this.tlsClient.notifySecureRenegotiation(secure_negotiation);
                        if (this.clientExtensions != null) {
                            this.tlsClient.processServerExtensions(serverExtensions);
                        }
                        this.keyExchange = this.tlsClient.getKeyExchange();
                        this.connection_state = (short) 2;
                        return;
                    default:
                        failWithError((short) 2, (short) 10);
                        return;
                }
            case 11:
                switch (this.connection_state) {
                    case 2:
                        Certificate serverCertificate = Certificate.parse(is);
                        assertEmpty(is);
                        this.keyExchange.processServerCertificate(serverCertificate);
                        this.authentication = this.tlsClient.getAuthentication();
                        this.authentication.notifyServerCertificate(serverCertificate);
                        break;
                    default:
                        failWithError((short) 2, (short) 10);
                        break;
                }
                this.connection_state = (short) 3;
                return;
            case 12:
                switch (this.connection_state) {
                    case 2:
                        this.keyExchange.skipServerCertificate();
                        this.authentication = null;
                    case 3:
                        this.keyExchange.processServerKeyExchange(is);
                        assertEmpty(is);
                        break;
                    default:
                        failWithError((short) 2, (short) 10);
                        break;
                }
                this.connection_state = (short) 4;
                return;
            case 13:
                switch (this.connection_state) {
                    case 3:
                        this.keyExchange.skipServerKeyExchange();
                    case 4:
                        if (this.authentication == null) {
                            failWithError((short) 2, (short) 40);
                        }
                        int numTypes = TlsUtils.readUint8(is);
                        short[] certificateTypes = new short[numTypes];
                        for (int i = 0; i < numTypes; i++) {
                            certificateTypes[i] = TlsUtils.readUint8(is);
                        }
                        byte[] authorities = TlsUtils.readOpaque16(is);
                        assertEmpty(is);
                        Vector authorityDNs = new Vector();
                        ByteArrayInputStream bis = new ByteArrayInputStream(authorities);
                        while (bis.available() > 0) {
                            byte[] dnBytes = TlsUtils.readOpaque16(bis);
                            authorityDNs.addElement(X500Name.getInstance(ASN1Primitive.fromByteArray(dnBytes)));
                        }
                        this.certificateRequest = new CertificateRequest(certificateTypes, authorityDNs);
                        this.keyExchange.validateCertificateRequest(this.certificateRequest);
                        break;
                    default:
                        failWithError((short) 2, (short) 10);
                        break;
                }
                this.connection_state = (short) 5;
                return;
            case 14:
                switch (this.connection_state) {
                    case 2:
                        this.keyExchange.skipServerCertificate();
                        this.authentication = null;
                    case 3:
                        this.keyExchange.skipServerKeyExchange();
                        break;
                    case 4:
                    case 5:
                        break;
                    default:
                        failWithError((short) 2, (short) 40);
                        return;
                }
                assertEmpty(is);
                this.connection_state = (short) 6;
                TlsCredentials clientCreds = null;
                if (this.certificateRequest == null) {
                    this.keyExchange.skipClientCredentials();
                } else {
                    clientCreds = this.authentication.getClientCredentials(this.certificateRequest);
                    if (clientCreds == null) {
                        this.keyExchange.skipClientCredentials();
                        boolean isTls = this.tlsClientContext.getServerVersion().getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
                        if (isTls) {
                            sendClientCertificate(Certificate.EMPTY_CHAIN);
                        } else {
                            sendAlert((short) 1, (short) 41);
                        }
                    } else {
                        this.keyExchange.processClientCredentials(clientCreds);
                        sendClientCertificate(clientCreds.getCertificate());
                    }
                }
                sendClientKeyExchange();
                this.connection_state = (short) 7;
                byte[] pms = this.keyExchange.generatePremasterSecret();
                this.securityParameters.masterSecret = TlsUtils.calculateMasterSecret(this.tlsClientContext, pms);
                Arrays.fill(pms, (byte) 0);
                if (clientCreds != null && (clientCreds instanceof TlsSignerCredentials)) {
                    TlsSignerCredentials signerCreds = (TlsSignerCredentials) clientCreds;
                    byte[] md5andsha1 = this.rs.getCurrentHash(null);
                    byte[] clientCertificateSignature = signerCreds.generateCertificateSignature(md5andsha1);
                    sendCertificateVerify(clientCertificateSignature);
                    this.connection_state = (short) 8;
                }
                byte[] cmessage = {1};
                this.rs.writeMessage((short) 20, cmessage, 0, cmessage.length);
                this.connection_state = (short) 9;
                this.rs.clientCipherSpecDecided(this.tlsClient.getCompression(), this.tlsClient.getCipher());
                byte[] clientVerifyData = TlsUtils.calculateVerifyData(this.tlsClientContext, "client finished", this.rs.getCurrentHash(TlsUtils.SSL_CLIENT));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                TlsUtils.writeUint8((short) 20, bos);
                TlsUtils.writeOpaque24(clientVerifyData, bos);
                byte[] message = bos.toByteArray();
                this.rs.writeMessage((short) 22, message, 0, message.length);
                this.connection_state = (short) 10;
                return;
            case 20:
                switch (this.connection_state) {
                    case 11:
                        boolean isTls2 = this.tlsClientContext.getServerVersion().getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
                        int checksumLength = isTls2 ? 12 : 36;
                        byte[] serverVerifyData = new byte[checksumLength];
                        TlsUtils.readFully(serverVerifyData, is);
                        assertEmpty(is);
                        byte[] expectedServerVerifyData = TlsUtils.calculateVerifyData(this.tlsClientContext, "server finished", this.rs.getCurrentHash(TlsUtils.SSL_SERVER));
                        if (!Arrays.constantTimeAreEqual(expectedServerVerifyData, serverVerifyData)) {
                            failWithError((short) 2, (short) 40);
                        }
                        this.connection_state = (short) 12;
                        this.appDataReady = true;
                        return;
                    default:
                        failWithError((short) 2, (short) 10);
                        return;
                }
            default:
                failWithError((short) 2, (short) 10);
                return;
        }
    }

    private void processApplicationData() {
    }

    private void processAlert() throws IOException {
        while (this.alertQueue.size() >= 2) {
            byte[] tmp = new byte[2];
            this.alertQueue.read(tmp, 0, 2, 0);
            this.alertQueue.removeData(2);
            short level = tmp[0];
            short description = tmp[1];
            if (level == 2) {
                this.failedWithError = true;
                this.closed = true;
                try {
                    this.rs.close();
                } catch (Exception e) {
                }
                throw new IOException(TLS_ERROR_MESSAGE);
            } else if (description == 0) {
                failWithError((short) 1, (short) 0);
            }
        }
    }

    private void processChangeCipherSpec() throws IOException {
        while (this.changeCipherSpecQueue.size() > 0) {
            byte[] b = new byte[1];
            this.changeCipherSpecQueue.read(b, 0, 1, 0);
            this.changeCipherSpecQueue.removeData(1);
            if (b[0] != 1) {
                failWithError((short) 2, (short) 10);
            }
            if (this.connection_state != 10) {
                failWithError((short) 2, (short) 40);
            }
            this.rs.serverClientSpecReceived();
            this.connection_state = (short) 11;
        }
    }

    private void sendClientCertificate(Certificate clientCert) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TlsUtils.writeUint8((short) 11, bos);
        TlsUtils.writeUint24(0, bos);
        clientCert.encode(bos);
        byte[] message = bos.toByteArray();
        TlsUtils.writeUint24(message.length - 4, message, 1);
        this.rs.writeMessage((short) 22, message, 0, message.length);
    }

    private void sendClientKeyExchange() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TlsUtils.writeUint8((short) 16, bos);
        TlsUtils.writeUint24(0, bos);
        this.keyExchange.generateClientKeyExchange(bos);
        byte[] message = bos.toByteArray();
        TlsUtils.writeUint24(message.length - 4, message, 1);
        this.rs.writeMessage((short) 22, message, 0, message.length);
    }

    private void sendCertificateVerify(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TlsUtils.writeUint8((short) 15, bos);
        TlsUtils.writeUint24(data.length + 2, bos);
        TlsUtils.writeOpaque16(data, bos);
        byte[] message = bos.toByteArray();
        this.rs.writeMessage((short) 22, message, 0, message.length);
    }

    public void connect(CertificateVerifyer verifyer) throws IOException {
        connect(new LegacyTlsClient(verifyer));
    }

    public void connect(TlsClient tlsClient) throws IOException {
        if (tlsClient == null) {
            throw new IllegalArgumentException("'tlsClient' cannot be null");
        }
        if (this.tlsClient != null) {
            throw new IllegalStateException("connect can only be called once");
        }
        this.securityParameters = new SecurityParameters();
        this.securityParameters.clientRandom = new byte[32];
        this.random.nextBytes(this.securityParameters.clientRandom);
        TlsUtils.writeGMTUnixTime(this.securityParameters.clientRandom, 0);
        this.tlsClientContext = new TlsClientContextImpl(this.random, this.securityParameters);
        this.rs.init(this.tlsClientContext);
        this.tlsClient = tlsClient;
        this.tlsClient.init(this.tlsClientContext);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ProtocolVersion client_version = this.tlsClient.getClientVersion();
        this.tlsClientContext.setClientVersion(client_version);
        this.tlsClientContext.setServerVersion(client_version);
        TlsUtils.writeVersion(client_version, os);
        os.write(this.securityParameters.clientRandom);
        TlsUtils.writeUint8((short) 0, os);
        this.offeredCipherSuites = this.tlsClient.getCipherSuites();
        this.clientExtensions = this.tlsClient.getClientExtensions();
        boolean noRenegExt = this.clientExtensions == null || this.clientExtensions.get(EXT_RenegotiationInfo) == null;
        int count = this.offeredCipherSuites.length;
        if (noRenegExt) {
            count++;
        }
        TlsUtils.writeUint16(count * 2, os);
        TlsUtils.writeUint16Array(this.offeredCipherSuites, os);
        if (noRenegExt) {
            TlsUtils.writeUint16(255, os);
        }
        this.offeredCompressionMethods = this.tlsClient.getCompressionMethods();
        TlsUtils.writeUint8((short) this.offeredCompressionMethods.length, os);
        TlsUtils.writeUint8Array(this.offeredCompressionMethods, os);
        if (this.clientExtensions != null) {
            ByteArrayOutputStream ext = new ByteArrayOutputStream();
            Enumeration keys = this.clientExtensions.keys();
            while (keys.hasMoreElements()) {
                Integer extType = (Integer) keys.nextElement();
                writeExtension(ext, extType, (byte[]) this.clientExtensions.get(extType));
            }
            TlsUtils.writeOpaque16(ext.toByteArray(), os);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TlsUtils.writeUint8((short) 1, bos);
        TlsUtils.writeUint24(os.size(), bos);
        bos.write(os.toByteArray());
        byte[] message = bos.toByteArray();
        safeWriteMessage((short) 22, message, 0, message.length);
        this.connection_state = (short) 1;
        while (this.connection_state != 12) {
            safeReadData();
        }
        this.tlsInputStream = new TlsInputStream(this);
        this.tlsOutputStream = new TlsOutputStream(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int readApplicationData(byte[] buf, int offset, int len) throws IOException {
        while (this.applicationDataQueue.size() == 0) {
            if (this.closed) {
                if (this.failedWithError) {
                    throw new IOException(TLS_ERROR_MESSAGE);
                }
                return -1;
            }
            safeReadData();
        }
        int len2 = Math.min(len, this.applicationDataQueue.size());
        this.applicationDataQueue.read(buf, offset, len2, 0);
        this.applicationDataQueue.removeData(len2);
        return len2;
    }

    private void safeReadData() throws IOException {
        try {
            this.rs.readData();
        } catch (TlsFatalAlert e) {
            if (!this.closed) {
                failWithError((short) 2, e.getAlertDescription());
            }
            throw e;
        } catch (IOException e2) {
            if (!this.closed) {
                failWithError((short) 2, (short) 80);
            }
            throw e2;
        } catch (RuntimeException e3) {
            if (!this.closed) {
                failWithError((short) 2, (short) 80);
            }
            throw e3;
        }
    }

    private void safeWriteMessage(short type, byte[] buf, int offset, int len) throws IOException {
        try {
            this.rs.writeMessage(type, buf, offset, len);
        } catch (TlsFatalAlert e) {
            if (!this.closed) {
                failWithError((short) 2, e.getAlertDescription());
            }
            throw e;
        } catch (IOException e2) {
            if (!this.closed) {
                failWithError((short) 2, (short) 80);
            }
            throw e2;
        } catch (RuntimeException e3) {
            if (!this.closed) {
                failWithError((short) 2, (short) 80);
            }
            throw e3;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeData(byte[] buf, int offset, int len) throws IOException {
        if (this.closed) {
            if (this.failedWithError) {
                throw new IOException(TLS_ERROR_MESSAGE);
            }
            throw new IOException("Sorry, connection has been closed, you cannot write more data");
        }
        safeWriteMessage((short) 23, emptybuf, 0, 0);
        do {
            int toWrite = Math.min(len, 16384);
            safeWriteMessage((short) 23, buf, offset, toWrite);
            offset += toWrite;
            len -= toWrite;
        } while (len > 0);
    }

    public OutputStream getOutputStream() {
        return this.tlsOutputStream;
    }

    public InputStream getInputStream() {
        return this.tlsInputStream;
    }

    private void failWithError(short alertLevel, short alertDescription) throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (alertLevel == 2) {
                this.failedWithError = true;
            }
            sendAlert(alertLevel, alertDescription);
            this.rs.close();
            if (alertLevel == 2) {
                throw new IOException(TLS_ERROR_MESSAGE);
            }
            return;
        }
        throw new IOException(TLS_ERROR_MESSAGE);
    }

    private void sendAlert(short alertLevel, short alertDescription) throws IOException {
        byte[] error = {(byte) alertLevel, (byte) alertDescription};
        this.rs.writeMessage((short) 21, error, 0, 2);
    }

    public void close() throws IOException {
        if (!this.closed) {
            failWithError((short) 1, (short) 0);
        }
    }

    protected void assertEmpty(ByteArrayInputStream is) throws IOException {
        if (is.available() > 0) {
            throw new TlsFatalAlert((short) 50);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void flush() throws IOException {
        this.rs.flush();
    }

    private static boolean arrayContains(short[] a, short n) {
        for (short s : a) {
            if (s == n) {
                return true;
            }
        }
        return false;
    }

    private static boolean arrayContains(int[] a, int n) {
        for (int i : a) {
            if (i == n) {
                return true;
            }
        }
        return false;
    }

    private static byte[] createRenegotiationInfo(byte[] renegotiated_connection) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        TlsUtils.writeOpaque8(renegotiated_connection, buf);
        return buf.toByteArray();
    }

    private static void writeExtension(OutputStream output, Integer extType, byte[] extValue) throws IOException {
        TlsUtils.writeUint16(extType.intValue(), output);
        TlsUtils.writeOpaque16(extValue, output);
    }
}