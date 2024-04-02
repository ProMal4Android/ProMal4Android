package org.spongycastle.crypto.engines;

import java.security.SecureRandom;
import org.spongycastle.crypto.AsymmetricBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.params.NTRUEncryptionParameters;
import org.spongycastle.crypto.params.NTRUEncryptionPrivateKeyParameters;
import org.spongycastle.crypto.params.NTRUEncryptionPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.math.ntru.polynomial.DenseTernaryPolynomial;
import org.spongycastle.math.ntru.polynomial.IntegerPolynomial;
import org.spongycastle.math.ntru.polynomial.Polynomial;
import org.spongycastle.math.ntru.polynomial.ProductFormPolynomial;
import org.spongycastle.math.ntru.polynomial.SparseTernaryPolynomial;
import org.spongycastle.math.ntru.polynomial.TernaryPolynomial;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class NTRUEngine implements AsymmetricBlockCipher {
    private boolean forEncryption;
    private NTRUEncryptionParameters params;
    private NTRUEncryptionPrivateKeyParameters privKey;
    private NTRUEncryptionPublicKeyParameters pubKey;
    private SecureRandom random;

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public void init(boolean forEncryption, CipherParameters parameters) {
        this.forEncryption = forEncryption;
        if (forEncryption) {
            if (parameters instanceof ParametersWithRandom) {
                ParametersWithRandom p = (ParametersWithRandom) parameters;
                this.random = p.getRandom();
                this.pubKey = (NTRUEncryptionPublicKeyParameters) p.getParameters();
            } else {
                this.random = new SecureRandom();
                this.pubKey = (NTRUEncryptionPublicKeyParameters) parameters;
            }
            this.params = this.pubKey.getParameters();
            return;
        }
        this.privKey = (NTRUEncryptionPrivateKeyParameters) parameters;
        this.params = this.privKey.getParameters();
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getInputBlockSize() {
        return this.params.maxMsgLenBytes;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public int getOutputBlockSize() {
        return ((this.params.N * log2(this.params.q)) + 7) / 8;
    }

    @Override // org.spongycastle.crypto.AsymmetricBlockCipher
    public byte[] processBlock(byte[] in, int inOff, int len) throws InvalidCipherTextException {
        byte[] tmp = new byte[len];
        System.arraycopy(in, inOff, tmp, 0, len);
        return this.forEncryption ? encrypt(tmp, this.pubKey) : decrypt(tmp, this.privKey);
    }

    private byte[] encrypt(byte[] m, NTRUEncryptionPublicKeyParameters pubKey) {
        IntegerPolynomial pub = pubKey.h;
        int N = this.params.N;
        int q = this.params.q;
        int maxLenBytes = this.params.maxMsgLenBytes;
        int db = this.params.db;
        int bufferLenBits = this.params.bufferLenBits;
        int dm0 = this.params.dm0;
        int pkLen = this.params.pkLen;
        int minCallsMask = this.params.minCallsMask;
        boolean hashSeed = this.params.hashSeed;
        byte[] oid = this.params.oid;
        int l = m.length;
        if (maxLenBytes > 255) {
            throw new IllegalArgumentException("llen values bigger than 1 are not supported");
        }
        if (l > maxLenBytes) {
            throw new DataLengthException("Message too long: " + l + ">" + maxLenBytes);
        }
        while (true) {
            byte[] b = new byte[db / 8];
            this.random.nextBytes(b);
            byte[] p0 = new byte[(maxLenBytes + 1) - l];
            byte[] M = new byte[bufferLenBits / 8];
            System.arraycopy(b, 0, M, 0, b.length);
            M[b.length] = (byte) l;
            System.arraycopy(m, 0, M, b.length + 1, m.length);
            System.arraycopy(p0, 0, M, b.length + 1 + m.length, p0.length);
            IntegerPolynomial mTrin = IntegerPolynomial.fromBinary3Sves(M, N);
            byte[] bh = pub.toBinary(q);
            byte[] hTrunc = copyOf(bh, pkLen / 8);
            byte[] sData = buildSData(oid, m, l, b, hTrunc);
            Polynomial r = generateBlindingPoly(sData, M);
            IntegerPolynomial R = r.mult(pub, q);
            IntegerPolynomial R4 = (IntegerPolynomial) R.clone();
            R4.modPositive(4);
            byte[] oR4 = R4.toBinary(4);
            IntegerPolynomial mask = MGF(oR4, N, minCallsMask, hashSeed);
            mTrin.add(mask);
            mTrin.mod3();
            if (mTrin.count(-1) >= dm0 && mTrin.count(0) >= dm0 && mTrin.count(1) >= dm0) {
                R.add(mTrin, q);
                R.ensurePositive(q);
                return R.toBinary(q);
            }
        }
    }

    private byte[] buildSData(byte[] oid, byte[] m, int l, byte[] b, byte[] hTrunc) {
        byte[] sData = new byte[oid.length + l + b.length + hTrunc.length];
        System.arraycopy(oid, 0, sData, 0, oid.length);
        System.arraycopy(m, 0, sData, oid.length, m.length);
        System.arraycopy(b, 0, sData, oid.length + m.length, b.length);
        System.arraycopy(hTrunc, 0, sData, oid.length + m.length + b.length, hTrunc.length);
        return sData;
    }

    protected IntegerPolynomial encrypt(IntegerPolynomial m, TernaryPolynomial r, IntegerPolynomial pubKey) {
        IntegerPolynomial e = r.mult(pubKey, this.params.q);
        e.add(m, this.params.q);
        e.ensurePositive(this.params.q);
        return e;
    }

    private Polynomial generateBlindingPoly(byte[] seed, byte[] M) {
        IndexGenerator ig = new IndexGenerator(seed, this.params);
        if (this.params.polyType == 1) {
            SparseTernaryPolynomial r1 = new SparseTernaryPolynomial(generateBlindingCoeffs(ig, this.params.dr1));
            SparseTernaryPolynomial r2 = new SparseTernaryPolynomial(generateBlindingCoeffs(ig, this.params.dr2));
            SparseTernaryPolynomial r3 = new SparseTernaryPolynomial(generateBlindingCoeffs(ig, this.params.dr3));
            return new ProductFormPolynomial(r1, r2, r3);
        }
        int dr = this.params.dr;
        boolean sparse = this.params.sparse;
        int[] r = generateBlindingCoeffs(ig, dr);
        if (sparse) {
            return new SparseTernaryPolynomial(r);
        }
        return new DenseTernaryPolynomial(r);
    }

    private int[] generateBlindingCoeffs(IndexGenerator ig, int dr) {
        int N = this.params.N;
        int[] r = new int[N];
        for (int coeff = -1; coeff <= 1; coeff += 2) {
            int t = 0;
            while (t < dr) {
                int i = ig.nextIndex();
                if (r[i] == 0) {
                    r[i] = coeff;
                    t++;
                }
            }
        }
        return r;
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0071, code lost:
        if (r5 >= r17) goto L35;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.spongycastle.math.ntru.polynomial.IntegerPolynomial MGF(byte[] r16, int r17, int r18, boolean r19) {
        /*
            r15 = this;
            org.spongycastle.crypto.params.NTRUEncryptionParameters r13 = r15.params
            org.spongycastle.crypto.Digest r7 = r13.hashAlg
            int r8 = r7.getDigestSize()
            int r13 = r18 * r8
            byte[] r3 = new byte[r13]
            if (r19 == 0) goto L2e
            r0 = r16
            byte[] r2 = r15.calcHash(r7, r0)
        L14:
            r4 = 0
        L15:
            r0 = r18
            if (r4 >= r0) goto L31
            r13 = 0
            int r14 = r2.length
            r7.update(r2, r13, r14)
            r15.putInt(r7, r4)
            byte[] r6 = r15.calcHash(r7)
            r13 = 0
            int r14 = r4 * r8
            java.lang.System.arraycopy(r6, r13, r3, r14, r8)
            int r4 = r4 + 1
            goto L15
        L2e:
            r2 = r16
            goto L14
        L31:
            org.spongycastle.math.ntru.polynomial.IntegerPolynomial r9 = new org.spongycastle.math.ntru.polynomial.IntegerPolynomial
            r0 = r17
            r9.<init>(r0)
        L38:
            r5 = 0
            r10 = 0
        L3a:
            int r13 = r3.length
            if (r10 == r13) goto L6f
            r13 = r3[r10]
            r1 = r13 & 255(0xff, float:3.57E-43)
            r13 = 243(0xf3, float:3.4E-43)
            if (r1 < r13) goto L48
        L45:
            int r10 = r10 + 1
            goto L3a
        L48:
            r12 = 0
        L49:
            r13 = 4
            if (r12 >= r13) goto L62
            int r11 = r1 % 3
            int[] r13 = r9.coeffs
            int r14 = r11 + (-1)
            r13[r5] = r14
            int r5 = r5 + 1
            r0 = r17
            if (r5 != r0) goto L5b
        L5a:
            return r9
        L5b:
            int r13 = r1 - r11
            int r1 = r13 / 3
            int r12 = r12 + 1
            goto L49
        L62:
            int[] r13 = r9.coeffs
            int r14 = r1 + (-1)
            r13[r5] = r14
            int r5 = r5 + 1
            r0 = r17
            if (r5 != r0) goto L45
            goto L5a
        L6f:
            r0 = r17
            if (r5 >= r0) goto L5a
            r13 = 0
            int r14 = r2.length
            r7.update(r2, r13, r14)
            r15.putInt(r7, r4)
            byte[] r6 = r15.calcHash(r7)
            r3 = r6
            int r4 = r4 + 1
            goto L38
        */
        throw new UnsupportedOperationException("Method not decompiled: org.spongycastle.crypto.engines.NTRUEngine.MGF(byte[], int, int, boolean):org.spongycastle.math.ntru.polynomial.IntegerPolynomial");
    }

    private void putInt(Digest hashAlg, int counter) {
        hashAlg.update((byte) (counter >> 24));
        hashAlg.update((byte) (counter >> 16));
        hashAlg.update((byte) (counter >> 8));
        hashAlg.update((byte) counter);
    }

    private byte[] calcHash(Digest hashAlg) {
        byte[] tmp = new byte[hashAlg.getDigestSize()];
        hashAlg.doFinal(tmp, 0);
        return tmp;
    }

    private byte[] calcHash(Digest hashAlg, byte[] input) {
        byte[] tmp = new byte[hashAlg.getDigestSize()];
        hashAlg.update(input, 0, input.length);
        hashAlg.doFinal(tmp, 0);
        return tmp;
    }

    private byte[] decrypt(byte[] data, NTRUEncryptionPrivateKeyParameters privKey) throws InvalidCipherTextException {
        Polynomial priv_t = privKey.t;
        IntegerPolynomial priv_fp = privKey.fp;
        IntegerPolynomial pub = privKey.h;
        int N = this.params.N;
        int q = this.params.q;
        int db = this.params.db;
        int maxMsgLenBytes = this.params.maxMsgLenBytes;
        int dm0 = this.params.dm0;
        int pkLen = this.params.pkLen;
        int minCallsMask = this.params.minCallsMask;
        boolean hashSeed = this.params.hashSeed;
        byte[] oid = this.params.oid;
        if (maxMsgLenBytes > 255) {
            throw new DataLengthException("maxMsgLenBytes values bigger than 255 are not supported");
        }
        int bLen = db / 8;
        IntegerPolynomial e = IntegerPolynomial.fromBinary(data, N, q);
        IntegerPolynomial ci = decrypt(e, priv_t, priv_fp);
        if (ci.count(-1) < dm0) {
            throw new InvalidCipherTextException("Less than dm0 coefficients equal -1");
        }
        if (ci.count(0) < dm0) {
            throw new InvalidCipherTextException("Less than dm0 coefficients equal 0");
        }
        if (ci.count(1) < dm0) {
            throw new InvalidCipherTextException("Less than dm0 coefficients equal 1");
        }
        IntegerPolynomial cR = (IntegerPolynomial) e.clone();
        cR.sub(ci);
        cR.modPositive(q);
        IntegerPolynomial cR4 = (IntegerPolynomial) cR.clone();
        cR4.modPositive(4);
        byte[] coR4 = cR4.toBinary(4);
        IntegerPolynomial mask = MGF(coR4, N, minCallsMask, hashSeed);
        ci.sub(mask);
        ci.mod3();
        byte[] cM = ci.toBinary3Sves();
        byte[] cb = new byte[bLen];
        System.arraycopy(cM, 0, cb, 0, bLen);
        int cl = cM[bLen] & 255;
        if (cl > maxMsgLenBytes) {
            throw new InvalidCipherTextException("Message too long: " + cl + ">" + maxMsgLenBytes);
        }
        byte[] cm = new byte[cl];
        System.arraycopy(cM, bLen + 1, cm, 0, cl);
        byte[] p0 = new byte[cM.length - ((bLen + 1) + cl)];
        System.arraycopy(cM, bLen + 1 + cl, p0, 0, p0.length);
        if (!Arrays.areEqual(p0, new byte[p0.length])) {
            throw new InvalidCipherTextException("The message is not followed by zeroes");
        }
        byte[] bh = pub.toBinary(q);
        byte[] hTrunc = copyOf(bh, pkLen / 8);
        byte[] sData = buildSData(oid, cm, cl, cb, hTrunc);
        Polynomial cr = generateBlindingPoly(sData, cm);
        IntegerPolynomial cRPrime = cr.mult(pub);
        cRPrime.modPositive(q);
        if (!cRPrime.equals(cR)) {
            throw new InvalidCipherTextException("Invalid message encoding");
        }
        return cm;
    }

    protected IntegerPolynomial decrypt(IntegerPolynomial e, Polynomial priv_t, IntegerPolynomial priv_fp) {
        IntegerPolynomial a;
        if (this.params.fastFp) {
            a = priv_t.mult(e, this.params.q);
            a.mult(3);
            a.add(e);
        } else {
            a = priv_t.mult(e, this.params.q);
        }
        a.center0(this.params.q);
        a.mod3();
        IntegerPolynomial c = this.params.fastFp ? a : new DenseTernaryPolynomial(a).mult(priv_fp, 3);
        c.center0(3);
        return c;
    }

    private byte[] copyOf(byte[] src, int len) {
        byte[] tmp = new byte[len];
        if (len >= src.length) {
            len = src.length;
        }
        System.arraycopy(src, 0, tmp, 0, len);
        return tmp;
    }

    private int log2(int value) {
        if (value == 2048) {
            return 11;
        }
        throw new IllegalStateException("log2 not fully implemented");
    }
}