package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.MaxBytesExceededException;
import org.spongycastle.crypto.StreamCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.util.Pack;
import org.spongycastle.util.Strings;

/* loaded from: classes.dex */
public class Salsa20Engine implements StreamCipher {
    private static final int STATE_SIZE = 16;
    private static final byte[] sigma = Strings.toByteArray("expand 32-byte k");
    private static final byte[] tau = Strings.toByteArray("expand 16-byte k");
    private int cW0;
    private int cW1;
    private int cW2;
    private int index = 0;
    private int[] engineState = new int[16];
    private int[] x = new int[16];
    private byte[] keyStream = new byte[64];
    private byte[] workingKey = null;
    private byte[] workingIV = null;
    private boolean initialised = false;

    @Override // org.spongycastle.crypto.StreamCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("Salsa20 Init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV) params;
        byte[] iv = ivParams.getIV();
        if (iv == null || iv.length != 8) {
            throw new IllegalArgumentException("Salsa20 requires exactly 8 bytes of IV");
        }
        if (!(ivParams.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("Salsa20 Init parameters must include a key");
        }
        KeyParameter key = (KeyParameter) ivParams.getParameters();
        this.workingKey = key.getKey();
        this.workingIV = iv;
        setKey(this.workingKey, this.workingIV);
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public String getAlgorithmName() {
        return "Salsa20";
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public byte returnByte(byte in) {
        if (limitExceeded()) {
            throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV");
        }
        if (this.index == 0) {
            generateKeyStream(this.keyStream);
            int[] iArr = this.engineState;
            int i = iArr[8] + 1;
            iArr[8] = i;
            if (i == 0) {
                int[] iArr2 = this.engineState;
                iArr2[9] = iArr2[9] + 1;
            }
        }
        byte out = (byte) (this.keyStream[this.index] ^ in);
        this.index = (this.index + 1) & 63;
        return out;
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (!this.initialised) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new DataLengthException("output buffer too short");
        }
        if (limitExceeded(len)) {
            throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV");
        }
        for (int i = 0; i < len; i++) {
            if (this.index == 0) {
                generateKeyStream(this.keyStream);
                int[] iArr = this.engineState;
                int i2 = iArr[8] + 1;
                iArr[8] = i2;
                if (i2 == 0) {
                    int[] iArr2 = this.engineState;
                    iArr2[9] = iArr2[9] + 1;
                }
            }
            out[i + outOff] = (byte) (this.keyStream[this.index] ^ in[i + inOff]);
            this.index = (this.index + 1) & 63;
        }
    }

    @Override // org.spongycastle.crypto.StreamCipher
    public void reset() {
        setKey(this.workingKey, this.workingIV);
    }

    private void setKey(byte[] keyBytes, byte[] ivBytes) {
        byte[] constants;
        this.workingKey = keyBytes;
        this.workingIV = ivBytes;
        this.index = 0;
        resetCounter();
        int offset = 0;
        this.engineState[1] = Pack.littleEndianToInt(this.workingKey, 0);
        this.engineState[2] = Pack.littleEndianToInt(this.workingKey, 4);
        this.engineState[3] = Pack.littleEndianToInt(this.workingKey, 8);
        this.engineState[4] = Pack.littleEndianToInt(this.workingKey, 12);
        if (this.workingKey.length == 32) {
            constants = sigma;
            offset = 16;
        } else {
            constants = tau;
        }
        this.engineState[11] = Pack.littleEndianToInt(this.workingKey, offset);
        this.engineState[12] = Pack.littleEndianToInt(this.workingKey, offset + 4);
        this.engineState[13] = Pack.littleEndianToInt(this.workingKey, offset + 8);
        this.engineState[14] = Pack.littleEndianToInt(this.workingKey, offset + 12);
        this.engineState[0] = Pack.littleEndianToInt(constants, 0);
        this.engineState[5] = Pack.littleEndianToInt(constants, 4);
        this.engineState[10] = Pack.littleEndianToInt(constants, 8);
        this.engineState[15] = Pack.littleEndianToInt(constants, 12);
        this.engineState[6] = Pack.littleEndianToInt(this.workingIV, 0);
        this.engineState[7] = Pack.littleEndianToInt(this.workingIV, 4);
        int[] iArr = this.engineState;
        this.engineState[9] = 0;
        iArr[8] = 0;
        this.initialised = true;
    }

    private void generateKeyStream(byte[] output) {
        salsaCore(20, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, output, 0);
    }

    public static void salsaCore(int rounds, int[] input, int[] x) {
        System.arraycopy(input, 0, x, 0, input.length);
        for (int i = rounds; i > 0; i -= 2) {
            x[4] = x[4] ^ rotl(x[0] + x[12], 7);
            x[8] = x[8] ^ rotl(x[4] + x[0], 9);
            x[12] = x[12] ^ rotl(x[8] + x[4], 13);
            x[0] = x[0] ^ rotl(x[12] + x[8], 18);
            x[9] = x[9] ^ rotl(x[5] + x[1], 7);
            x[13] = x[13] ^ rotl(x[9] + x[5], 9);
            x[1] = x[1] ^ rotl(x[13] + x[9], 13);
            x[5] = x[5] ^ rotl(x[1] + x[13], 18);
            x[14] = x[14] ^ rotl(x[10] + x[6], 7);
            x[2] = x[2] ^ rotl(x[14] + x[10], 9);
            x[6] = x[6] ^ rotl(x[2] + x[14], 13);
            x[10] = x[10] ^ rotl(x[6] + x[2], 18);
            x[3] = x[3] ^ rotl(x[15] + x[11], 7);
            x[7] = x[7] ^ rotl(x[3] + x[15], 9);
            x[11] = x[11] ^ rotl(x[7] + x[3], 13);
            x[15] = x[15] ^ rotl(x[11] + x[7], 18);
            x[1] = x[1] ^ rotl(x[0] + x[3], 7);
            x[2] = x[2] ^ rotl(x[1] + x[0], 9);
            x[3] = x[3] ^ rotl(x[2] + x[1], 13);
            x[0] = x[0] ^ rotl(x[3] + x[2], 18);
            x[6] = x[6] ^ rotl(x[5] + x[4], 7);
            x[7] = x[7] ^ rotl(x[6] + x[5], 9);
            x[4] = x[4] ^ rotl(x[7] + x[6], 13);
            x[5] = x[5] ^ rotl(x[4] + x[7], 18);
            x[11] = x[11] ^ rotl(x[10] + x[9], 7);
            x[8] = x[8] ^ rotl(x[11] + x[10], 9);
            x[9] = x[9] ^ rotl(x[8] + x[11], 13);
            x[10] = x[10] ^ rotl(x[9] + x[8], 18);
            x[12] = x[12] ^ rotl(x[15] + x[14], 7);
            x[13] = x[13] ^ rotl(x[12] + x[15], 9);
            x[14] = x[14] ^ rotl(x[13] + x[12], 13);
            x[15] = x[15] ^ rotl(x[14] + x[13], 18);
        }
        for (int i2 = 0; i2 < 16; i2++) {
            x[i2] = x[i2] + input[i2];
        }
    }

    private static int rotl(int x, int y) {
        return (x << y) | (x >>> (-y));
    }

    private void resetCounter() {
        this.cW0 = 0;
        this.cW1 = 0;
        this.cW2 = 0;
    }

    private boolean limitExceeded() {
        int i = this.cW0 + 1;
        this.cW0 = i;
        if (i == 0) {
            int i2 = this.cW1 + 1;
            this.cW1 = i2;
            if (i2 == 0) {
                int i3 = this.cW2 + 1;
                this.cW2 = i3;
                return (i3 & 32) != 0;
            }
            return false;
        }
        return false;
    }

    private boolean limitExceeded(int len) {
        this.cW0 += len;
        if (this.cW0 >= len || this.cW0 < 0) {
            return false;
        }
        int i = this.cW1 + 1;
        this.cW1 = i;
        if (i == 0) {
            int i2 = this.cW2 + 1;
            this.cW2 = i2;
            return (i2 & 32) != 0;
        }
        return false;
    }
}