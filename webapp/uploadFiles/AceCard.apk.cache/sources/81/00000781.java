package org.spongycastle.util.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

/* loaded from: classes.dex */
public class FixedSecureRandom extends SecureRandom {
    private byte[] _data;
    private int _index;
    private int _intPad;

    public FixedSecureRandom(byte[] value) {
        this(false, new byte[][]{value});
    }

    public FixedSecureRandom(byte[][] values) {
        this(false, values);
    }

    public FixedSecureRandom(boolean intPad, byte[] value) {
        this(intPad, new byte[][]{value});
    }

    public FixedSecureRandom(boolean intPad, byte[][] values) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != values.length; i++) {
            try {
                bOut.write(values[i]);
            } catch (IOException e) {
                throw new IllegalArgumentException("can't save value array.");
            }
        }
        this._data = bOut.toByteArray();
        if (intPad) {
            this._intPad = this._data.length % 4;
        }
    }

    @Override // java.security.SecureRandom, java.util.Random
    public void nextBytes(byte[] bytes) {
        System.arraycopy(this._data, this._index, bytes, 0, bytes.length);
        this._index += bytes.length;
    }

    @Override // java.util.Random
    public int nextInt() {
        int val = 0 | (nextValue() << 24) | (nextValue() << 16);
        if (this._intPad == 2) {
            this._intPad--;
        } else {
            val |= nextValue() << 8;
        }
        if (this._intPad == 1) {
            this._intPad--;
            return val;
        }
        return val | nextValue();
    }

    @Override // java.util.Random
    public long nextLong() {
        long val = 0 | (nextValue() << 56);
        return val | (nextValue() << 48) | (nextValue() << 40) | (nextValue() << 32) | (nextValue() << 24) | (nextValue() << 16) | (nextValue() << 8) | nextValue();
    }

    public boolean isExhausted() {
        return this._index == this._data.length;
    }

    private int nextValue() {
        byte[] bArr = this._data;
        int i = this._index;
        this._index = i + 1;
        return bArr[i] & 255;
    }
}