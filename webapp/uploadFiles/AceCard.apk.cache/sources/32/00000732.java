package org.spongycastle.crypto.tls;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.x509.KeyUsage;
import org.spongycastle.asn1.x509.X509CertificateStructure;
import org.spongycastle.asn1.x509.X509Extension;
import org.spongycastle.asn1.x509.X509Extensions;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.MD5Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Strings;
import org.spongycastle.util.io.Streams;

/* loaded from: classes.dex */
public class TlsUtils {
    static final byte[] SSL_CLIENT = {67, 76, 78, 84};
    static final byte[] SSL_SERVER = {83, 82, 86, 82};
    static final byte[][] SSL3_CONST = genConst();

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint8(short i, OutputStream os) throws IOException {
        os.write(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint8(short i, byte[] buf, int offset) {
        buf[offset] = (byte) i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint16(int i, OutputStream os) throws IOException {
        os.write(i >> 8);
        os.write(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint16(int i, byte[] buf, int offset) {
        buf[offset] = (byte) (i >> 8);
        buf[offset + 1] = (byte) i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint24(int i, OutputStream os) throws IOException {
        os.write(i >> 16);
        os.write(i >> 8);
        os.write(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint24(int i, byte[] buf, int offset) {
        buf[offset] = (byte) (i >> 16);
        buf[offset + 1] = (byte) (i >> 8);
        buf[offset + 2] = (byte) i;
    }

    protected static void writeUint32(long i, OutputStream os) throws IOException {
        os.write((int) (i >> 24));
        os.write((int) (i >> 16));
        os.write((int) (i >> 8));
        os.write((int) i);
    }

    protected static void writeUint32(long i, byte[] buf, int offset) {
        buf[offset] = (byte) (i >> 24);
        buf[offset + 1] = (byte) (i >> 16);
        buf[offset + 2] = (byte) (i >> 8);
        buf[offset + 3] = (byte) i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint64(long i, OutputStream os) throws IOException {
        os.write((int) (i >> 56));
        os.write((int) (i >> 48));
        os.write((int) (i >> 40));
        os.write((int) (i >> 32));
        os.write((int) (i >> 24));
        os.write((int) (i >> 16));
        os.write((int) (i >> 8));
        os.write((int) i);
    }

    protected static void writeUint64(long i, byte[] buf, int offset) {
        buf[offset] = (byte) (i >> 56);
        buf[offset + 1] = (byte) (i >> 48);
        buf[offset + 2] = (byte) (i >> 40);
        buf[offset + 3] = (byte) (i >> 32);
        buf[offset + 4] = (byte) (i >> 24);
        buf[offset + 5] = (byte) (i >> 16);
        buf[offset + 6] = (byte) (i >> 8);
        buf[offset + 7] = (byte) i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeOpaque8(byte[] buf, OutputStream os) throws IOException {
        writeUint8((short) buf.length, os);
        os.write(buf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeOpaque16(byte[] buf, OutputStream os) throws IOException {
        writeUint16(buf.length, os);
        os.write(buf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeOpaque24(byte[] buf, OutputStream os) throws IOException {
        writeUint24(buf.length, os);
        os.write(buf);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint8Array(short[] uints, OutputStream os) throws IOException {
        for (short s : uints) {
            writeUint8(s, os);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeUint16Array(int[] uints, OutputStream os) throws IOException {
        for (int i : uints) {
            writeUint16(i, os);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static short readUint8(InputStream is) throws IOException {
        int i = is.read();
        if (i == -1) {
            throw new EOFException();
        }
        return (short) i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int readUint16(InputStream is) throws IOException {
        int i1 = is.read();
        int i2 = is.read();
        if ((i1 | i2) < 0) {
            throw new EOFException();
        }
        return (i1 << 8) | i2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int readUint24(InputStream is) throws IOException {
        int i1 = is.read();
        int i2 = is.read();
        int i3 = is.read();
        if ((i1 | i2 | i3) < 0) {
            throw new EOFException();
        }
        return (i1 << 16) | (i2 << 8) | i3;
    }

    protected static long readUint32(InputStream is) throws IOException {
        int i1 = is.read();
        int i2 = is.read();
        int i3 = is.read();
        int i4 = is.read();
        if ((i1 | i2 | i3 | i4) < 0) {
            throw new EOFException();
        }
        return (i1 << 24) | (i2 << 16) | (i3 << 8) | i4;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void readFully(byte[] buf, InputStream is) throws IOException {
        if (Streams.readFully(is, buf) != buf.length) {
            throw new EOFException();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte[] readOpaque8(InputStream is) throws IOException {
        byte[] value = new byte[readUint8(is)];
        readFully(value, is);
        return value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte[] readOpaque16(InputStream is) throws IOException {
        int length = readUint16(is);
        byte[] value = new byte[length];
        readFully(value, is);
        return value;
    }

    static ProtocolVersion readVersion(byte[] buf) throws IOException {
        return ProtocolVersion.get(buf[0], buf[1]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ProtocolVersion readVersion(InputStream is) throws IOException {
        int i1 = is.read();
        int i2 = is.read();
        return ProtocolVersion.get(i1, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void writeGMTUnixTime(byte[] buf, int offset) {
        int t = (int) (System.currentTimeMillis() / 1000);
        buf[offset] = (byte) (t >> 24);
        buf[offset + 1] = (byte) (t >> 16);
        buf[offset + 2] = (byte) (t >> 8);
        buf[offset + 3] = (byte) t;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeVersion(ProtocolVersion version, OutputStream os) throws IOException {
        os.write(version.getMajorVersion());
        os.write(version.getMinorVersion());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void writeVersion(ProtocolVersion version, byte[] buf, int offset) throws IOException {
        buf[offset] = (byte) version.getMajorVersion();
        buf[offset + 1] = (byte) version.getMinorVersion();
    }

    private static void hmac_hash(Digest digest, byte[] secret, byte[] seed, byte[] out) {
        HMac mac = new HMac(digest);
        KeyParameter param = new KeyParameter(secret);
        byte[] a = seed;
        int size = digest.getDigestSize();
        int iterations = ((out.length + size) - 1) / size;
        byte[] buf = new byte[mac.getMacSize()];
        byte[] buf2 = new byte[mac.getMacSize()];
        for (int i = 0; i < iterations; i++) {
            mac.init(param);
            mac.update(a, 0, a.length);
            mac.doFinal(buf, 0);
            a = buf;
            mac.init(param);
            mac.update(a, 0, a.length);
            mac.update(seed, 0, seed.length);
            mac.doFinal(buf2, 0);
            System.arraycopy(buf2, 0, out, size * i, Math.min(size, out.length - (size * i)));
        }
    }

    protected static byte[] PRF(byte[] secret, String asciiLabel, byte[] seed, int size) {
        byte[] label = Strings.toByteArray(asciiLabel);
        int s_half = (secret.length + 1) / 2;
        byte[] s1 = new byte[s_half];
        byte[] s2 = new byte[s_half];
        System.arraycopy(secret, 0, s1, 0, s_half);
        System.arraycopy(secret, secret.length - s_half, s2, 0, s_half);
        byte[] ls = concat(label, seed);
        byte[] buf = new byte[size];
        byte[] prf = new byte[size];
        hmac_hash(new MD5Digest(), s1, ls, prf);
        hmac_hash(new SHA1Digest(), s2, ls, buf);
        for (int i = 0; i < size; i++) {
            buf[i] = (byte) (buf[i] ^ prf[i]);
        }
        return buf;
    }

    static byte[] PRF_1_2(Digest digest, byte[] secret, String asciiLabel, byte[] seed, int size) {
        byte[] label = Strings.toByteArray(asciiLabel);
        byte[] labelSeed = concat(label, seed);
        byte[] buf = new byte[size];
        hmac_hash(digest, secret, labelSeed, buf);
        return buf;
    }

    static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void validateKeyUsage(X509CertificateStructure c, int keyUsageBits) throws IOException {
        X509Extension ext;
        X509Extensions exts = c.getTBSCertificate().getExtensions();
        if (exts != null && (ext = exts.getExtension(X509Extension.keyUsage)) != null) {
            DERBitString ku = KeyUsage.getInstance(ext);
            int bits = ku.getBytes()[0] & 255;
            if ((bits & keyUsageBits) != keyUsageBits) {
                throw new TlsFatalAlert((short) 46);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] calculateKeyBlock(TlsClientContext context, int size) {
        ProtocolVersion pv = context.getServerVersion();
        SecurityParameters sp = context.getSecurityParameters();
        byte[] random = concat(sp.serverRandom, sp.clientRandom);
        boolean isTls = pv.getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        if (isTls) {
            return PRF(sp.masterSecret, "key expansion", random, size);
        }
        Digest md5 = new MD5Digest();
        Digest sha1 = new SHA1Digest();
        int md5Size = md5.getDigestSize();
        byte[] shatmp = new byte[sha1.getDigestSize()];
        byte[] tmp = new byte[size + md5Size];
        int i = 0;
        int pos = 0;
        while (pos < size) {
            byte[] ssl3Const = SSL3_CONST[i];
            sha1.update(ssl3Const, 0, ssl3Const.length);
            sha1.update(sp.masterSecret, 0, sp.masterSecret.length);
            sha1.update(random, 0, random.length);
            sha1.doFinal(shatmp, 0);
            md5.update(sp.masterSecret, 0, sp.masterSecret.length);
            md5.update(shatmp, 0, shatmp.length);
            md5.doFinal(tmp, pos);
            pos += md5Size;
            i++;
        }
        byte[] rval = new byte[size];
        System.arraycopy(tmp, 0, rval, 0, size);
        return rval;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] calculateMasterSecret(TlsClientContext context, byte[] pms) {
        ProtocolVersion pv = context.getServerVersion();
        SecurityParameters sp = context.getSecurityParameters();
        byte[] random = concat(sp.clientRandom, sp.serverRandom);
        boolean isTls = pv.getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        if (isTls) {
            return PRF(pms, "master secret", random, 48);
        }
        Digest md5 = new MD5Digest();
        Digest sha1 = new SHA1Digest();
        int md5Size = md5.getDigestSize();
        byte[] shatmp = new byte[sha1.getDigestSize()];
        byte[] rval = new byte[md5Size * 3];
        int pos = 0;
        for (int i = 0; i < 3; i++) {
            byte[] ssl3Const = SSL3_CONST[i];
            sha1.update(ssl3Const, 0, ssl3Const.length);
            sha1.update(pms, 0, pms.length);
            sha1.update(random, 0, random.length);
            sha1.doFinal(shatmp, 0);
            md5.update(pms, 0, pms.length);
            md5.update(shatmp, 0, shatmp.length);
            md5.doFinal(rval, pos);
            pos += md5Size;
        }
        return rval;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static byte[] calculateVerifyData(TlsClientContext context, String asciiLabel, byte[] handshakeHash) {
        ProtocolVersion pv = context.getServerVersion();
        SecurityParameters sp = context.getSecurityParameters();
        boolean isTls = pv.getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        if (isTls) {
            return PRF(sp.masterSecret, asciiLabel, handshakeHash, 12);
        }
        return handshakeHash;
    }

    private static byte[][] genConst() {
        byte[][] arr = new byte[10];
        for (int i = 0; i < 10; i++) {
            byte[] b = new byte[i + 1];
            Arrays.fill(b, (byte) (i + 65));
            arr[i] = b;
        }
        return arr;
    }
}