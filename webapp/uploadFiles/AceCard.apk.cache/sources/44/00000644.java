package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.params.KeyParameter;

/* loaded from: classes.dex */
public class SerpentEngine implements BlockCipher {
    private static final int BLOCK_SIZE = 16;
    static final int PHI = -1640531527;
    static final int ROUNDS = 32;
    private int X0;
    private int X1;
    private int X2;
    private int X3;
    private boolean encrypting;
    private int[] wKey;

    @Override // org.spongycastle.crypto.BlockCipher
    public void init(boolean encrypting, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.encrypting = encrypting;
            this.wKey = makeWorkingKey(((KeyParameter) params).getKey());
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to Serpent init - " + params.getClass().getName());
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return "Serpent";
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public final int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.wKey == null) {
            throw new IllegalStateException("Serpent not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        if (this.encrypting) {
            encryptBlock(in, inOff, out, outOff);
            return 16;
        }
        decryptBlock(in, inOff, out, outOff);
        return 16;
    }

    @Override // org.spongycastle.crypto.BlockCipher
    public void reset() {
    }

    private int[] makeWorkingKey(byte[] key) throws IllegalArgumentException {
        int[] kPad = new int[16];
        int off = key.length - 4;
        int length = 0;
        while (off > 0) {
            kPad[length] = bytesToWord(key, off);
            off -= 4;
            length++;
        }
        if (off == 0) {
            int length2 = length + 1;
            kPad[length] = bytesToWord(key, 0);
            if (length2 < 8) {
                kPad[length2] = 1;
            }
            int[] w = new int[132];
            for (int i = 8; i < 16; i++) {
                kPad[i] = rotateLeft(((((kPad[i - 8] ^ kPad[i - 5]) ^ kPad[i - 3]) ^ kPad[i - 1]) ^ PHI) ^ (i - 8), 11);
            }
            System.arraycopy(kPad, 8, w, 0, 8);
            for (int i2 = 8; i2 < 132; i2++) {
                w[i2] = rotateLeft(((((w[i2 - 8] ^ w[i2 - 5]) ^ w[i2 - 3]) ^ w[i2 - 1]) ^ PHI) ^ i2, 11);
            }
            sb3(w[0], w[1], w[2], w[3]);
            w[0] = this.X0;
            w[1] = this.X1;
            w[2] = this.X2;
            w[3] = this.X3;
            sb2(w[4], w[5], w[6], w[7]);
            w[4] = this.X0;
            w[5] = this.X1;
            w[6] = this.X2;
            w[7] = this.X3;
            sb1(w[8], w[9], w[10], w[11]);
            w[8] = this.X0;
            w[9] = this.X1;
            w[10] = this.X2;
            w[11] = this.X3;
            sb0(w[12], w[13], w[14], w[15]);
            w[12] = this.X0;
            w[13] = this.X1;
            w[14] = this.X2;
            w[15] = this.X3;
            sb7(w[16], w[17], w[18], w[19]);
            w[16] = this.X0;
            w[17] = this.X1;
            w[18] = this.X2;
            w[19] = this.X3;
            sb6(w[20], w[21], w[22], w[23]);
            w[20] = this.X0;
            w[21] = this.X1;
            w[22] = this.X2;
            w[23] = this.X3;
            sb5(w[24], w[25], w[26], w[27]);
            w[24] = this.X0;
            w[25] = this.X1;
            w[26] = this.X2;
            w[27] = this.X3;
            sb4(w[28], w[29], w[30], w[31]);
            w[28] = this.X0;
            w[29] = this.X1;
            w[30] = this.X2;
            w[31] = this.X3;
            sb3(w[32], w[33], w[34], w[35]);
            w[32] = this.X0;
            w[33] = this.X1;
            w[34] = this.X2;
            w[35] = this.X3;
            sb2(w[36], w[37], w[38], w[39]);
            w[36] = this.X0;
            w[37] = this.X1;
            w[38] = this.X2;
            w[39] = this.X3;
            sb1(w[40], w[41], w[42], w[43]);
            w[40] = this.X0;
            w[41] = this.X1;
            w[42] = this.X2;
            w[43] = this.X3;
            sb0(w[44], w[45], w[46], w[47]);
            w[44] = this.X0;
            w[45] = this.X1;
            w[46] = this.X2;
            w[47] = this.X3;
            sb7(w[48], w[49], w[50], w[51]);
            w[48] = this.X0;
            w[49] = this.X1;
            w[50] = this.X2;
            w[51] = this.X3;
            sb6(w[52], w[53], w[54], w[55]);
            w[52] = this.X0;
            w[53] = this.X1;
            w[54] = this.X2;
            w[55] = this.X3;
            sb5(w[56], w[57], w[58], w[59]);
            w[56] = this.X0;
            w[57] = this.X1;
            w[58] = this.X2;
            w[59] = this.X3;
            sb4(w[60], w[61], w[62], w[63]);
            w[60] = this.X0;
            w[61] = this.X1;
            w[62] = this.X2;
            w[63] = this.X3;
            sb3(w[64], w[65], w[66], w[67]);
            w[64] = this.X0;
            w[65] = this.X1;
            w[66] = this.X2;
            w[67] = this.X3;
            sb2(w[68], w[69], w[70], w[71]);
            w[68] = this.X0;
            w[69] = this.X1;
            w[70] = this.X2;
            w[71] = this.X3;
            sb1(w[72], w[73], w[74], w[75]);
            w[72] = this.X0;
            w[73] = this.X1;
            w[74] = this.X2;
            w[75] = this.X3;
            sb0(w[76], w[77], w[78], w[79]);
            w[76] = this.X0;
            w[77] = this.X1;
            w[78] = this.X2;
            w[79] = this.X3;
            sb7(w[80], w[81], w[82], w[83]);
            w[80] = this.X0;
            w[81] = this.X1;
            w[82] = this.X2;
            w[83] = this.X3;
            sb6(w[84], w[85], w[86], w[87]);
            w[84] = this.X0;
            w[85] = this.X1;
            w[86] = this.X2;
            w[87] = this.X3;
            sb5(w[88], w[89], w[90], w[91]);
            w[88] = this.X0;
            w[89] = this.X1;
            w[90] = this.X2;
            w[91] = this.X3;
            sb4(w[92], w[93], w[94], w[95]);
            w[92] = this.X0;
            w[93] = this.X1;
            w[94] = this.X2;
            w[95] = this.X3;
            sb3(w[96], w[97], w[98], w[99]);
            w[96] = this.X0;
            w[97] = this.X1;
            w[98] = this.X2;
            w[99] = this.X3;
            sb2(w[100], w[101], w[102], w[103]);
            w[100] = this.X0;
            w[101] = this.X1;
            w[102] = this.X2;
            w[103] = this.X3;
            sb1(w[104], w[105], w[106], w[107]);
            w[104] = this.X0;
            w[105] = this.X1;
            w[106] = this.X2;
            w[107] = this.X3;
            sb0(w[108], w[109], w[110], w[111]);
            w[108] = this.X0;
            w[109] = this.X1;
            w[110] = this.X2;
            w[111] = this.X3;
            sb7(w[112], w[113], w[114], w[115]);
            w[112] = this.X0;
            w[113] = this.X1;
            w[114] = this.X2;
            w[115] = this.X3;
            sb6(w[116], w[117], w[118], w[119]);
            w[116] = this.X0;
            w[117] = this.X1;
            w[118] = this.X2;
            w[119] = this.X3;
            sb5(w[120], w[121], w[122], w[123]);
            w[120] = this.X0;
            w[121] = this.X1;
            w[122] = this.X2;
            w[123] = this.X3;
            sb4(w[124], w[125], w[126], w[127]);
            w[124] = this.X0;
            w[125] = this.X1;
            w[126] = this.X2;
            w[127] = this.X3;
            sb3(w[128], w[129], w[130], w[131]);
            w[128] = this.X0;
            w[129] = this.X1;
            w[130] = this.X2;
            w[131] = this.X3;
            return w;
        }
        throw new IllegalArgumentException("key must be a multiple of 4 bytes");
    }

    private int rotateLeft(int x, int bits) {
        return (x << bits) | (x >>> (-bits));
    }

    private int rotateRight(int x, int bits) {
        return (x >>> bits) | (x << (-bits));
    }

    private int bytesToWord(byte[] src, int srcOff) {
        return ((src[srcOff] & 255) << 24) | ((src[srcOff + 1] & 255) << 16) | ((src[srcOff + 2] & 255) << 8) | (src[srcOff + 3] & 255);
    }

    private void wordToBytes(int word, byte[] dst, int dstOff) {
        dst[dstOff + 3] = (byte) word;
        dst[dstOff + 2] = (byte) (word >>> 8);
        dst[dstOff + 1] = (byte) (word >>> 16);
        dst[dstOff] = (byte) (word >>> 24);
    }

    private void encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        this.X3 = bytesToWord(in, inOff);
        this.X2 = bytesToWord(in, inOff + 4);
        this.X1 = bytesToWord(in, inOff + 8);
        this.X0 = bytesToWord(in, inOff + 12);
        sb0(this.wKey[0] ^ this.X0, this.wKey[1] ^ this.X1, this.wKey[2] ^ this.X2, this.wKey[3] ^ this.X3);
        LT();
        sb1(this.wKey[4] ^ this.X0, this.wKey[5] ^ this.X1, this.wKey[6] ^ this.X2, this.wKey[7] ^ this.X3);
        LT();
        sb2(this.wKey[8] ^ this.X0, this.wKey[9] ^ this.X1, this.wKey[10] ^ this.X2, this.wKey[11] ^ this.X3);
        LT();
        sb3(this.wKey[12] ^ this.X0, this.wKey[13] ^ this.X1, this.wKey[14] ^ this.X2, this.wKey[15] ^ this.X3);
        LT();
        sb4(this.wKey[16] ^ this.X0, this.wKey[17] ^ this.X1, this.wKey[18] ^ this.X2, this.wKey[19] ^ this.X3);
        LT();
        sb5(this.wKey[20] ^ this.X0, this.wKey[21] ^ this.X1, this.wKey[22] ^ this.X2, this.wKey[23] ^ this.X3);
        LT();
        sb6(this.wKey[24] ^ this.X0, this.wKey[25] ^ this.X1, this.wKey[26] ^ this.X2, this.wKey[27] ^ this.X3);
        LT();
        sb7(this.wKey[28] ^ this.X0, this.wKey[29] ^ this.X1, this.wKey[30] ^ this.X2, this.wKey[31] ^ this.X3);
        LT();
        sb0(this.wKey[32] ^ this.X0, this.wKey[33] ^ this.X1, this.wKey[34] ^ this.X2, this.wKey[35] ^ this.X3);
        LT();
        sb1(this.wKey[36] ^ this.X0, this.wKey[37] ^ this.X1, this.wKey[38] ^ this.X2, this.wKey[39] ^ this.X3);
        LT();
        sb2(this.wKey[40] ^ this.X0, this.wKey[41] ^ this.X1, this.wKey[42] ^ this.X2, this.wKey[43] ^ this.X3);
        LT();
        sb3(this.wKey[44] ^ this.X0, this.wKey[45] ^ this.X1, this.wKey[46] ^ this.X2, this.wKey[47] ^ this.X3);
        LT();
        sb4(this.wKey[48] ^ this.X0, this.wKey[49] ^ this.X1, this.wKey[50] ^ this.X2, this.wKey[51] ^ this.X3);
        LT();
        sb5(this.wKey[52] ^ this.X0, this.wKey[53] ^ this.X1, this.wKey[54] ^ this.X2, this.wKey[55] ^ this.X3);
        LT();
        sb6(this.wKey[56] ^ this.X0, this.wKey[57] ^ this.X1, this.wKey[58] ^ this.X2, this.wKey[59] ^ this.X3);
        LT();
        sb7(this.wKey[60] ^ this.X0, this.wKey[61] ^ this.X1, this.wKey[62] ^ this.X2, this.wKey[63] ^ this.X3);
        LT();
        sb0(this.wKey[64] ^ this.X0, this.wKey[65] ^ this.X1, this.wKey[66] ^ this.X2, this.wKey[67] ^ this.X3);
        LT();
        sb1(this.wKey[68] ^ this.X0, this.wKey[69] ^ this.X1, this.wKey[70] ^ this.X2, this.wKey[71] ^ this.X3);
        LT();
        sb2(this.wKey[72] ^ this.X0, this.wKey[73] ^ this.X1, this.wKey[74] ^ this.X2, this.wKey[75] ^ this.X3);
        LT();
        sb3(this.wKey[76] ^ this.X0, this.wKey[77] ^ this.X1, this.wKey[78] ^ this.X2, this.wKey[79] ^ this.X3);
        LT();
        sb4(this.wKey[80] ^ this.X0, this.wKey[81] ^ this.X1, this.wKey[82] ^ this.X2, this.wKey[83] ^ this.X3);
        LT();
        sb5(this.wKey[84] ^ this.X0, this.wKey[85] ^ this.X1, this.wKey[86] ^ this.X2, this.wKey[87] ^ this.X3);
        LT();
        sb6(this.wKey[88] ^ this.X0, this.wKey[89] ^ this.X1, this.wKey[90] ^ this.X2, this.wKey[91] ^ this.X3);
        LT();
        sb7(this.wKey[92] ^ this.X0, this.wKey[93] ^ this.X1, this.wKey[94] ^ this.X2, this.wKey[95] ^ this.X3);
        LT();
        sb0(this.wKey[96] ^ this.X0, this.wKey[97] ^ this.X1, this.wKey[98] ^ this.X2, this.wKey[99] ^ this.X3);
        LT();
        sb1(this.wKey[100] ^ this.X0, this.wKey[101] ^ this.X1, this.wKey[102] ^ this.X2, this.wKey[103] ^ this.X3);
        LT();
        sb2(this.wKey[104] ^ this.X0, this.wKey[105] ^ this.X1, this.wKey[106] ^ this.X2, this.wKey[107] ^ this.X3);
        LT();
        sb3(this.wKey[108] ^ this.X0, this.wKey[109] ^ this.X1, this.wKey[110] ^ this.X2, this.wKey[111] ^ this.X3);
        LT();
        sb4(this.wKey[112] ^ this.X0, this.wKey[113] ^ this.X1, this.wKey[114] ^ this.X2, this.wKey[115] ^ this.X3);
        LT();
        sb5(this.wKey[116] ^ this.X0, this.wKey[117] ^ this.X1, this.wKey[118] ^ this.X2, this.wKey[119] ^ this.X3);
        LT();
        sb6(this.wKey[120] ^ this.X0, this.wKey[121] ^ this.X1, this.wKey[122] ^ this.X2, this.wKey[123] ^ this.X3);
        LT();
        sb7(this.wKey[124] ^ this.X0, this.wKey[125] ^ this.X1, this.wKey[126] ^ this.X2, this.wKey[127] ^ this.X3);
        wordToBytes(this.wKey[131] ^ this.X3, out, outOff);
        wordToBytes(this.wKey[130] ^ this.X2, out, outOff + 4);
        wordToBytes(this.wKey[129] ^ this.X1, out, outOff + 8);
        wordToBytes(this.wKey[128] ^ this.X0, out, outOff + 12);
    }

    private void decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        this.X3 = this.wKey[131] ^ bytesToWord(in, inOff);
        this.X2 = this.wKey[130] ^ bytesToWord(in, inOff + 4);
        this.X1 = this.wKey[129] ^ bytesToWord(in, inOff + 8);
        this.X0 = this.wKey[128] ^ bytesToWord(in, inOff + 12);
        ib7(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[124];
        this.X1 ^= this.wKey[125];
        this.X2 ^= this.wKey[126];
        this.X3 ^= this.wKey[127];
        inverseLT();
        ib6(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[120];
        this.X1 ^= this.wKey[121];
        this.X2 ^= this.wKey[122];
        this.X3 ^= this.wKey[123];
        inverseLT();
        ib5(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[116];
        this.X1 ^= this.wKey[117];
        this.X2 ^= this.wKey[118];
        this.X3 ^= this.wKey[119];
        inverseLT();
        ib4(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[112];
        this.X1 ^= this.wKey[113];
        this.X2 ^= this.wKey[114];
        this.X3 ^= this.wKey[115];
        inverseLT();
        ib3(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[108];
        this.X1 ^= this.wKey[109];
        this.X2 ^= this.wKey[110];
        this.X3 ^= this.wKey[111];
        inverseLT();
        ib2(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[104];
        this.X1 ^= this.wKey[105];
        this.X2 ^= this.wKey[106];
        this.X3 ^= this.wKey[107];
        inverseLT();
        ib1(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[100];
        this.X1 ^= this.wKey[101];
        this.X2 ^= this.wKey[102];
        this.X3 ^= this.wKey[103];
        inverseLT();
        ib0(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[96];
        this.X1 ^= this.wKey[97];
        this.X2 ^= this.wKey[98];
        this.X3 ^= this.wKey[99];
        inverseLT();
        ib7(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[92];
        this.X1 ^= this.wKey[93];
        this.X2 ^= this.wKey[94];
        this.X3 ^= this.wKey[95];
        inverseLT();
        ib6(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[88];
        this.X1 ^= this.wKey[89];
        this.X2 ^= this.wKey[90];
        this.X3 ^= this.wKey[91];
        inverseLT();
        ib5(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[84];
        this.X1 ^= this.wKey[85];
        this.X2 ^= this.wKey[86];
        this.X3 ^= this.wKey[87];
        inverseLT();
        ib4(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[80];
        this.X1 ^= this.wKey[81];
        this.X2 ^= this.wKey[82];
        this.X3 ^= this.wKey[83];
        inverseLT();
        ib3(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[76];
        this.X1 ^= this.wKey[77];
        this.X2 ^= this.wKey[78];
        this.X3 ^= this.wKey[79];
        inverseLT();
        ib2(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[72];
        this.X1 ^= this.wKey[73];
        this.X2 ^= this.wKey[74];
        this.X3 ^= this.wKey[75];
        inverseLT();
        ib1(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[68];
        this.X1 ^= this.wKey[69];
        this.X2 ^= this.wKey[70];
        this.X3 ^= this.wKey[71];
        inverseLT();
        ib0(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[64];
        this.X1 ^= this.wKey[65];
        this.X2 ^= this.wKey[66];
        this.X3 ^= this.wKey[67];
        inverseLT();
        ib7(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[60];
        this.X1 ^= this.wKey[61];
        this.X2 ^= this.wKey[62];
        this.X3 ^= this.wKey[63];
        inverseLT();
        ib6(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[56];
        this.X1 ^= this.wKey[57];
        this.X2 ^= this.wKey[58];
        this.X3 ^= this.wKey[59];
        inverseLT();
        ib5(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[52];
        this.X1 ^= this.wKey[53];
        this.X2 ^= this.wKey[54];
        this.X3 ^= this.wKey[55];
        inverseLT();
        ib4(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[48];
        this.X1 ^= this.wKey[49];
        this.X2 ^= this.wKey[50];
        this.X3 ^= this.wKey[51];
        inverseLT();
        ib3(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[44];
        this.X1 ^= this.wKey[45];
        this.X2 ^= this.wKey[46];
        this.X3 ^= this.wKey[47];
        inverseLT();
        ib2(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[40];
        this.X1 ^= this.wKey[41];
        this.X2 ^= this.wKey[42];
        this.X3 ^= this.wKey[43];
        inverseLT();
        ib1(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[36];
        this.X1 ^= this.wKey[37];
        this.X2 ^= this.wKey[38];
        this.X3 ^= this.wKey[39];
        inverseLT();
        ib0(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[32];
        this.X1 ^= this.wKey[33];
        this.X2 ^= this.wKey[34];
        this.X3 ^= this.wKey[35];
        inverseLT();
        ib7(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[28];
        this.X1 ^= this.wKey[29];
        this.X2 ^= this.wKey[30];
        this.X3 ^= this.wKey[31];
        inverseLT();
        ib6(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[24];
        this.X1 ^= this.wKey[25];
        this.X2 ^= this.wKey[26];
        this.X3 ^= this.wKey[27];
        inverseLT();
        ib5(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[20];
        this.X1 ^= this.wKey[21];
        this.X2 ^= this.wKey[22];
        this.X3 ^= this.wKey[23];
        inverseLT();
        ib4(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[16];
        this.X1 ^= this.wKey[17];
        this.X2 ^= this.wKey[18];
        this.X3 ^= this.wKey[19];
        inverseLT();
        ib3(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[12];
        this.X1 ^= this.wKey[13];
        this.X2 ^= this.wKey[14];
        this.X3 ^= this.wKey[15];
        inverseLT();
        ib2(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[8];
        this.X1 ^= this.wKey[9];
        this.X2 ^= this.wKey[10];
        this.X3 ^= this.wKey[11];
        inverseLT();
        ib1(this.X0, this.X1, this.X2, this.X3);
        this.X0 ^= this.wKey[4];
        this.X1 ^= this.wKey[5];
        this.X2 ^= this.wKey[6];
        this.X3 ^= this.wKey[7];
        inverseLT();
        ib0(this.X0, this.X1, this.X2, this.X3);
        wordToBytes(this.X3 ^ this.wKey[3], out, outOff);
        wordToBytes(this.X2 ^ this.wKey[2], out, outOff + 4);
        wordToBytes(this.X1 ^ this.wKey[1], out, outOff + 8);
        wordToBytes(this.X0 ^ this.wKey[0], out, outOff + 12);
    }

    private void sb0(int a, int b, int c, int d) {
        int t1 = a ^ d;
        int t3 = c ^ t1;
        int t4 = b ^ t3;
        this.X3 = (a & d) ^ t4;
        int t7 = a ^ (b & t1);
        this.X2 = (c | t7) ^ t4;
        int t12 = this.X3 & (t3 ^ t7);
        this.X1 = (t3 ^ (-1)) ^ t12;
        this.X0 = (t7 ^ (-1)) ^ t12;
    }

    private void ib0(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ b;
        int t4 = d ^ (t1 | t2);
        int t5 = c ^ t4;
        this.X2 = t2 ^ t5;
        int t8 = t1 ^ (d & t2);
        this.X1 = (this.X2 & t8) ^ t4;
        this.X3 = (a & t4) ^ (this.X1 | t5);
        this.X0 = this.X3 ^ (t5 ^ t8);
    }

    private void sb1(int a, int b, int c, int d) {
        int t2 = b ^ (a ^ (-1));
        int t5 = c ^ (a | t2);
        this.X2 = d ^ t5;
        int t7 = b ^ (d | t2);
        int t8 = t2 ^ this.X2;
        this.X3 = (t5 & t7) ^ t8;
        int t11 = t5 ^ t7;
        this.X1 = this.X3 ^ t11;
        this.X0 = (t8 & t11) ^ t5;
    }

    private void ib1(int a, int b, int c, int d) {
        int t1 = b ^ d;
        int t3 = a ^ (b & t1);
        int t4 = t1 ^ t3;
        this.X3 = c ^ t4;
        int t7 = b ^ (t1 & t3);
        int t8 = this.X3 | t7;
        this.X1 = t3 ^ t8;
        int t10 = this.X1 ^ (-1);
        int t11 = this.X3 ^ t7;
        this.X0 = t10 ^ t11;
        this.X2 = (t10 | t11) ^ t4;
    }

    private void sb2(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = b ^ d;
        int t3 = c & t1;
        this.X0 = t2 ^ t3;
        int t5 = c ^ t1;
        int t6 = c ^ this.X0;
        int t7 = b & t6;
        this.X3 = t5 ^ t7;
        this.X2 = ((d | t7) & (this.X0 | t5)) ^ a;
        this.X1 = (this.X3 ^ t2) ^ (this.X2 ^ (d | t1));
    }

    private void ib2(int a, int b, int c, int d) {
        int t1 = b ^ d;
        int t2 = t1 ^ (-1);
        int t3 = a ^ c;
        int t4 = c ^ t1;
        int t5 = b & t4;
        this.X0 = t3 ^ t5;
        int t7 = a | t2;
        int t8 = d ^ t7;
        int t9 = t3 | t8;
        this.X3 = t1 ^ t9;
        int t11 = t4 ^ (-1);
        int t12 = this.X0 | this.X3;
        this.X1 = t11 ^ t12;
        this.X2 = (d & t11) ^ (t3 ^ t12);
    }

    private void sb3(int a, int b, int c, int d) {
        int t1 = a ^ b;
        int t2 = a & c;
        int t3 = a | d;
        int t4 = c ^ d;
        int t5 = t1 & t3;
        int t6 = t2 | t5;
        this.X2 = t4 ^ t6;
        int t8 = b ^ t3;
        int t9 = t6 ^ t8;
        int t10 = t4 & t9;
        this.X0 = t1 ^ t10;
        int t12 = this.X2 & this.X0;
        this.X1 = t9 ^ t12;
        this.X3 = (b | d) ^ (t4 ^ t12);
    }

    private void ib3(int a, int b, int c, int d) {
        int t1 = a | b;
        int t2 = b ^ c;
        int t3 = b & t2;
        int t4 = a ^ t3;
        int t5 = c ^ t4;
        int t6 = d | t4;
        this.X0 = t2 ^ t6;
        int t8 = t2 | t6;
        int t9 = d ^ t8;
        this.X2 = t5 ^ t9;
        int t11 = t1 ^ t9;
        int t12 = this.X0 & t11;
        this.X3 = t4 ^ t12;
        this.X1 = this.X3 ^ (this.X0 ^ t11);
    }

    private void sb4(int a, int b, int c, int d) {
        int t1 = a ^ d;
        int t2 = d & t1;
        int t3 = c ^ t2;
        int t4 = b | t3;
        this.X3 = t1 ^ t4;
        int t6 = b ^ (-1);
        int t7 = t1 | t6;
        this.X0 = t3 ^ t7;
        int t9 = a & this.X0;
        int t10 = t1 ^ t6;
        int t11 = t4 & t10;
        this.X2 = t9 ^ t11;
        this.X1 = (a ^ t3) ^ (this.X2 & t10);
    }

    private void ib4(int a, int b, int c, int d) {
        int t1 = c | d;
        int t2 = a & t1;
        int t3 = b ^ t2;
        int t4 = a & t3;
        int t5 = c ^ t4;
        this.X1 = d ^ t5;
        int t7 = a ^ (-1);
        int t8 = t5 & this.X1;
        this.X3 = t3 ^ t8;
        int t10 = this.X1 | t7;
        int t11 = d ^ t10;
        this.X0 = this.X3 ^ t11;
        this.X2 = (t3 & t11) ^ (this.X1 ^ t7);
    }

    private void sb5(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ b;
        int t3 = a ^ d;
        int t4 = c ^ t1;
        int t5 = t2 | t3;
        this.X0 = t4 ^ t5;
        int t7 = d & this.X0;
        int t8 = t2 ^ this.X0;
        this.X1 = t7 ^ t8;
        int t10 = t1 | this.X0;
        int t11 = t2 | t7;
        int t12 = t3 ^ t10;
        this.X2 = t11 ^ t12;
        this.X3 = (b ^ t7) ^ (this.X1 & t12);
    }

    private void ib5(int a, int b, int c, int d) {
        int t1 = c ^ (-1);
        int t2 = b & t1;
        int t3 = d ^ t2;
        int t4 = a & t3;
        int t5 = b ^ t1;
        this.X3 = t4 ^ t5;
        int t7 = b | this.X3;
        int t8 = a & t7;
        this.X1 = t3 ^ t8;
        int t10 = a | d;
        int t11 = t1 ^ t7;
        this.X0 = t10 ^ t11;
        this.X2 = (b & t10) ^ ((a ^ c) | t4);
    }

    private void sb6(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ d;
        int t3 = b ^ t2;
        int t4 = t1 | t2;
        int t5 = c ^ t4;
        this.X1 = b ^ t5;
        int t7 = t2 | this.X1;
        int t8 = d ^ t7;
        int t9 = t5 & t8;
        this.X2 = t3 ^ t9;
        int t11 = t5 ^ t8;
        this.X0 = this.X2 ^ t11;
        this.X3 = (t5 ^ (-1)) ^ (t3 & t11);
    }

    private void ib6(int a, int b, int c, int d) {
        int t1 = a ^ (-1);
        int t2 = a ^ b;
        int t3 = c ^ t2;
        int t4 = c | t1;
        int t5 = d ^ t4;
        this.X1 = t3 ^ t5;
        int t7 = t3 & t5;
        int t8 = t2 ^ t7;
        int t9 = b | t8;
        this.X3 = t5 ^ t9;
        int t11 = b | this.X3;
        this.X0 = t8 ^ t11;
        this.X2 = (d & t1) ^ (t3 ^ t11);
    }

    private void sb7(int a, int b, int c, int d) {
        int t1 = b ^ c;
        int t2 = c & t1;
        int t3 = d ^ t2;
        int t4 = a ^ t3;
        int t5 = d | t1;
        int t6 = t4 & t5;
        this.X1 = b ^ t6;
        int t8 = t3 | this.X1;
        int t9 = a & t4;
        this.X3 = t1 ^ t9;
        int t11 = t4 ^ t8;
        int t12 = this.X3 & t11;
        this.X2 = t3 ^ t12;
        this.X0 = (t11 ^ (-1)) ^ (this.X3 & this.X2);
    }

    private void ib7(int a, int b, int c, int d) {
        int t3 = c | (a & b);
        int t4 = d & (a | b);
        this.X3 = t3 ^ t4;
        int t6 = d ^ (-1);
        int t7 = b ^ t4;
        int t9 = t7 | (this.X3 ^ t6);
        this.X1 = a ^ t9;
        this.X0 = (c ^ t7) ^ (this.X1 | d);
        this.X2 = (this.X1 ^ t3) ^ (this.X0 ^ (this.X3 & a));
    }

    private void LT() {
        int x0 = rotateLeft(this.X0, 13);
        int x2 = rotateLeft(this.X2, 3);
        int x1 = (this.X1 ^ x0) ^ x2;
        int x3 = (this.X3 ^ x2) ^ (x0 << 3);
        this.X1 = rotateLeft(x1, 1);
        this.X3 = rotateLeft(x3, 7);
        this.X0 = rotateLeft((this.X1 ^ x0) ^ this.X3, 5);
        this.X2 = rotateLeft((this.X3 ^ x2) ^ (this.X1 << 7), 22);
    }

    private void inverseLT() {
        int x2 = (rotateRight(this.X2, 22) ^ this.X3) ^ (this.X1 << 7);
        int x0 = (rotateRight(this.X0, 5) ^ this.X1) ^ this.X3;
        int x3 = rotateRight(this.X3, 7);
        int x1 = rotateRight(this.X1, 1);
        this.X3 = (x3 ^ x2) ^ (x0 << 3);
        this.X1 = (x1 ^ x0) ^ x2;
        this.X2 = rotateRight(x2, 3);
        this.X0 = rotateRight(x0, 13);
    }
}