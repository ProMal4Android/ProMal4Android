package org.spongycastle.crypto.generators;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

/* loaded from: classes.dex */
public class PKCS5S2ParametersGenerator extends PBEParametersGenerator {
    private Mac hMac;

    public PKCS5S2ParametersGenerator() {
        this(new SHA1Digest());
    }

    public PKCS5S2ParametersGenerator(Digest digest) {
        this.hMac = new HMac(digest);
    }

    private void F(byte[] P, byte[] S, int c, byte[] iBuf, byte[] out, int outOff) {
        byte[] state = new byte[this.hMac.getMacSize()];
        CipherParameters param = new KeyParameter(P);
        this.hMac.init(param);
        if (S != null) {
            this.hMac.update(S, 0, S.length);
        }
        this.hMac.update(iBuf, 0, iBuf.length);
        this.hMac.doFinal(state, 0);
        System.arraycopy(state, 0, out, outOff, state.length);
        if (c == 0) {
            throw new IllegalArgumentException("iteration count must be at least 1.");
        }
        for (int count = 1; count < c; count++) {
            this.hMac.init(param);
            this.hMac.update(state, 0, state.length);
            this.hMac.doFinal(state, 0);
            for (int j = 0; j != state.length; j++) {
                int i = outOff + j;
                out[i] = (byte) (out[i] ^ state[j]);
            }
        }
    }

    private void intToOctet(byte[] buf, int i) {
        buf[0] = (byte) (i >>> 24);
        buf[1] = (byte) (i >>> 16);
        buf[2] = (byte) (i >>> 8);
        buf[3] = (byte) i;
    }

    private byte[] generateDerivedKey(int dkLen) {
        int hLen = this.hMac.getMacSize();
        int l = ((dkLen + hLen) - 1) / hLen;
        byte[] iBuf = new byte[4];
        byte[] out = new byte[l * hLen];
        for (int i = 1; i <= l; i++) {
            intToOctet(iBuf, i);
            F(this.password, this.salt, this.iterationCount, iBuf, out, (i - 1) * hLen);
        }
        return out;
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize) {
        int keySize2 = keySize / 8;
        byte[] dKey = generateDerivedKey(keySize2);
        return new KeyParameter(dKey, 0, keySize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
        int keySize2 = keySize / 8;
        int ivSize2 = ivSize / 8;
        byte[] dKey = generateDerivedKey(keySize2 + ivSize2);
        return new ParametersWithIV(new KeyParameter(dKey, 0, keySize2), dKey, keySize2, ivSize2);
    }

    @Override // org.spongycastle.crypto.PBEParametersGenerator
    public CipherParameters generateDerivedMacParameters(int keySize) {
        return generateDerivedParameters(keySize);
    }
}