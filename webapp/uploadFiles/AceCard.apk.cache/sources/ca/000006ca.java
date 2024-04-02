package org.spongycastle.crypto.params;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;

/* loaded from: classes.dex */
public class NTRUSigningParameters implements Cloneable {
    public int B;
    public int N;
    double beta;
    public double betaSq;
    int bitsF;
    public int d;
    public int d1;
    public int d2;
    public int d3;
    public Digest hashAlg;
    double normBound;
    public double normBoundSq;
    public int q;
    public int signFailTolerance;

    public NTRUSigningParameters(int N, int q, int d, int B, double beta, double normBound, Digest hashAlg) {
        this.signFailTolerance = 100;
        this.bitsF = 6;
        this.N = N;
        this.q = q;
        this.d = d;
        this.B = B;
        this.beta = beta;
        this.normBound = normBound;
        this.hashAlg = hashAlg;
        init();
    }

    public NTRUSigningParameters(int N, int q, int d1, int d2, int d3, int B, double beta, double normBound, double keyNormBound, Digest hashAlg) {
        this.signFailTolerance = 100;
        this.bitsF = 6;
        this.N = N;
        this.q = q;
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
        this.B = B;
        this.beta = beta;
        this.normBound = normBound;
        this.hashAlg = hashAlg;
        init();
    }

    private void init() {
        this.betaSq = this.beta * this.beta;
        this.normBoundSq = this.normBound * this.normBound;
    }

    public NTRUSigningParameters(InputStream is) throws IOException {
        this.signFailTolerance = 100;
        this.bitsF = 6;
        DataInputStream dis = new DataInputStream(is);
        this.N = dis.readInt();
        this.q = dis.readInt();
        this.d = dis.readInt();
        this.d1 = dis.readInt();
        this.d2 = dis.readInt();
        this.d3 = dis.readInt();
        this.B = dis.readInt();
        this.beta = dis.readDouble();
        this.normBound = dis.readDouble();
        this.signFailTolerance = dis.readInt();
        this.bitsF = dis.readInt();
        String alg = dis.readUTF();
        if ("SHA-512".equals(alg)) {
            this.hashAlg = new SHA512Digest();
        } else if ("SHA-256".equals(alg)) {
            this.hashAlg = new SHA256Digest();
        }
        init();
    }

    public void writeTo(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(this.N);
        dos.writeInt(this.q);
        dos.writeInt(this.d);
        dos.writeInt(this.d1);
        dos.writeInt(this.d2);
        dos.writeInt(this.d3);
        dos.writeInt(this.B);
        dos.writeDouble(this.beta);
        dos.writeDouble(this.normBound);
        dos.writeInt(this.signFailTolerance);
        dos.writeInt(this.bitsF);
        dos.writeUTF(this.hashAlg.getAlgorithmName());
    }

    public NTRUSigningParameters clone() {
        return new NTRUSigningParameters(this.N, this.q, this.d, this.B, this.beta, this.normBound, this.hashAlg);
    }

    public int hashCode() {
        int result = this.B + 31;
        int result2 = (result * 31) + this.N;
        long temp = Double.doubleToLongBits(this.beta);
        int result3 = (result2 * 31) + ((int) ((temp >>> 32) ^ temp));
        long temp2 = Double.doubleToLongBits(this.betaSq);
        int result4 = (((((((((((((result3 * 31) + ((int) ((temp2 >>> 32) ^ temp2))) * 31) + this.bitsF) * 31) + this.d) * 31) + this.d1) * 31) + this.d2) * 31) + this.d3) * 31) + (this.hashAlg == null ? 0 : this.hashAlg.getAlgorithmName().hashCode());
        long temp3 = Double.doubleToLongBits(this.normBound);
        int result5 = (result4 * 31) + ((int) ((temp3 >>> 32) ^ temp3));
        long temp4 = Double.doubleToLongBits(this.normBoundSq);
        return (((((result5 * 31) + ((int) ((temp4 >>> 32) ^ temp4))) * 31) + this.q) * 31) + this.signFailTolerance;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && (obj instanceof NTRUSigningParameters)) {
            NTRUSigningParameters other = (NTRUSigningParameters) obj;
            if (this.B == other.B && this.N == other.N && Double.doubleToLongBits(this.beta) == Double.doubleToLongBits(other.beta) && Double.doubleToLongBits(this.betaSq) == Double.doubleToLongBits(other.betaSq) && this.bitsF == other.bitsF && this.d == other.d && this.d1 == other.d1 && this.d2 == other.d2 && this.d3 == other.d3) {
                if (this.hashAlg == null) {
                    if (other.hashAlg != null) {
                        return false;
                    }
                } else if (!this.hashAlg.getAlgorithmName().equals(other.hashAlg.getAlgorithmName())) {
                    return false;
                }
                return Double.doubleToLongBits(this.normBound) == Double.doubleToLongBits(other.normBound) && Double.doubleToLongBits(this.normBoundSq) == Double.doubleToLongBits(other.normBoundSq) && this.q == other.q && this.signFailTolerance == other.signFailTolerance;
            }
            return false;
        }
        return false;
    }

    public String toString() {
        DecimalFormat format = new DecimalFormat("0.00");
        StringBuilder output = new StringBuilder("SignatureParameters(N=" + this.N + " q=" + this.q);
        output.append(" B=" + this.B + " beta=" + format.format(this.beta) + " normBound=" + format.format(this.normBound) + " hashAlg=" + this.hashAlg + ")");
        return output.toString();
    }
}