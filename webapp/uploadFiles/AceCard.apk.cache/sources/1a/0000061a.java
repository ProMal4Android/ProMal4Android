package org.spongycastle.crypto.engines;

import java.lang.reflect.Array;
import net.freehaven.tor.control.TorControlCommands;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.signers.PSSSigner;
import org.spongycastle.crypto.tls.CipherSuite;
import org.spongycastle.math.ec.Tnaf;

/* loaded from: classes.dex */
public class AESLightEngine implements BlockCipher {
    private static final int BLOCK_SIZE = 16;
    private static final int m1 = -2139062144;
    private static final int m2 = 2139062143;
    private static final int m3 = 27;
    private int C0;
    private int C1;
    private int C2;
    private int C3;
    private int ROUNDS;
    private int[][] WorkingKey = null;
    private boolean forEncryption;
    private static final byte[] S = {99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, Byte.MIN_VALUE, -30, -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, Byte.MAX_VALUE, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, PSSSigner.TRAILER_IMPLICIT, -74, -38, 33, Tnaf.POW_2_WIDTH, -1, -13, -46, -51, TorControlCommands.SIGNAL_USR2, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, TorControlCommands.SIGNAL_TERM, -80, 84, -69, 22};
    private static final byte[] Si = {82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, -112, -40, -85, 0, -116, PSSSigner.TRAILER_IMPLICIT, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, -48, 44, 30, -113, -54, 63, TorControlCommands.SIGNAL_TERM, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27, -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, Tnaf.POW_2_WIDTH, 89, 39, Byte.MIN_VALUE, -20, 95, 96, 81, Byte.MAX_VALUE, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, TorControlCommands.SIGNAL_USR2, 125};
    private static final int[] rcon = {1, 2, 4, 8, 16, 32, 64, 128, 27, 54, EACTags.CARDHOLDER_IMAGE_TEMPLATE, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, EACTags.LOGIN_TEMPLATE, 212, 179, EACTags.SECURE_MESSAGING_TEMPLATE, 250, 239, 197, CipherSuite.TLS_DHE_PSK_WITH_AES_256_CBC_SHA};

    private int shift(int r, int shift) {
        return (r >>> shift) | (r << (-shift));
    }

    private int FFmulX(int x) {
        return ((m2 & x) << 1) ^ (((m1 & x) >>> 7) * 27);
    }

    private int mcol(int x) {
        int f2 = FFmulX(x);
        return ((shift(x ^ f2, 8) ^ f2) ^ shift(x, 16)) ^ shift(x, 24);
    }

    private int inv_mcol(int x) {
        int f2 = FFmulX(x);
        int f4 = FFmulX(f2);
        int f8 = FFmulX(f4);
        int f9 = x ^ f8;
        return ((((f2 ^ f4) ^ f8) ^ shift(f2 ^ f9, 8)) ^ shift(f4 ^ f9, 16)) ^ shift(f9, 24);
    }

    private int subWord(int x) {
        return (S[x & 255] & 255) | ((S[(x >> 8) & 255] & 255) << 8) | ((S[(x >> 16) & 255] & 255) << 16) | (S[(x >> 24) & 255] << 24);
    }

    private int[][] generateWorkingKey(byte[] key, boolean forEncryption) {
        int KC = key.length / 4;
        if ((KC != 4 && KC != 6 && KC != 8) || KC * 4 != key.length) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        this.ROUNDS = KC + 6;
        int[][] W = (int[][]) Array.newInstance(Integer.TYPE, this.ROUNDS + 1, 4);
        int t = 0;
        int i = 0;
        while (i < key.length) {
            W[t >> 2][t & 3] = (key[i] & 255) | ((key[i + 1] & 255) << 8) | ((key[i + 2] & 255) << 16) | (key[i + 3] << 24);
            i += 4;
            t++;
        }
        int k = (this.ROUNDS + 1) << 2;
        for (int i2 = KC; i2 < k; i2++) {
            int temp = W[(i2 - 1) >> 2][(i2 - 1) & 3];
            if (i2 % KC == 0) {
                temp = subWord(shift(temp, 8)) ^ rcon[(i2 / KC) - 1];
            } else if (KC > 6 && i2 % KC == 4) {
                temp = subWord(temp);
            }
            W[i2 >> 2][i2 & 3] = W[(i2 - KC) >> 2][(i2 - KC) & 3] ^ temp;
        }
        if (!forEncryption) {
            for (int j = 1; j < this.ROUNDS; j++) {
                for (int i3 = 0; i3 < 4; i3++) {
                    W[j][i3] = inv_mcol(W[j][i3]);
                }
            }
        }
        return W;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.WorkingKey = generateWorkingKey(((KeyParameter) params).getKey(), forEncryption);
            this.forEncryption = forEncryption;
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "AES";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            unpackBlock(in, inOff);
            encryptBlock(this.WorkingKey);
            packBlock(out, outOff);
            return 16;
        }
        unpackBlock(in, inOff);
        decryptBlock(this.WorkingKey);
        packBlock(out, outOff);
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private void unpackBlock(byte[] bytes, int off) {
        int index = off + 1;
        this.C0 = bytes[off] & 255;
        int index2 = index + 1;
        this.C0 |= (bytes[index] & 255) << 8;
        int index3 = index2 + 1;
        this.C0 |= (bytes[index2] & 255) << 16;
        int index4 = index3 + 1;
        this.C0 |= bytes[index3] << 24;
        int index5 = index4 + 1;
        this.C1 = bytes[index4] & 255;
        int index6 = index5 + 1;
        this.C1 |= (bytes[index5] & 255) << 8;
        int index7 = index6 + 1;
        this.C1 |= (bytes[index6] & 255) << 16;
        int index8 = index7 + 1;
        this.C1 |= bytes[index7] << 24;
        int index9 = index8 + 1;
        this.C2 = bytes[index8] & 255;
        int index10 = index9 + 1;
        this.C2 |= (bytes[index9] & 255) << 8;
        int index11 = index10 + 1;
        this.C2 |= (bytes[index10] & 255) << 16;
        int index12 = index11 + 1;
        this.C2 |= bytes[index11] << 24;
        int index13 = index12 + 1;
        this.C3 = bytes[index12] & 255;
        int index14 = index13 + 1;
        this.C3 |= (bytes[index13] & 255) << 8;
        int index15 = index14 + 1;
        this.C3 |= (bytes[index14] & 255) << 16;
        int i = index15 + 1;
        this.C3 |= bytes[index15] << 24;
    }

    private void packBlock(byte[] bytes, int off) {
        int index = off + 1;
        bytes[off] = (byte) this.C0;
        int index2 = index + 1;
        bytes[index] = (byte) (this.C0 >> 8);
        int index3 = index2 + 1;
        bytes[index2] = (byte) (this.C0 >> 16);
        int index4 = index3 + 1;
        bytes[index3] = (byte) (this.C0 >> 24);
        int index5 = index4 + 1;
        bytes[index4] = (byte) this.C1;
        int index6 = index5 + 1;
        bytes[index5] = (byte) (this.C1 >> 8);
        int index7 = index6 + 1;
        bytes[index6] = (byte) (this.C1 >> 16);
        int index8 = index7 + 1;
        bytes[index7] = (byte) (this.C1 >> 24);
        int index9 = index8 + 1;
        bytes[index8] = (byte) this.C2;
        int index10 = index9 + 1;
        bytes[index9] = (byte) (this.C2 >> 8);
        int index11 = index10 + 1;
        bytes[index10] = (byte) (this.C2 >> 16);
        int index12 = index11 + 1;
        bytes[index11] = (byte) (this.C2 >> 24);
        int index13 = index12 + 1;
        bytes[index12] = (byte) this.C3;
        int index14 = index13 + 1;
        bytes[index13] = (byte) (this.C3 >> 8);
        int index15 = index14 + 1;
        bytes[index14] = (byte) (this.C3 >> 16);
        int i = index15 + 1;
        bytes[index15] = (byte) (this.C3 >> 24);
    }

    private void encryptBlock(int[][] KW) {
        this.C0 ^= KW[0][0];
        this.C1 ^= KW[0][1];
        this.C2 ^= KW[0][2];
        this.C3 ^= KW[0][3];
        int r = 1;
        while (r < this.ROUNDS - 1) {
            int r0 = mcol((((S[this.C0 & 255] & 255) ^ ((S[(this.C1 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C2 >> 16) & 255] & 255) << 16)) ^ (S[(this.C3 >> 24) & 255] << 24)) ^ KW[r][0];
            int r1 = mcol((((S[this.C1 & 255] & 255) ^ ((S[(this.C2 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C3 >> 16) & 255] & 255) << 16)) ^ (S[(this.C0 >> 24) & 255] << 24)) ^ KW[r][1];
            int r2 = mcol((((S[this.C2 & 255] & 255) ^ ((S[(this.C3 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C0 >> 16) & 255] & 255) << 16)) ^ (S[(this.C1 >> 24) & 255] << 24)) ^ KW[r][2];
            int r3 = r + 1;
            int r32 = mcol((((S[this.C3 & 255] & 255) ^ ((S[(this.C0 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C1 >> 16) & 255] & 255) << 16)) ^ (S[(this.C2 >> 24) & 255] << 24)) ^ KW[r][3];
            this.C0 = mcol((((S[r0 & 255] & 255) ^ ((S[(r1 >> 8) & 255] & 255) << 8)) ^ ((S[(r2 >> 16) & 255] & 255) << 16)) ^ (S[(r32 >> 24) & 255] << 24)) ^ KW[r3][0];
            this.C1 = mcol((((S[r1 & 255] & 255) ^ ((S[(r2 >> 8) & 255] & 255) << 8)) ^ ((S[(r32 >> 16) & 255] & 255) << 16)) ^ (S[(r0 >> 24) & 255] << 24)) ^ KW[r3][1];
            this.C2 = mcol((((S[r2 & 255] & 255) ^ ((S[(r32 >> 8) & 255] & 255) << 8)) ^ ((S[(r0 >> 16) & 255] & 255) << 16)) ^ (S[(r1 >> 24) & 255] << 24)) ^ KW[r3][2];
            r = r3 + 1;
            this.C3 = mcol((((S[r32 & 255] & 255) ^ ((S[(r0 >> 8) & 255] & 255) << 8)) ^ ((S[(r1 >> 16) & 255] & 255) << 16)) ^ (S[(r2 >> 24) & 255] << 24)) ^ KW[r3][3];
        }
        int r02 = mcol((((S[this.C0 & 255] & 255) ^ ((S[(this.C1 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C2 >> 16) & 255] & 255) << 16)) ^ (S[(this.C3 >> 24) & 255] << 24)) ^ KW[r][0];
        int r12 = mcol((((S[this.C1 & 255] & 255) ^ ((S[(this.C2 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C3 >> 16) & 255] & 255) << 16)) ^ (S[(this.C0 >> 24) & 255] << 24)) ^ KW[r][1];
        int r22 = mcol((((S[this.C2 & 255] & 255) ^ ((S[(this.C3 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C0 >> 16) & 255] & 255) << 16)) ^ (S[(this.C1 >> 24) & 255] << 24)) ^ KW[r][2];
        int r4 = r + 1;
        int r33 = mcol((((S[this.C3 & 255] & 255) ^ ((S[(this.C0 >> 8) & 255] & 255) << 8)) ^ ((S[(this.C1 >> 16) & 255] & 255) << 16)) ^ (S[(this.C2 >> 24) & 255] << 24)) ^ KW[r][3];
        this.C0 = ((((S[r02 & 255] & 255) ^ ((S[(r12 >> 8) & 255] & 255) << 8)) ^ ((S[(r22 >> 16) & 255] & 255) << 16)) ^ (S[(r33 >> 24) & 255] << 24)) ^ KW[r4][0];
        this.C1 = ((((S[r12 & 255] & 255) ^ ((S[(r22 >> 8) & 255] & 255) << 8)) ^ ((S[(r33 >> 16) & 255] & 255) << 16)) ^ (S[(r02 >> 24) & 255] << 24)) ^ KW[r4][1];
        this.C2 = ((((S[r22 & 255] & 255) ^ ((S[(r33 >> 8) & 255] & 255) << 8)) ^ ((S[(r02 >> 16) & 255] & 255) << 16)) ^ (S[(r12 >> 24) & 255] << 24)) ^ KW[r4][2];
        this.C3 = ((((S[r33 & 255] & 255) ^ ((S[(r02 >> 8) & 255] & 255) << 8)) ^ ((S[(r12 >> 16) & 255] & 255) << 16)) ^ (S[(r22 >> 24) & 255] << 24)) ^ KW[r4][3];
    }

    private void decryptBlock(int[][] KW) {
        this.C0 ^= KW[this.ROUNDS][0];
        this.C1 ^= KW[this.ROUNDS][1];
        this.C2 ^= KW[this.ROUNDS][2];
        this.C3 ^= KW[this.ROUNDS][3];
        int r = this.ROUNDS - 1;
        int r2 = r;
        while (r2 > 1) {
            int r0 = inv_mcol((((Si[this.C0 & 255] & 255) ^ ((Si[(this.C3 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C2 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C1 >> 24) & 255] << 24)) ^ KW[r2][0];
            int r1 = inv_mcol((((Si[this.C1 & 255] & 255) ^ ((Si[(this.C0 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C3 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C2 >> 24) & 255] << 24)) ^ KW[r2][1];
            int r22 = inv_mcol((((Si[this.C2 & 255] & 255) ^ ((Si[(this.C1 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C0 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C3 >> 24) & 255] << 24)) ^ KW[r2][2];
            int r3 = r2 - 1;
            int r32 = inv_mcol((((Si[this.C3 & 255] & 255) ^ ((Si[(this.C2 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C1 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C0 >> 24) & 255] << 24)) ^ KW[r2][3];
            this.C0 = inv_mcol((((Si[r0 & 255] & 255) ^ ((Si[(r32 >> 8) & 255] & 255) << 8)) ^ ((Si[(r22 >> 16) & 255] & 255) << 16)) ^ (Si[(r1 >> 24) & 255] << 24)) ^ KW[r3][0];
            this.C1 = inv_mcol((((Si[r1 & 255] & 255) ^ ((Si[(r0 >> 8) & 255] & 255) << 8)) ^ ((Si[(r32 >> 16) & 255] & 255) << 16)) ^ (Si[(r22 >> 24) & 255] << 24)) ^ KW[r3][1];
            this.C2 = inv_mcol((((Si[r22 & 255] & 255) ^ ((Si[(r1 >> 8) & 255] & 255) << 8)) ^ ((Si[(r0 >> 16) & 255] & 255) << 16)) ^ (Si[(r32 >> 24) & 255] << 24)) ^ KW[r3][2];
            r2 = r3 - 1;
            this.C3 = inv_mcol((((Si[r32 & 255] & 255) ^ ((Si[(r22 >> 8) & 255] & 255) << 8)) ^ ((Si[(r1 >> 16) & 255] & 255) << 16)) ^ (Si[(r0 >> 24) & 255] << 24)) ^ KW[r3][3];
        }
        int r02 = inv_mcol((((Si[this.C0 & 255] & 255) ^ ((Si[(this.C3 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C2 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C1 >> 24) & 255] << 24)) ^ KW[r2][0];
        int r12 = inv_mcol((((Si[this.C1 & 255] & 255) ^ ((Si[(this.C0 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C3 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C2 >> 24) & 255] << 24)) ^ KW[r2][1];
        int r23 = inv_mcol((((Si[this.C2 & 255] & 255) ^ ((Si[(this.C1 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C0 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C3 >> 24) & 255] << 24)) ^ KW[r2][2];
        int r33 = inv_mcol((((Si[this.C3 & 255] & 255) ^ ((Si[(this.C2 >> 8) & 255] & 255) << 8)) ^ ((Si[(this.C1 >> 16) & 255] & 255) << 16)) ^ (Si[(this.C0 >> 24) & 255] << 24)) ^ KW[r2][3];
        this.C0 = ((((Si[r02 & 255] & 255) ^ ((Si[(r33 >> 8) & 255] & 255) << 8)) ^ ((Si[(r23 >> 16) & 255] & 255) << 16)) ^ (Si[(r12 >> 24) & 255] << 24)) ^ KW[0][0];
        this.C1 = ((((Si[r12 & 255] & 255) ^ ((Si[(r02 >> 8) & 255] & 255) << 8)) ^ ((Si[(r33 >> 16) & 255] & 255) << 16)) ^ (Si[(r23 >> 24) & 255] << 24)) ^ KW[0][1];
        this.C2 = ((((Si[r23 & 255] & 255) ^ ((Si[(r12 >> 8) & 255] & 255) << 8)) ^ ((Si[(r02 >> 16) & 255] & 255) << 16)) ^ (Si[(r33 >> 24) & 255] << 24)) ^ KW[0][2];
        this.C3 = ((((Si[r33 & 255] & 255) ^ ((Si[(r23 >> 8) & 255] & 255) << 8)) ^ ((Si[(r12 >> 16) & 255] & 255) << 16)) ^ (Si[(r02 >> 24) & 255] << 24)) ^ KW[0][3];
    }
}