package org.spongycastle.crypto.paddings;

import java.security.SecureRandom;
import org.spongycastle.crypto.InvalidCipherTextException;

/* loaded from: classes.dex */
public class PKCS7Padding implements BlockCipherPadding {
    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public String getPaddingName() {
        return "PKCS7";
    }

    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public int addPadding(byte[] in, int inOff) {
        byte code = (byte) (in.length - inOff);
        while (inOff < in.length) {
            in[inOff] = code;
            inOff++;
        }
        return code;
    }

    @Override // org.spongycastle.crypto.paddings.BlockCipherPadding
    public int padCount(byte[] in) throws InvalidCipherTextException {
        int count = in[in.length - 1] & 255;
        if (count > in.length || count == 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        for (int i = 1; i <= count; i++) {
            if (in[in.length - i] != count) {
                throw new InvalidCipherTextException("pad block corrupted");
            }
        }
        return count;
    }
}