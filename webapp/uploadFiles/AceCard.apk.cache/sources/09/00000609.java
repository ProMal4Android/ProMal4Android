package org.spongycastle.crypto.digests;

/* loaded from: classes.dex */
public class RIPEMD160Digest extends GeneralDigest {
    private static final int DIGEST_LENGTH = 20;
    private int H0;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int[] X;
    private int xOff;

    public RIPEMD160Digest() {
        this.X = new int[16];
        reset();
    }

    public RIPEMD160Digest(RIPEMD160Digest t) {
        super(t);
        this.X = new int[16];
        this.H0 = t.H0;
        this.H1 = t.H1;
        this.H2 = t.H2;
        this.H3 = t.H3;
        this.H4 = t.H4;
        System.arraycopy(t.X, 0, this.X, 0, t.X.length);
        this.xOff = t.xOff;
    }

    @Override // org.spongycastle.crypto.Digest
    public String getAlgorithmName() {
        return "RIPEMD160";
    }

    @Override // org.spongycastle.crypto.Digest
    public int getDigestSize() {
        return 20;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processWord(byte[] in, int inOff) {
        int[] iArr = this.X;
        int i = this.xOff;
        this.xOff = i + 1;
        iArr[i] = (in[inOff] & 255) | ((in[inOff + 1] & 255) << 8) | ((in[inOff + 2] & 255) << 16) | ((in[inOff + 3] & 255) << 24);
        if (this.xOff == 16) {
            processBlock();
        }
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = (int) ((-1) & bitLength);
        this.X[15] = (int) (bitLength >>> 32);
    }

    private void unpackWord(int word, byte[] out, int outOff) {
        out[outOff] = (byte) word;
        out[outOff + 1] = (byte) (word >>> 8);
        out[outOff + 2] = (byte) (word >>> 16);
        out[outOff + 3] = (byte) (word >>> 24);
    }

    @Override // org.spongycastle.crypto.Digest
    public int doFinal(byte[] out, int outOff) {
        finish();
        unpackWord(this.H0, out, outOff);
        unpackWord(this.H1, out, outOff + 4);
        unpackWord(this.H2, out, outOff + 8);
        unpackWord(this.H3, out, outOff + 12);
        unpackWord(this.H4, out, outOff + 16);
        reset();
        return 20;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest, org.spongycastle.crypto.Digest
    public void reset() {
        super.reset();
        this.H0 = 1732584193;
        this.H1 = -271733879;
        this.H2 = -1732584194;
        this.H3 = 271733878;
        this.H4 = -1009589776;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    private int RL(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private int f1(int x, int y, int z) {
        return (x ^ y) ^ z;
    }

    private int f2(int x, int y, int z) {
        return (x & y) | ((x ^ (-1)) & z);
    }

    private int f3(int x, int y, int z) {
        return ((y ^ (-1)) | x) ^ z;
    }

    private int f4(int x, int y, int z) {
        return (x & z) | ((z ^ (-1)) & y);
    }

    private int f5(int x, int y, int z) {
        return ((z ^ (-1)) | y) ^ x;
    }

    @Override // org.spongycastle.crypto.digests.GeneralDigest
    protected void processBlock() {
        int aa = this.H0;
        int bb = this.H1;
        int cc = this.H2;
        int dd = this.H3;
        int ee = this.H4;
        int a = RL(f1(bb, cc, dd) + aa + this.X[0], 11) + ee;
        int c = RL(cc, 10);
        int e = RL(f1(a, bb, c) + ee + this.X[1], 14) + dd;
        int b = RL(bb, 10);
        int d = RL(f1(e, a, b) + dd + this.X[2], 15) + c;
        int a2 = RL(a, 10);
        int c2 = RL(f1(d, e, a2) + c + this.X[3], 12) + b;
        int e2 = RL(e, 10);
        int b2 = RL(f1(c2, d, e2) + b + this.X[4], 5) + a2;
        int d2 = RL(d, 10);
        int a3 = RL(f1(b2, c2, d2) + a2 + this.X[5], 8) + e2;
        int c3 = RL(c2, 10);
        int e3 = RL(f1(a3, b2, c3) + e2 + this.X[6], 7) + d2;
        int b3 = RL(b2, 10);
        int d3 = RL(f1(e3, a3, b3) + d2 + this.X[7], 9) + c3;
        int a4 = RL(a3, 10);
        int c4 = RL(f1(d3, e3, a4) + c3 + this.X[8], 11) + b3;
        int e4 = RL(e3, 10);
        int b4 = RL(f1(c4, d3, e4) + b3 + this.X[9], 13) + a4;
        int d4 = RL(d3, 10);
        int a5 = RL(f1(b4, c4, d4) + a4 + this.X[10], 14) + e4;
        int c5 = RL(c4, 10);
        int e5 = RL(f1(a5, b4, c5) + e4 + this.X[11], 15) + d4;
        int b5 = RL(b4, 10);
        int d5 = RL(f1(e5, a5, b5) + d4 + this.X[12], 6) + c5;
        int a6 = RL(a5, 10);
        int c6 = RL(f1(d5, e5, a6) + c5 + this.X[13], 7) + b5;
        int e6 = RL(e5, 10);
        int b6 = RL(f1(c6, d5, e6) + b5 + this.X[14], 9) + a6;
        int d6 = RL(d5, 10);
        int a7 = RL(f1(b6, c6, d6) + a6 + this.X[15], 8) + e6;
        int c7 = RL(c6, 10);
        int aa2 = RL(f5(bb, cc, dd) + aa + this.X[5] + 1352829926, 8) + ee;
        int cc2 = RL(cc, 10);
        int ee2 = RL(f5(aa2, bb, cc2) + ee + this.X[14] + 1352829926, 9) + dd;
        int bb2 = RL(bb, 10);
        int dd2 = RL(f5(ee2, aa2, bb2) + dd + this.X[7] + 1352829926, 9) + cc2;
        int aa3 = RL(aa2, 10);
        int cc3 = RL(f5(dd2, ee2, aa3) + cc2 + this.X[0] + 1352829926, 11) + bb2;
        int ee3 = RL(ee2, 10);
        int bb3 = RL(f5(cc3, dd2, ee3) + bb2 + this.X[9] + 1352829926, 13) + aa3;
        int dd3 = RL(dd2, 10);
        int aa4 = RL(f5(bb3, cc3, dd3) + aa3 + this.X[2] + 1352829926, 15) + ee3;
        int cc4 = RL(cc3, 10);
        int ee4 = RL(f5(aa4, bb3, cc4) + ee3 + this.X[11] + 1352829926, 15) + dd3;
        int bb4 = RL(bb3, 10);
        int dd4 = RL(f5(ee4, aa4, bb4) + dd3 + this.X[4] + 1352829926, 5) + cc4;
        int aa5 = RL(aa4, 10);
        int cc5 = RL(f5(dd4, ee4, aa5) + cc4 + this.X[13] + 1352829926, 7) + bb4;
        int ee5 = RL(ee4, 10);
        int bb5 = RL(f5(cc5, dd4, ee5) + bb4 + this.X[6] + 1352829926, 7) + aa5;
        int dd5 = RL(dd4, 10);
        int aa6 = RL(f5(bb5, cc5, dd5) + aa5 + this.X[15] + 1352829926, 8) + ee5;
        int cc6 = RL(cc5, 10);
        int ee6 = RL(f5(aa6, bb5, cc6) + ee5 + this.X[8] + 1352829926, 11) + dd5;
        int bb6 = RL(bb5, 10);
        int dd6 = RL(f5(ee6, aa6, bb6) + dd5 + this.X[1] + 1352829926, 14) + cc6;
        int aa7 = RL(aa6, 10);
        int cc7 = RL(f5(dd6, ee6, aa7) + cc6 + this.X[10] + 1352829926, 14) + bb6;
        int ee7 = RL(ee6, 10);
        int bb7 = RL(f5(cc7, dd6, ee7) + bb6 + this.X[3] + 1352829926, 12) + aa7;
        int dd7 = RL(dd6, 10);
        int aa8 = RL(f5(bb7, cc7, dd7) + aa7 + this.X[12] + 1352829926, 6) + ee7;
        int cc8 = RL(cc7, 10);
        int e7 = RL(f2(a7, b6, c7) + e6 + this.X[7] + 1518500249, 7) + d6;
        int b7 = RL(b6, 10);
        int d7 = RL(f2(e7, a7, b7) + d6 + this.X[4] + 1518500249, 6) + c7;
        int a8 = RL(a7, 10);
        int c8 = RL(f2(d7, e7, a8) + c7 + this.X[13] + 1518500249, 8) + b7;
        int e8 = RL(e7, 10);
        int b8 = RL(f2(c8, d7, e8) + b7 + this.X[1] + 1518500249, 13) + a8;
        int d8 = RL(d7, 10);
        int a9 = RL(f2(b8, c8, d8) + a8 + this.X[10] + 1518500249, 11) + e8;
        int c9 = RL(c8, 10);
        int e9 = RL(f2(a9, b8, c9) + e8 + this.X[6] + 1518500249, 9) + d8;
        int b9 = RL(b8, 10);
        int d9 = RL(f2(e9, a9, b9) + d8 + this.X[15] + 1518500249, 7) + c9;
        int a10 = RL(a9, 10);
        int c10 = RL(f2(d9, e9, a10) + c9 + this.X[3] + 1518500249, 15) + b9;
        int e10 = RL(e9, 10);
        int b10 = RL(f2(c10, d9, e10) + b9 + this.X[12] + 1518500249, 7) + a10;
        int d10 = RL(d9, 10);
        int a11 = RL(f2(b10, c10, d10) + a10 + this.X[0] + 1518500249, 12) + e10;
        int c11 = RL(c10, 10);
        int e11 = RL(f2(a11, b10, c11) + e10 + this.X[9] + 1518500249, 15) + d10;
        int b11 = RL(b10, 10);
        int d11 = RL(f2(e11, a11, b11) + d10 + this.X[5] + 1518500249, 9) + c11;
        int a12 = RL(a11, 10);
        int c12 = RL(f2(d11, e11, a12) + c11 + this.X[2] + 1518500249, 11) + b11;
        int e12 = RL(e11, 10);
        int b12 = RL(f2(c12, d11, e12) + b11 + this.X[14] + 1518500249, 7) + a12;
        int d12 = RL(d11, 10);
        int a13 = RL(f2(b12, c12, d12) + a12 + this.X[11] + 1518500249, 13) + e12;
        int c13 = RL(c12, 10);
        int e13 = RL(f2(a13, b12, c13) + e12 + this.X[8] + 1518500249, 12) + d12;
        int b13 = RL(b12, 10);
        int ee8 = RL(f4(aa8, bb7, cc8) + ee7 + this.X[6] + 1548603684, 9) + dd7;
        int bb8 = RL(bb7, 10);
        int dd8 = RL(f4(ee8, aa8, bb8) + dd7 + this.X[11] + 1548603684, 13) + cc8;
        int aa9 = RL(aa8, 10);
        int cc9 = RL(f4(dd8, ee8, aa9) + cc8 + this.X[3] + 1548603684, 15) + bb8;
        int ee9 = RL(ee8, 10);
        int bb9 = RL(f4(cc9, dd8, ee9) + bb8 + this.X[7] + 1548603684, 7) + aa9;
        int dd9 = RL(dd8, 10);
        int aa10 = RL(f4(bb9, cc9, dd9) + aa9 + this.X[0] + 1548603684, 12) + ee9;
        int cc10 = RL(cc9, 10);
        int ee10 = RL(f4(aa10, bb9, cc10) + ee9 + this.X[13] + 1548603684, 8) + dd9;
        int bb10 = RL(bb9, 10);
        int dd10 = RL(f4(ee10, aa10, bb10) + dd9 + this.X[5] + 1548603684, 9) + cc10;
        int aa11 = RL(aa10, 10);
        int cc11 = RL(f4(dd10, ee10, aa11) + cc10 + this.X[10] + 1548603684, 11) + bb10;
        int ee11 = RL(ee10, 10);
        int bb11 = RL(f4(cc11, dd10, ee11) + bb10 + this.X[14] + 1548603684, 7) + aa11;
        int dd11 = RL(dd10, 10);
        int aa12 = RL(f4(bb11, cc11, dd11) + aa11 + this.X[15] + 1548603684, 7) + ee11;
        int cc12 = RL(cc11, 10);
        int ee12 = RL(f4(aa12, bb11, cc12) + ee11 + this.X[8] + 1548603684, 12) + dd11;
        int bb12 = RL(bb11, 10);
        int dd12 = RL(f4(ee12, aa12, bb12) + dd11 + this.X[12] + 1548603684, 7) + cc12;
        int aa13 = RL(aa12, 10);
        int cc13 = RL(f4(dd12, ee12, aa13) + cc12 + this.X[4] + 1548603684, 6) + bb12;
        int ee13 = RL(ee12, 10);
        int bb13 = RL(f4(cc13, dd12, ee13) + bb12 + this.X[9] + 1548603684, 15) + aa13;
        int dd13 = RL(dd12, 10);
        int aa14 = RL(f4(bb13, cc13, dd13) + aa13 + this.X[1] + 1548603684, 13) + ee13;
        int cc14 = RL(cc13, 10);
        int ee14 = RL(f4(aa14, bb13, cc14) + ee13 + this.X[2] + 1548603684, 11) + dd13;
        int bb14 = RL(bb13, 10);
        int d13 = RL(f3(e13, a13, b13) + d12 + this.X[3] + 1859775393, 11) + c13;
        int a14 = RL(a13, 10);
        int c14 = RL(f3(d13, e13, a14) + c13 + this.X[10] + 1859775393, 13) + b13;
        int e14 = RL(e13, 10);
        int b14 = RL(f3(c14, d13, e14) + b13 + this.X[14] + 1859775393, 6) + a14;
        int d14 = RL(d13, 10);
        int a15 = RL(f3(b14, c14, d14) + a14 + this.X[4] + 1859775393, 7) + e14;
        int c15 = RL(c14, 10);
        int e15 = RL(f3(a15, b14, c15) + e14 + this.X[9] + 1859775393, 14) + d14;
        int b15 = RL(b14, 10);
        int d15 = RL(f3(e15, a15, b15) + d14 + this.X[15] + 1859775393, 9) + c15;
        int a16 = RL(a15, 10);
        int c16 = RL(f3(d15, e15, a16) + c15 + this.X[8] + 1859775393, 13) + b15;
        int e16 = RL(e15, 10);
        int b16 = RL(f3(c16, d15, e16) + b15 + this.X[1] + 1859775393, 15) + a16;
        int d16 = RL(d15, 10);
        int a17 = RL(f3(b16, c16, d16) + a16 + this.X[2] + 1859775393, 14) + e16;
        int c17 = RL(c16, 10);
        int e17 = RL(f3(a17, b16, c17) + e16 + this.X[7] + 1859775393, 8) + d16;
        int b17 = RL(b16, 10);
        int d17 = RL(f3(e17, a17, b17) + d16 + this.X[0] + 1859775393, 13) + c17;
        int a18 = RL(a17, 10);
        int c18 = RL(f3(d17, e17, a18) + c17 + this.X[6] + 1859775393, 6) + b17;
        int e18 = RL(e17, 10);
        int b18 = RL(f3(c18, d17, e18) + b17 + this.X[13] + 1859775393, 5) + a18;
        int d18 = RL(d17, 10);
        int a19 = RL(f3(b18, c18, d18) + a18 + this.X[11] + 1859775393, 12) + e18;
        int c19 = RL(c18, 10);
        int e19 = RL(f3(a19, b18, c19) + e18 + this.X[5] + 1859775393, 7) + d18;
        int b19 = RL(b18, 10);
        int d19 = RL(f3(e19, a19, b19) + d18 + this.X[12] + 1859775393, 5) + c19;
        int a20 = RL(a19, 10);
        int dd14 = RL(f3(ee14, aa14, bb14) + dd13 + this.X[15] + 1836072691, 9) + cc14;
        int aa15 = RL(aa14, 10);
        int cc15 = RL(f3(dd14, ee14, aa15) + cc14 + this.X[5] + 1836072691, 7) + bb14;
        int ee15 = RL(ee14, 10);
        int bb15 = RL(f3(cc15, dd14, ee15) + bb14 + this.X[1] + 1836072691, 15) + aa15;
        int dd15 = RL(dd14, 10);
        int aa16 = RL(f3(bb15, cc15, dd15) + aa15 + this.X[3] + 1836072691, 11) + ee15;
        int cc16 = RL(cc15, 10);
        int ee16 = RL(f3(aa16, bb15, cc16) + ee15 + this.X[7] + 1836072691, 8) + dd15;
        int bb16 = RL(bb15, 10);
        int dd16 = RL(f3(ee16, aa16, bb16) + dd15 + this.X[14] + 1836072691, 6) + cc16;
        int aa17 = RL(aa16, 10);
        int cc17 = RL(f3(dd16, ee16, aa17) + cc16 + this.X[6] + 1836072691, 6) + bb16;
        int ee17 = RL(ee16, 10);
        int bb17 = RL(f3(cc17, dd16, ee17) + bb16 + this.X[9] + 1836072691, 14) + aa17;
        int dd17 = RL(dd16, 10);
        int aa18 = RL(f3(bb17, cc17, dd17) + aa17 + this.X[11] + 1836072691, 12) + ee17;
        int cc18 = RL(cc17, 10);
        int ee18 = RL(f3(aa18, bb17, cc18) + ee17 + this.X[8] + 1836072691, 13) + dd17;
        int bb18 = RL(bb17, 10);
        int dd18 = RL(f3(ee18, aa18, bb18) + dd17 + this.X[12] + 1836072691, 5) + cc18;
        int aa19 = RL(aa18, 10);
        int cc19 = RL(f3(dd18, ee18, aa19) + cc18 + this.X[2] + 1836072691, 14) + bb18;
        int ee19 = RL(ee18, 10);
        int bb19 = RL(f3(cc19, dd18, ee19) + bb18 + this.X[10] + 1836072691, 13) + aa19;
        int dd19 = RL(dd18, 10);
        int aa20 = RL(f3(bb19, cc19, dd19) + aa19 + this.X[0] + 1836072691, 13) + ee19;
        int cc20 = RL(cc19, 10);
        int ee20 = RL(f3(aa20, bb19, cc20) + ee19 + this.X[4] + 1836072691, 7) + dd19;
        int bb20 = RL(bb19, 10);
        int dd20 = RL(f3(ee20, aa20, bb20) + dd19 + this.X[13] + 1836072691, 5) + cc20;
        int aa21 = RL(aa20, 10);
        int c20 = RL(((f4(d19, e19, a20) + c19) + this.X[1]) - 1894007588, 11) + b19;
        int e20 = RL(e19, 10);
        int b20 = RL(((f4(c20, d19, e20) + b19) + this.X[9]) - 1894007588, 12) + a20;
        int d20 = RL(d19, 10);
        int a21 = RL(((f4(b20, c20, d20) + a20) + this.X[11]) - 1894007588, 14) + e20;
        int c21 = RL(c20, 10);
        int e21 = RL(((f4(a21, b20, c21) + e20) + this.X[10]) - 1894007588, 15) + d20;
        int b21 = RL(b20, 10);
        int d21 = RL(((f4(e21, a21, b21) + d20) + this.X[0]) - 1894007588, 14) + c21;
        int a22 = RL(a21, 10);
        int c22 = RL(((f4(d21, e21, a22) + c21) + this.X[8]) - 1894007588, 15) + b21;
        int e22 = RL(e21, 10);
        int b22 = RL(((f4(c22, d21, e22) + b21) + this.X[12]) - 1894007588, 9) + a22;
        int d22 = RL(d21, 10);
        int a23 = RL(((f4(b22, c22, d22) + a22) + this.X[4]) - 1894007588, 8) + e22;
        int c23 = RL(c22, 10);
        int e23 = RL(((f4(a23, b22, c23) + e22) + this.X[13]) - 1894007588, 9) + d22;
        int b23 = RL(b22, 10);
        int d23 = RL(((f4(e23, a23, b23) + d22) + this.X[3]) - 1894007588, 14) + c23;
        int a24 = RL(a23, 10);
        int c24 = RL(((f4(d23, e23, a24) + c23) + this.X[7]) - 1894007588, 5) + b23;
        int e24 = RL(e23, 10);
        int b24 = RL(((f4(c24, d23, e24) + b23) + this.X[15]) - 1894007588, 6) + a24;
        int d24 = RL(d23, 10);
        int a25 = RL(((f4(b24, c24, d24) + a24) + this.X[14]) - 1894007588, 8) + e24;
        int c25 = RL(c24, 10);
        int e25 = RL(((f4(a25, b24, c25) + e24) + this.X[5]) - 1894007588, 6) + d24;
        int b25 = RL(b24, 10);
        int d25 = RL(((f4(e25, a25, b25) + d24) + this.X[6]) - 1894007588, 5) + c25;
        int a26 = RL(a25, 10);
        int c26 = RL(((f4(d25, e25, a26) + c25) + this.X[2]) - 1894007588, 12) + b25;
        int e26 = RL(e25, 10);
        int cc21 = RL(f2(dd20, ee20, aa21) + cc20 + this.X[8] + 2053994217, 15) + bb20;
        int ee21 = RL(ee20, 10);
        int bb21 = RL(f2(cc21, dd20, ee21) + bb20 + this.X[6] + 2053994217, 5) + aa21;
        int dd21 = RL(dd20, 10);
        int aa22 = RL(f2(bb21, cc21, dd21) + aa21 + this.X[4] + 2053994217, 8) + ee21;
        int cc22 = RL(cc21, 10);
        int ee22 = RL(f2(aa22, bb21, cc22) + ee21 + this.X[1] + 2053994217, 11) + dd21;
        int bb22 = RL(bb21, 10);
        int dd22 = RL(f2(ee22, aa22, bb22) + dd21 + this.X[3] + 2053994217, 14) + cc22;
        int aa23 = RL(aa22, 10);
        int cc23 = RL(f2(dd22, ee22, aa23) + cc22 + this.X[11] + 2053994217, 14) + bb22;
        int ee23 = RL(ee22, 10);
        int bb23 = RL(f2(cc23, dd22, ee23) + bb22 + this.X[15] + 2053994217, 6) + aa23;
        int dd23 = RL(dd22, 10);
        int aa24 = RL(f2(bb23, cc23, dd23) + aa23 + this.X[0] + 2053994217, 14) + ee23;
        int cc24 = RL(cc23, 10);
        int ee24 = RL(f2(aa24, bb23, cc24) + ee23 + this.X[5] + 2053994217, 6) + dd23;
        int bb24 = RL(bb23, 10);
        int dd24 = RL(f2(ee24, aa24, bb24) + dd23 + this.X[12] + 2053994217, 9) + cc24;
        int aa25 = RL(aa24, 10);
        int cc25 = RL(f2(dd24, ee24, aa25) + cc24 + this.X[2] + 2053994217, 12) + bb24;
        int ee25 = RL(ee24, 10);
        int bb25 = RL(f2(cc25, dd24, ee25) + bb24 + this.X[13] + 2053994217, 9) + aa25;
        int dd25 = RL(dd24, 10);
        int aa26 = RL(f2(bb25, cc25, dd25) + aa25 + this.X[9] + 2053994217, 12) + ee25;
        int cc26 = RL(cc25, 10);
        int ee26 = RL(f2(aa26, bb25, cc26) + ee25 + this.X[7] + 2053994217, 5) + dd25;
        int bb26 = RL(bb25, 10);
        int dd26 = RL(f2(ee26, aa26, bb26) + dd25 + this.X[10] + 2053994217, 15) + cc26;
        int aa27 = RL(aa26, 10);
        int cc27 = RL(f2(dd26, ee26, aa27) + cc26 + this.X[14] + 2053994217, 8) + bb26;
        int ee27 = RL(ee26, 10);
        int b26 = RL(((f5(c26, d25, e26) + b25) + this.X[4]) - 1454113458, 9) + a26;
        int d26 = RL(d25, 10);
        int a27 = RL(((f5(b26, c26, d26) + a26) + this.X[0]) - 1454113458, 15) + e26;
        int c27 = RL(c26, 10);
        int e27 = RL(((f5(a27, b26, c27) + e26) + this.X[5]) - 1454113458, 5) + d26;
        int b27 = RL(b26, 10);
        int d27 = RL(((f5(e27, a27, b27) + d26) + this.X[9]) - 1454113458, 11) + c27;
        int a28 = RL(a27, 10);
        int c28 = RL(((f5(d27, e27, a28) + c27) + this.X[7]) - 1454113458, 6) + b27;
        int e28 = RL(e27, 10);
        int b28 = RL(((f5(c28, d27, e28) + b27) + this.X[12]) - 1454113458, 8) + a28;
        int d28 = RL(d27, 10);
        int a29 = RL(((f5(b28, c28, d28) + a28) + this.X[2]) - 1454113458, 13) + e28;
        int c29 = RL(c28, 10);
        int e29 = RL(((f5(a29, b28, c29) + e28) + this.X[10]) - 1454113458, 12) + d28;
        int b29 = RL(b28, 10);
        int d29 = RL(((f5(e29, a29, b29) + d28) + this.X[14]) - 1454113458, 5) + c29;
        int a30 = RL(a29, 10);
        int c30 = RL(((f5(d29, e29, a30) + c29) + this.X[1]) - 1454113458, 12) + b29;
        int e30 = RL(e29, 10);
        int b30 = RL(((f5(c30, d29, e30) + b29) + this.X[3]) - 1454113458, 13) + a30;
        int d30 = RL(d29, 10);
        int a31 = RL(((f5(b30, c30, d30) + a30) + this.X[8]) - 1454113458, 14) + e30;
        int c31 = RL(c30, 10);
        int e31 = RL(((f5(a31, b30, c31) + e30) + this.X[11]) - 1454113458, 11) + d30;
        int b31 = RL(b30, 10);
        int d31 = RL(((f5(e31, a31, b31) + d30) + this.X[6]) - 1454113458, 8) + c31;
        int a32 = RL(a31, 10);
        int c32 = RL(((f5(d31, e31, a32) + c31) + this.X[15]) - 1454113458, 5) + b31;
        int e32 = RL(e31, 10);
        int b32 = RL(((f5(c32, d31, e32) + b31) + this.X[13]) - 1454113458, 6) + a32;
        int d32 = RL(d31, 10);
        int bb27 = RL(f1(cc27, dd26, ee27) + bb26 + this.X[12], 8) + aa27;
        int dd27 = RL(dd26, 10);
        int aa28 = RL(f1(bb27, cc27, dd27) + aa27 + this.X[15], 5) + ee27;
        int cc28 = RL(cc27, 10);
        int ee28 = RL(f1(aa28, bb27, cc28) + ee27 + this.X[10], 12) + dd27;
        int bb28 = RL(bb27, 10);
        int dd28 = RL(f1(ee28, aa28, bb28) + dd27 + this.X[4], 9) + cc28;
        int aa29 = RL(aa28, 10);
        int cc29 = RL(f1(dd28, ee28, aa29) + cc28 + this.X[1], 12) + bb28;
        int ee29 = RL(ee28, 10);
        int bb29 = RL(f1(cc29, dd28, ee29) + bb28 + this.X[5], 5) + aa29;
        int dd29 = RL(dd28, 10);
        int aa30 = RL(f1(bb29, cc29, dd29) + aa29 + this.X[8], 14) + ee29;
        int cc30 = RL(cc29, 10);
        int ee30 = RL(f1(aa30, bb29, cc30) + ee29 + this.X[7], 6) + dd29;
        int bb30 = RL(bb29, 10);
        int dd30 = RL(f1(ee30, aa30, bb30) + dd29 + this.X[6], 8) + cc30;
        int aa31 = RL(aa30, 10);
        int cc31 = RL(f1(dd30, ee30, aa31) + cc30 + this.X[2], 13) + bb30;
        int ee31 = RL(ee30, 10);
        int bb31 = RL(f1(cc31, dd30, ee31) + bb30 + this.X[13], 6) + aa31;
        int dd31 = RL(dd30, 10);
        int aa32 = RL(f1(bb31, cc31, dd31) + aa31 + this.X[14], 5) + ee31;
        int cc32 = RL(cc31, 10);
        int ee32 = RL(f1(aa32, bb31, cc32) + ee31 + this.X[0], 15) + dd31;
        int bb32 = RL(bb31, 10);
        int dd32 = RL(f1(ee32, aa32, bb32) + dd31 + this.X[3], 13) + cc32;
        int aa33 = RL(aa32, 10);
        int cc33 = RL(f1(dd32, ee32, aa33) + cc32 + this.X[9], 11) + bb32;
        int ee33 = RL(ee32, 10);
        int bb33 = RL(f1(cc33, dd32, ee33) + bb32 + this.X[11], 11) + aa33;
        int dd33 = RL(dd32, 10) + this.H1 + c32;
        this.H1 = this.H2 + d32 + ee33;
        this.H2 = this.H3 + e32 + aa33;
        this.H3 = this.H4 + a32 + bb33;
        this.H4 = this.H0 + b32 + cc33;
        this.H0 = dd33;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }
}