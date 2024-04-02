package org.spongycastle.crypto.params;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.KeyGenerationParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.tls.CipherSuite;

/* loaded from: classes.dex */
public class NTRUEncryptionKeyGenerationParameters extends KeyGenerationParameters implements Cloneable {
    public int N;
    public int bufferLenBits;
    int bufferLenTrits;
    public int c;
    public int db;
    public int df;
    public int df1;
    public int df2;
    public int df3;
    public int dg;
    public int dm0;
    public int dr;
    public int dr1;
    public int dr2;
    public int dr3;
    public boolean fastFp;
    public Digest hashAlg;
    public boolean hashSeed;
    int llen;
    public int maxMsgLenBytes;
    public int minCallsMask;
    public int minCallsR;
    public byte[] oid;
    public int pkLen;
    public int polyType;
    public int q;
    public boolean sparse;
    public static final NTRUEncryptionKeyGenerationParameters EES1087EP2 = new NTRUEncryptionKeyGenerationParameters(1087, 2048, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, 256, 13, 25, 14, true, new byte[]{0, 6, 3}, true, false, new SHA512Digest());
    public static final NTRUEncryptionKeyGenerationParameters EES1171EP1 = new NTRUEncryptionKeyGenerationParameters(1171, 2048, EACTags.LOGIN_TEMPLATE, EACTags.LOGIN_TEMPLATE, 256, 13, 20, 15, true, new byte[]{0, 6, 4}, true, false, new SHA512Digest());
    public static final NTRUEncryptionKeyGenerationParameters EES1499EP1 = new NTRUEncryptionKeyGenerationParameters(1499, 2048, 79, 79, 256, 13, 17, 19, true, new byte[]{0, 6, 5}, true, false, new SHA512Digest());
    public static final NTRUEncryptionKeyGenerationParameters APR2011_439 = new NTRUEncryptionKeyGenerationParameters(439, 2048, CipherSuite.TLS_RSA_PSK_WITH_RC4_128_SHA, 130, 128, 9, 32, 9, true, new byte[]{0, 7, 101}, true, false, new SHA256Digest());
    public static final NTRUEncryptionKeyGenerationParameters APR2011_439_FAST = new NTRUEncryptionKeyGenerationParameters(439, 2048, 9, 8, 5, 130, 128, 9, 32, 9, true, new byte[]{0, 7, 101}, true, true, new SHA256Digest());
    public static final NTRUEncryptionKeyGenerationParameters APR2011_743 = new NTRUEncryptionKeyGenerationParameters(743, 2048, 248, 220, 256, 10, 27, 14, true, new byte[]{0, 7, 105}, false, false, new SHA512Digest());
    public static final NTRUEncryptionKeyGenerationParameters APR2011_743_FAST = new NTRUEncryptionKeyGenerationParameters(743, 2048, 11, 11, 15, 220, 256, 10, 27, 14, true, new byte[]{0, 7, 105}, false, true, new SHA512Digest());

    public NTRUEncryptionKeyGenerationParameters(int N, int q, int df, int dm0, int db, int c, int minCallsR, int minCallsMask, boolean hashSeed, byte[] oid, boolean sparse, boolean fastFp, Digest hashAlg) {
        super(new SecureRandom(), db);
        this.N = N;
        this.q = q;
        this.df = df;
        this.db = db;
        this.dm0 = dm0;
        this.c = c;
        this.minCallsR = minCallsR;
        this.minCallsMask = minCallsMask;
        this.hashSeed = hashSeed;
        this.oid = oid;
        this.sparse = sparse;
        this.fastFp = fastFp;
        this.polyType = 0;
        this.hashAlg = hashAlg;
        init();
    }

    public NTRUEncryptionKeyGenerationParameters(int N, int q, int df1, int df2, int df3, int dm0, int db, int c, int minCallsR, int minCallsMask, boolean hashSeed, byte[] oid, boolean sparse, boolean fastFp, Digest hashAlg) {
        super(new SecureRandom(), db);
        this.N = N;
        this.q = q;
        this.df1 = df1;
        this.df2 = df2;
        this.df3 = df3;
        this.db = db;
        this.dm0 = dm0;
        this.c = c;
        this.minCallsR = minCallsR;
        this.minCallsMask = minCallsMask;
        this.hashSeed = hashSeed;
        this.oid = oid;
        this.sparse = sparse;
        this.fastFp = fastFp;
        this.polyType = 1;
        this.hashAlg = hashAlg;
        init();
    }

    private void init() {
        this.dr = this.df;
        this.dr1 = this.df1;
        this.dr2 = this.df2;
        this.dr3 = this.df3;
        this.dg = this.N / 3;
        this.llen = 1;
        this.maxMsgLenBytes = (((((this.N * 3) / 2) / 8) - this.llen) - (this.db / 8)) - 1;
        this.bufferLenBits = (((((this.N * 3) / 2) + 7) / 8) * 8) + 1;
        this.bufferLenTrits = this.N - 1;
        this.pkLen = this.db;
    }

    public NTRUEncryptionKeyGenerationParameters(InputStream is) throws IOException {
        super(new SecureRandom(), -1);
        DataInputStream dis = new DataInputStream(is);
        this.N = dis.readInt();
        this.q = dis.readInt();
        this.df = dis.readInt();
        this.df1 = dis.readInt();
        this.df2 = dis.readInt();
        this.df3 = dis.readInt();
        this.db = dis.readInt();
        this.dm0 = dis.readInt();
        this.c = dis.readInt();
        this.minCallsR = dis.readInt();
        this.minCallsMask = dis.readInt();
        this.hashSeed = dis.readBoolean();
        this.oid = new byte[3];
        dis.read(this.oid);
        this.sparse = dis.readBoolean();
        this.fastFp = dis.readBoolean();
        this.polyType = dis.read();
        String alg = dis.readUTF();
        if ("SHA-512".equals(alg)) {
            this.hashAlg = new SHA512Digest();
        } else if ("SHA-256".equals(alg)) {
            this.hashAlg = new SHA256Digest();
        }
        init();
    }

    public NTRUEncryptionParameters getEncryptionParameters() {
        return this.polyType == 0 ? new NTRUEncryptionParameters(this.N, this.q, this.df, this.dm0, this.db, this.c, this.minCallsR, this.minCallsMask, this.hashSeed, this.oid, this.sparse, this.fastFp, this.hashAlg) : new NTRUEncryptionParameters(this.N, this.q, this.df1, this.df2, this.df3, this.dm0, this.db, this.c, this.minCallsR, this.minCallsMask, this.hashSeed, this.oid, this.sparse, this.fastFp, this.hashAlg);
    }

    public NTRUEncryptionKeyGenerationParameters clone() {
        return this.polyType == 0 ? new NTRUEncryptionKeyGenerationParameters(this.N, this.q, this.df, this.dm0, this.db, this.c, this.minCallsR, this.minCallsMask, this.hashSeed, this.oid, this.sparse, this.fastFp, this.hashAlg) : new NTRUEncryptionKeyGenerationParameters(this.N, this.q, this.df1, this.df2, this.df3, this.dm0, this.db, this.c, this.minCallsR, this.minCallsMask, this.hashSeed, this.oid, this.sparse, this.fastFp, this.hashAlg);
    }

    public int getMaxMessageLength() {
        return this.maxMsgLenBytes;
    }

    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(this.N);
        dos.writeInt(this.q);
        dos.writeInt(this.df);
        dos.writeInt(this.df1);
        dos.writeInt(this.df2);
        dos.writeInt(this.df3);
        dos.writeInt(this.db);
        dos.writeInt(this.dm0);
        dos.writeInt(this.c);
        dos.writeInt(this.minCallsR);
        dos.writeInt(this.minCallsMask);
        dos.writeBoolean(this.hashSeed);
        dos.write(this.oid);
        dos.writeBoolean(this.sparse);
        dos.writeBoolean(this.fastFp);
        dos.write(this.polyType);
        dos.writeUTF(this.hashAlg.getAlgorithmName());
    }

    public int hashCode() {
        int result = this.N + 31;
        return (((((((((((((((((((((((((((((((((((((((((((((((((((result * 31) + this.bufferLenBits) * 31) + this.bufferLenTrits) * 31) + this.c) * 31) + this.db) * 31) + this.df) * 31) + this.df1) * 31) + this.df2) * 31) + this.df3) * 31) + this.dg) * 31) + this.dm0) * 31) + this.dr) * 31) + this.dr1) * 31) + this.dr2) * 31) + this.dr3) * 31) + (this.fastFp ? 1231 : 1237)) * 31) + (this.hashAlg == null ? 0 : this.hashAlg.getAlgorithmName().hashCode())) * 31) + (this.hashSeed ? 1231 : 1237)) * 31) + this.llen) * 31) + this.maxMsgLenBytes) * 31) + this.minCallsMask) * 31) + this.minCallsR) * 31) + Arrays.hashCode(this.oid)) * 31) + this.pkLen) * 31) + this.polyType) * 31) + this.q) * 31) + (this.sparse ? 1231 : 1237);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            NTRUEncryptionKeyGenerationParameters other = (NTRUEncryptionKeyGenerationParameters) obj;
            if (this.N == other.N && this.bufferLenBits == other.bufferLenBits && this.bufferLenTrits == other.bufferLenTrits && this.c == other.c && this.db == other.db && this.df == other.df && this.df1 == other.df1 && this.df2 == other.df2 && this.df3 == other.df3 && this.dg == other.dg && this.dm0 == other.dm0 && this.dr == other.dr && this.dr1 == other.dr1 && this.dr2 == other.dr2 && this.dr3 == other.dr3 && this.fastFp == other.fastFp) {
                if (this.hashAlg == null) {
                    if (other.hashAlg != null) {
                        return false;
                    }
                } else if (!this.hashAlg.getAlgorithmName().equals(other.hashAlg.getAlgorithmName())) {
                    return false;
                }
                return this.hashSeed == other.hashSeed && this.llen == other.llen && this.maxMsgLenBytes == other.maxMsgLenBytes && this.minCallsMask == other.minCallsMask && this.minCallsR == other.minCallsR && Arrays.equals(this.oid, other.oid) && this.pkLen == other.pkLen && this.polyType == other.polyType && this.q == other.q && this.sparse == other.sparse;
            }
            return false;
        }
        return false;
    }

    public String toString() {
        StringBuilder output = new StringBuilder("EncryptionParameters(N=" + this.N + " q=" + this.q);
        if (this.polyType == 0) {
            output.append(" polyType=SIMPLE df=" + this.df);
        } else {
            output.append(" polyType=PRODUCT df1=" + this.df1 + " df2=" + this.df2 + " df3=" + this.df3);
        }
        output.append(" dm0=" + this.dm0 + " db=" + this.db + " c=" + this.c + " minCallsR=" + this.minCallsR + " minCallsMask=" + this.minCallsMask + " hashSeed=" + this.hashSeed + " hashAlg=" + this.hashAlg + " oid=" + Arrays.toString(this.oid) + " sparse=" + this.sparse + ")");
        return output.toString();
    }
}