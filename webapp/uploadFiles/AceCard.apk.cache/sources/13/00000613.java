package org.spongycastle.crypto.digests;

import ch.boye.httpclientandroidlib.HttpStatus;
import org.spongycastle.asn1.eac.CertificateBody;
import org.spongycastle.asn1.eac.CertificateHolderAuthorization;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public final class WhirlpoolDigest implements ExtendedDigest {
    private static final int BITCOUNT_ARRAY_SIZE = 32;
    private static final int BYTE_LENGTH = 64;
    private static final int DIGEST_LENGTH_BYTES = 64;
    private static final int REDUCTION_POLYNOMIAL = 285;
    private static final int ROUNDS = 10;
    private long[] _K;
    private long[] _L;
    private short[] _bitCount;
    private long[] _block;
    private byte[] _buffer;
    private int _bufferPos;
    private long[] _hash;
    private final long[] _rc;
    private long[] _state;
    private static final int[] SBOX = {24, 35, 198, 232, 135, 184, 1, 79, 54, 166, 210, 245, EACTags.COEXISTANT_TAG_ALLOCATION_AUTHORITY, EACTags.FCI_TEMPLATE, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA, 82, 96, 188, 155, CipherSuite.TLS_DHE_PSK_WITH_RC4_128_SHA, 163, 12, EACTags.SECURITY_ENVIRONMENT_TEMPLATE, 53, 29, 224, 215, 194, 46, 75, 254, 87, 21, 119, 55, 229, 159, 240, 74, 218, 88, HttpStatus.SC_CREATED, 41, 10, 177, 160, EACTags.QUALIFIED_NAME, 133, 189, 93, 16, 244, HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION, 62, 5, EACTags.AUTHENTIFICATION_DATA, 228, 39, 65, CipherSuite.TLS_PSK_WITH_3DES_EDE_CBC_SHA, 167, EACTags.SECURE_MESSAGING_TEMPLATE, CipherSuite.TLS_RSA_PSK_WITH_AES_256_CBC_SHA, 216, 251, 238, EACTags.DYNAMIC_AUTHENTIFICATION_TEMPLATE, 102, 221, 23, 71, 158, HttpStatus.SC_ACCEPTED, 45, 191, 7, 173, 90, 131, 51, 99, 2, 170, 113, 200, 25, 73, 217, 242, 227, 91, 136, 154, 38, 50, 176, 233, 15, 213, 128, 190, HttpStatus.SC_RESET_CONTENT, 52, 72, 255, EACTags.SECURITY_SUPPORT_TEMPLATE, CipherSuite.TLS_DHE_PSK_WITH_AES_128_CBC_SHA, 95, 32, EACTags.SPECIAL_USER_REQUIREMENTS, 26, 174, 180, 84, CipherSuite.TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA, 34, 100, 241, EACTags.DISCRETIONARY_DATA_OBJECTS, 18, 64, 8, 195, 236, 219, 161, CipherSuite.TLS_PSK_WITH_AES_256_CBC_SHA, 61, 151, 0, HttpStatus.SC_MULTI_STATUS, 43, 118, 130, 214, 27, 181, 175, EACTags.LOGIN_TEMPLATE, 80, 69, 243, 48, 239, 63, 85, 162, 234, 101, 186, 47, CertificateHolderAuthorization.CVCA, 222, 28, 253, 77, CipherSuite.TLS_RSA_PSK_WITH_RC4_128_SHA, 117, 6, CipherSuite.TLS_PSK_WITH_RC4_128_SHA, 178, 230, 14, 31, 98, 212, 168, 150, 249, 197, 37, 89, 132, 114, 57, 76, 94, EACTags.COMPATIBLE_TAG_ALLOCATION_AUTHORITY, 56, CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA, 209, 165, 226, 97, 179, 33, 156, 30, 67, 199, 252, 4, 81, 153, EACTags.APPLICATION_IMAGE_TEMPLATE, 13, 250, 223, EACTags.NON_INTERINDUSTRY_DATA_OBJECT_NESTING_TEMPLATE, 36, 59, 171, HttpStatus.SC_PARTIAL_CONTENT, 17, CipherSuite.TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA, 78, 183, 235, 60, 129, CipherSuite.TLS_RSA_PSK_WITH_AES_128_CBC_SHA, 247, 185, 19, 44, 211, 231, EACTags.APPLICATION_RELATED_DATA, 196, 3, 86, 68, CertificateBody.profileType, 169, 42, 187, 193, 83, 220, 11, 157, EACTags.CARDHOLDER_IMAGE_TEMPLATE, 49, 116, 246, 70, 172, 137, 20, 225, 22, 58, 105, 9, 112, 182, 208, 237, HttpStatus.SC_NO_CONTENT, 66, 152, 164, 40, 92, 248, 134};
    private static final long[] C0 = new long[256];
    private static final long[] C1 = new long[256];
    private static final long[] C2 = new long[256];
    private static final long[] C3 = new long[256];
    private static final long[] C4 = new long[256];
    private static final long[] C5 = new long[256];
    private static final long[] C6 = new long[256];
    private static final long[] C7 = new long[256];
    private static final short[] EIGHT = new short[32];

    static {
        EIGHT[31] = 8;
    }

    public WhirlpoolDigest() {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        for (int i = 0; i < 256; i++) {
            int v1 = SBOX[i];
            int v2 = maskWithReductionPolynomial(v1 << 1);
            int v4 = maskWithReductionPolynomial(v2 << 1);
            int v5 = v4 ^ v1;
            int v8 = maskWithReductionPolynomial(v4 << 1);
            int v9 = v8 ^ v1;
            C0[i] = packIntoLong(v1, v1, v4, v1, v8, v5, v2, v9);
            C1[i] = packIntoLong(v9, v1, v1, v4, v1, v8, v5, v2);
            C2[i] = packIntoLong(v2, v9, v1, v1, v4, v1, v8, v5);
            C3[i] = packIntoLong(v5, v2, v9, v1, v1, v4, v1, v8);
            C4[i] = packIntoLong(v8, v5, v2, v9, v1, v1, v4, v1);
            C5[i] = packIntoLong(v1, v8, v5, v2, v9, v1, v1, v4);
            C6[i] = packIntoLong(v4, v1, v8, v5, v2, v9, v1, v1);
            C7[i] = packIntoLong(v1, v4, v1, v8, v5, v2, v9, v1);
        }
        this._rc[0] = 0;
        for (int r = 1; r <= 10; r++) {
            int i2 = (r - 1) * 8;
            this._rc[r] = (((((((C0[i2] & (-72057594037927936L)) ^ (C1[i2 + 1] & 71776119061217280L)) ^ (C2[i2 + 2] & 280375465082880L)) ^ (C3[i2 + 3] & 1095216660480L)) ^ (C4[i2 + 4] & 4278190080L)) ^ (C5[i2 + 5] & 16711680)) ^ (C6[i2 + 6] & 65280)) ^ (C7[i2 + 7] & 255);
        }
    }

    private long packIntoLong(int b7, int b6, int b5, int b4, int b3, int b2, int b1, int b0) {
        return (((((((b7 << 56) ^ (b6 << 48)) ^ (b5 << 40)) ^ (b4 << 32)) ^ (b3 << 24)) ^ (b2 << 16)) ^ (b1 << 8)) ^ b0;
    }

    private int maskWithReductionPolynomial(int input) {
        if (input < 256) {
            return input;
        }
        int rv = input ^ REDUCTION_POLYNOMIAL;
        return rv;
    }

    public WhirlpoolDigest(WhirlpoolDigest originalDigest) {
        this._rc = new long[11];
        this._buffer = new byte[64];
        this._bufferPos = 0;
        this._bitCount = new short[32];
        this._hash = new long[8];
        this._K = new long[8];
        this._L = new long[8];
        this._block = new long[8];
        this._state = new long[8];
        System.arraycopy(originalDigest._rc, 0, this._rc, 0, this._rc.length);
        System.arraycopy(originalDigest._buffer, 0, this._buffer, 0, this._buffer.length);
        this._bufferPos = originalDigest._bufferPos;
        System.arraycopy(originalDigest._bitCount, 0, this._bitCount, 0, this._bitCount.length);
        System.arraycopy(originalDigest._hash, 0, this._hash, 0, this._hash.length);
        System.arraycopy(originalDigest._K, 0, this._K, 0, this._K.length);
        System.arraycopy(originalDigest._L, 0, this._L, 0, this._L.length);
        System.arraycopy(originalDigest._block, 0, this._block, 0, this._block.length);
        System.arraycopy(originalDigest._state, 0, this._state, 0, this._state.length);
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "Whirlpool";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 64;
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        for (int i = 0; i < 8; i++) {
            convertLongToByteArray(this._hash[i], out, (i * 8) + outOff);
        }
        reset();
        return getDigestSize();
    }

    @Override // org.spongycastle.crypto.Digest
    public void reset() {
        this._bufferPos = 0;
        Arrays.fill(this._bitCount, (short) 0);
        Arrays.fill(this._buffer, (byte) 0);
        Arrays.fill(this._hash, 0L);
        Arrays.fill(this._K, 0L);
        Arrays.fill(this._L, 0L);
        Arrays.fill(this._block, 0L);
        Arrays.fill(this._state, 0L);
    }

    private void processFilledBuffer(byte[] in, int inOff) {
        for (int i = 0; i < this._state.length; i++) {
            this._block[i] = bytesToLongFromBuffer(this._buffer, i * 8);
        }
        processBlock();
        this._bufferPos = 0;
        Arrays.fill(this._buffer, (byte) 0);
    }

    private long bytesToLongFromBuffer(byte[] buffer, int startPos) {
        long rv = ((buffer[startPos + 0] & 255) << 56) | ((buffer[startPos + 1] & 255) << 48) | ((buffer[startPos + 2] & 255) << 40) | ((buffer[startPos + 3] & 255) << 32) | ((buffer[startPos + 4] & 255) << 24) | ((buffer[startPos + 5] & 255) << 16) | ((buffer[startPos + 6] & 255) << 8) | (buffer[startPos + 7] & 255);
        return rv;
    }

    private void convertLongToByteArray(long inputLong, byte[] outputArray, int offSet) {
        for (int i = 0; i < 8; i++) {
            outputArray[offSet + i] = (byte) ((inputLong >> (56 - (i * 8))) & 255);
        }
    }

    protected void processBlock() {
        for (int i = 0; i < 8; i++) {
            long[] jArr = this._state;
            long j = this._block[i];
            long[] jArr2 = this._K;
            long j2 = this._hash[i];
            jArr2[i] = j2;
            jArr[i] = j ^ j2;
        }
        for (int round = 1; round <= 10; round++) {
            for (int i2 = 0; i2 < 8; i2++) {
                this._L[i2] = 0;
                long[] jArr3 = this._L;
                jArr3[i2] = jArr3[i2] ^ C0[((int) (this._K[(i2 + 0) & 7] >>> 56)) & 255];
                long[] jArr4 = this._L;
                jArr4[i2] = jArr4[i2] ^ C1[((int) (this._K[(i2 - 1) & 7] >>> 48)) & 255];
                long[] jArr5 = this._L;
                jArr5[i2] = jArr5[i2] ^ C2[((int) (this._K[(i2 - 2) & 7] >>> 40)) & 255];
                long[] jArr6 = this._L;
                jArr6[i2] = jArr6[i2] ^ C3[((int) (this._K[(i2 - 3) & 7] >>> 32)) & 255];
                long[] jArr7 = this._L;
                jArr7[i2] = jArr7[i2] ^ C4[((int) (this._K[(i2 - 4) & 7] >>> 24)) & 255];
                long[] jArr8 = this._L;
                jArr8[i2] = jArr8[i2] ^ C5[((int) (this._K[(i2 - 5) & 7] >>> 16)) & 255];
                long[] jArr9 = this._L;
                jArr9[i2] = jArr9[i2] ^ C6[((int) (this._K[(i2 - 6) & 7] >>> 8)) & 255];
                long[] jArr10 = this._L;
                jArr10[i2] = jArr10[i2] ^ C7[((int) this._K[(i2 - 7) & 7]) & 255];
            }
            System.arraycopy(this._L, 0, this._K, 0, this._K.length);
            long[] jArr11 = this._K;
            jArr11[0] = jArr11[0] ^ this._rc[round];
            for (int i3 = 0; i3 < 8; i3++) {
                this._L[i3] = this._K[i3];
                long[] jArr12 = this._L;
                jArr12[i3] = jArr12[i3] ^ C0[((int) (this._state[(i3 + 0) & 7] >>> 56)) & 255];
                long[] jArr13 = this._L;
                jArr13[i3] = jArr13[i3] ^ C1[((int) (this._state[(i3 - 1) & 7] >>> 48)) & 255];
                long[] jArr14 = this._L;
                jArr14[i3] = jArr14[i3] ^ C2[((int) (this._state[(i3 - 2) & 7] >>> 40)) & 255];
                long[] jArr15 = this._L;
                jArr15[i3] = jArr15[i3] ^ C3[((int) (this._state[(i3 - 3) & 7] >>> 32)) & 255];
                long[] jArr16 = this._L;
                jArr16[i3] = jArr16[i3] ^ C4[((int) (this._state[(i3 - 4) & 7] >>> 24)) & 255];
                long[] jArr17 = this._L;
                jArr17[i3] = jArr17[i3] ^ C5[((int) (this._state[(i3 - 5) & 7] >>> 16)) & 255];
                long[] jArr18 = this._L;
                jArr18[i3] = jArr18[i3] ^ C6[((int) (this._state[(i3 - 6) & 7] >>> 8)) & 255];
                long[] jArr19 = this._L;
                jArr19[i3] = jArr19[i3] ^ C7[((int) this._state[(i3 - 7) & 7]) & 255];
            }
            System.arraycopy(this._L, 0, this._state, 0, this._state.length);
        }
        for (int i4 = 0; i4 < 8; i4++) {
            long[] jArr20 = this._hash;
            jArr20[i4] = jArr20[i4] ^ (this._state[i4] ^ this._block[i4]);
        }
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte in) {
        this._buffer[this._bufferPos] = in;
        this._bufferPos++;
        if (this._bufferPos == this._buffer.length) {
            processFilledBuffer(this._buffer, 0);
        }
        increment();
    }

    private void increment() {
        int carry = 0;
        for (int i = this._bitCount.length - 1; i >= 0; i--) {
            int sum = (this._bitCount[i] & 255) + EIGHT[i] + carry;
            carry = sum >>> 8;
            this._bitCount[i] = (short) (sum & 255);
        }
    }

    @Override // org.spongycastle.crypto.Digest
    public void update(byte[] in, int inOff, int len) {
        while (len > 0) {
            update(in[inOff]);
            inOff++;
            len--;
        }
    }

    private void finish() {
        byte[] bitLength = copyBitLength();
        byte[] bArr = this._buffer;
        int i = this._bufferPos;
        this._bufferPos = i + 1;
        bArr[i] = (byte) (bArr[i] | 128);
        if (this._bufferPos == this._buffer.length) {
            processFilledBuffer(this._buffer, 0);
        }
        if (this._bufferPos > 32) {
            while (this._bufferPos != 0) {
                update((byte) 0);
            }
        }
        while (this._bufferPos <= 32) {
            update((byte) 0);
        }
        System.arraycopy(bitLength, 0, this._buffer, 32, bitLength.length);
        processFilledBuffer(this._buffer, 0);
    }

    private byte[] copyBitLength() {
        byte[] rv = new byte[32];
        for (int i = 0; i < rv.length; i++) {
            rv[i] = (byte) (this._bitCount[i] & 255);
        }
        return rv;
    }

    @Override // org.spongycastle.crypto.ExtendedDigest
    public int getByteLength() {
        return 64;
    }
}