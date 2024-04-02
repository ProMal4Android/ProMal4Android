package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.crypto.Digest;

/* loaded from: classes.dex */
class RecordStream {
    private TlsProtocolHandler handler;
    private InputStream is;
    private OutputStream os;
    private TlsCipher readCipher;
    private TlsCompression readCompression;
    private TlsCipher writeCipher;
    private TlsCompression writeCompression;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private TlsClientContext context = null;
    private CombinedHash hash = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RecordStream(TlsProtocolHandler handler, InputStream is, OutputStream os) {
        this.readCompression = null;
        this.writeCompression = null;
        this.readCipher = null;
        this.writeCipher = null;
        this.handler = handler;
        this.is = is;
        this.os = os;
        this.readCompression = new TlsNullCompression();
        this.writeCompression = this.readCompression;
        this.readCipher = new TlsNullCipher();
        this.writeCipher = this.readCipher;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(TlsClientContext context) {
        this.context = context;
        this.hash = new CombinedHash(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clientCipherSpecDecided(TlsCompression tlsCompression, TlsCipher tlsCipher) {
        this.writeCompression = tlsCompression;
        this.writeCipher = tlsCipher;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void serverClientSpecReceived() {
        this.readCompression = this.writeCompression;
        this.readCipher = this.writeCipher;
    }

    public void readData() throws IOException {
        short type = TlsUtils.readUint8(this.is);
        ProtocolVersion expectedVersion = ProtocolVersion.TLSv10;
        if (!expectedVersion.equals(TlsUtils.readVersion(this.is))) {
            throw new TlsFatalAlert((short) 47);
        }
        int size = TlsUtils.readUint16(this.is);
        byte[] buf = decodeAndVerify(type, this.is, size);
        this.handler.processData(type, buf, 0, buf.length);
    }

    protected byte[] decodeAndVerify(short type, InputStream is, int len) throws IOException {
        byte[] buf = new byte[len];
        TlsUtils.readFully(buf, is);
        byte[] decoded = this.readCipher.decodeCiphertext(type, buf, 0, buf.length);
        OutputStream cOut = this.readCompression.decompress(this.buffer);
        if (cOut != this.buffer) {
            cOut.write(decoded, 0, decoded.length);
            cOut.flush();
            return getBufferContents();
        }
        return decoded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeMessage(short type, byte[] message, int offset, int len) throws IOException {
        byte[] ciphertext;
        if (type == 22) {
            updateHandshakeData(message, offset, len);
        }
        OutputStream cOut = this.writeCompression.compress(this.buffer);
        if (cOut == this.buffer) {
            ciphertext = this.writeCipher.encodePlaintext(type, message, offset, len);
        } else {
            cOut.write(message, offset, len);
            cOut.flush();
            byte[] compressed = getBufferContents();
            ciphertext = this.writeCipher.encodePlaintext(type, compressed, 0, compressed.length);
        }
        byte[] writeMessage = new byte[ciphertext.length + 5];
        TlsUtils.writeUint8(type, writeMessage, 0);
        TlsUtils.writeVersion(ProtocolVersion.TLSv10, writeMessage, 1);
        TlsUtils.writeUint16(ciphertext.length, writeMessage, 3);
        System.arraycopy(ciphertext, 0, writeMessage, 5, ciphertext.length);
        this.os.write(writeMessage);
        this.os.flush();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateHandshakeData(byte[] message, int offset, int len) {
        this.hash.update(message, offset, len);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getCurrentHash(byte[] sender) {
        Digest d = new CombinedHash(this.hash);
        boolean isTls = this.context.getServerVersion().getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        if (!isTls && sender != null) {
            d.update(sender, 0, sender.length);
        }
        return doFinal(d);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void close() throws IOException {
        IOException e = null;
        try {
            this.is.close();
        } catch (IOException ex) {
            e = ex;
        }
        try {
            this.os.close();
        } catch (IOException ex2) {
            e = ex2;
        }
        if (e != null) {
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void flush() throws IOException {
        this.os.flush();
    }

    private byte[] getBufferContents() {
        byte[] contents = this.buffer.toByteArray();
        this.buffer.reset();
        return contents;
    }

    private static byte[] doFinal(Digest d) {
        byte[] bs = new byte[d.getDigestSize()];
        d.doFinal(bs, 0);
        return bs;
    }
}