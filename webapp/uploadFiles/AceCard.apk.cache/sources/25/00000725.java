package org.spongycastle.crypto.tls;

/* loaded from: classes.dex */
public class TlsNullCipher implements TlsCipher {
    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] encodePlaintext(short type, byte[] plaintext, int offset, int len) {
        return copyData(plaintext, offset, len);
    }

    @Override // org.spongycastle.crypto.tls.TlsCipher
    public byte[] decodeCiphertext(short type, byte[] ciphertext, int offset, int len) {
        return copyData(ciphertext, offset, len);
    }

    protected byte[] copyData(byte[] text, int offset, int len) {
        byte[] result = new byte[len];
        System.arraycopy(text, offset, result, 0, len);
        return result;
    }
}