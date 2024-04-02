package org.spongycastle.crypto.macs;

import android.support.v4.view.MotionEventCompat;
import net.freehaven.tor.control.TorControlCommands;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithSBox;
import org.spongycastle.math.ec.Tnaf;

/* loaded from: classes.dex */
public class GOST28147Mac implements Mac {
    private int blockSize = 8;
    private int macSize = 4;
    private boolean firstStep = true;
    private int[] workingKey = null;
    private byte[] S = {9, 6, 3, 2, 8, 11, 1, 7, 10, 4, 14, TorControlCommands.SIGNAL_TERM, TorControlCommands.SIGNAL_USR2, 0, 13, 5, 3, 7, 14, 9, 8, 10, TorControlCommands.SIGNAL_TERM, 0, 5, 2, 6, TorControlCommands.SIGNAL_USR2, 11, 4, 13, 1, 14, 4, 6, 2, 11, 3, 13, 8, TorControlCommands.SIGNAL_USR2, TorControlCommands.SIGNAL_TERM, 5, 10, 0, 7, 1, 9, 14, 7, 10, TorControlCommands.SIGNAL_USR2, 13, 1, 3, 9, 0, 2, 11, 4, TorControlCommands.SIGNAL_TERM, 8, 5, 6, 11, 5, 1, 9, 8, 13, TorControlCommands.SIGNAL_TERM, 0, 14, 4, 2, 3, TorControlCommands.SIGNAL_USR2, 7, 10, 6, 3, 10, 13, TorControlCommands.SIGNAL_USR2, 1, 2, 0, 11, 7, 5, 9, 4, 8, TorControlCommands.SIGNAL_TERM, 14, 6, 1, 13, 2, 9, 7, 10, 6, 0, 8, TorControlCommands.SIGNAL_USR2, 4, 5, TorControlCommands.SIGNAL_TERM, 3, 11, 14, 11, 10, TorControlCommands.SIGNAL_TERM, 5, 0, TorControlCommands.SIGNAL_USR2, 14, 8, 6, 2, 3, 9, 1, 7, 13, 4};
    private byte[] mac = new byte[this.blockSize];
    private byte[] buf = new byte[this.blockSize];
    private int bufOff = 0;

    private int[] generateWorkingKey(byte[] userKey) {
        if (userKey.length != 32) {
            throw new IllegalArgumentException("Key length invalid. Key needs to be 32 byte - 256 bit!!!");
        }
        int[] key = new int[8];
        for (int i = 0; i != 8; i++) {
            key[i] = bytesToint(userKey, i * 4);
        }
        return key;
    }

    @Override // org.spongycastle.crypto.Mac
    public void init(CipherParameters params) throws IllegalArgumentException {
        reset();
        this.buf = new byte[this.blockSize];
        if (params instanceof ParametersWithSBox) {
            ParametersWithSBox param = (ParametersWithSBox) params;
            System.arraycopy(param.getSBox(), 0, this.S, 0, param.getSBox().length);
            if (param.getParameters() != null) {
                this.workingKey = generateWorkingKey(((KeyParameter) param.getParameters()).getKey());
            }
        } else if (params instanceof KeyParameter) {
            this.workingKey = generateWorkingKey(((KeyParameter) params).getKey());
        } else {
            throw new IllegalArgumentException("invalid parameter passed to GOST28147 init - " + params.getClass().getName());
        }
    }

    @Override // org.spongycastle.crypto.Mac
    public String getAlgorithmName() {
        return "GOST28147Mac";
    }

    @Override // org.spongycastle.crypto.Mac
    public int getMacSize() {
        return this.macSize;
    }

    private int gost28147_mainStep(int n1, int key) {
        int cm = key + n1;
        int om = (this.S[((cm >> 0) & 15) + 0] << 0) + (this.S[((cm >> 4) & 15) + 16] << 4) + (this.S[((cm >> 8) & 15) + 32] << 8) + (this.S[((cm >> 12) & 15) + 48] << TorControlCommands.SIGNAL_USR2) + (this.S[((cm >> 16) & 15) + 64] << Tnaf.POW_2_WIDTH) + (this.S[((cm >> 20) & 15) + 80] << 20) + (this.S[((cm >> 24) & 15) + 96] << 24) + (this.S[((cm >> 28) & 15) + 112] << 28);
        return (om << 11) | (om >>> 21);
    }

    private void gost28147MacFunc(int[] workingKey, byte[] in, int inOff, byte[] out, int outOff) {
        int N1 = bytesToint(in, inOff);
        int N2 = bytesToint(in, inOff + 4);
        for (int k = 0; k < 2; k++) {
            for (int j = 0; j < 8; j++) {
                int tmp = N1;
                N1 = N2 ^ gost28147_mainStep(N1, workingKey[j]);
                N2 = tmp;
            }
        }
        intTobytes(N1, out, outOff);
        intTobytes(N2, out, outOff + 4);
    }

    private int bytesToint(byte[] in, int inOff) {
        return ((in[inOff + 3] << 24) & (-16777216)) + ((in[inOff + 2] << Tnaf.POW_2_WIDTH) & 16711680) + ((in[inOff + 1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) + (in[inOff] & 255);
    }

    private void intTobytes(int num, byte[] out, int outOff) {
        out[outOff + 3] = (byte) (num >>> 24);
        out[outOff + 2] = (byte) (num >>> 16);
        out[outOff + 1] = (byte) (num >>> 8);
        out[outOff] = (byte) num;
    }

    private byte[] CM5func(byte[] buf, int bufOff, byte[] mac) {
        byte[] sum = new byte[buf.length - bufOff];
        System.arraycopy(buf, bufOff, sum, 0, mac.length);
        for (int i = 0; i != mac.length; i++) {
            sum[i] = (byte) (sum[i] ^ mac[i]);
        }
        return sum;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte in) throws IllegalStateException {
        if (this.bufOff == this.buf.length) {
            byte[] sumbuf = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, sumbuf, 0, this.mac.length);
            if (this.firstStep) {
                this.firstStep = false;
            } else {
                sumbuf = CM5func(this.buf, 0, this.mac);
            }
            gost28147MacFunc(this.workingKey, sumbuf, 0, this.mac, 0);
            this.bufOff = 0;
        }
        byte[] bArr = this.buf;
        int i = this.bufOff;
        this.bufOff = i + 1;
        bArr[i] = in;
    }

    @Override // org.spongycastle.crypto.Mac
    public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
        if (len < 0) {
            throw new IllegalArgumentException("Can't have a negative input length!");
        }
        int gapLen = this.blockSize - this.bufOff;
        if (len > gapLen) {
            System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
            byte[] sumbuf = new byte[this.buf.length];
            System.arraycopy(this.buf, 0, sumbuf, 0, this.mac.length);
            if (this.firstStep) {
                this.firstStep = false;
            } else {
                sumbuf = CM5func(this.buf, 0, this.mac);
            }
            gost28147MacFunc(this.workingKey, sumbuf, 0, this.mac, 0);
            this.bufOff = 0;
            len -= gapLen;
            inOff += gapLen;
            while (len > this.blockSize) {
                gost28147MacFunc(this.workingKey, CM5func(in, inOff, this.mac), 0, this.mac, 0);
                len -= this.blockSize;
                inOff += this.blockSize;
            }
        }
        System.arraycopy(in, inOff, this.buf, this.bufOff, len);
        this.bufOff += len;
    }

    @Override // org.spongycastle.crypto.Mac
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        while (this.bufOff < this.blockSize) {
            this.buf[this.bufOff] = 0;
            this.bufOff++;
        }
        byte[] sumbuf = new byte[this.buf.length];
        System.arraycopy(this.buf, 0, sumbuf, 0, this.mac.length);
        if (this.firstStep) {
            this.firstStep = false;
        } else {
            sumbuf = CM5func(this.buf, 0, this.mac);
        }
        gost28147MacFunc(this.workingKey, sumbuf, 0, this.mac, 0);
        System.arraycopy(this.mac, (this.mac.length / 2) - this.macSize, out, outOff, this.macSize);
        reset();
        return this.macSize;
    }

    @Override // org.spongycastle.crypto.Mac
    public void reset() {
        for (int i = 0; i < this.buf.length; i++) {
            this.buf[i] = 0;
        }
        this.bufOff = 0;
        this.firstStep = true;
    }
}