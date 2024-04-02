package org.spongycastle.crypto.util;

/* loaded from: classes.dex */
public abstract class Pack {
    public static int bigEndianToInt(byte[] bs, int off) {
        int n = bs[off] << 24;
        int off2 = off + 1;
        int off3 = off2 + 1;
        return n | ((bs[off2] & 255) << 16) | ((bs[off3] & 255) << 8) | (bs[off3 + 1] & 255);
    }

    public static void bigEndianToInt(byte[] bs, int off, int[] ns) {
        for (int i = 0; i < ns.length; i++) {
            ns[i] = bigEndianToInt(bs, off);
            off += 4;
        }
    }

    public static void intToBigEndian(int n, byte[] bs, int off) {
        bs[off] = (byte) (n >>> 24);
        int off2 = off + 1;
        bs[off2] = (byte) (n >>> 16);
        int off3 = off2 + 1;
        bs[off3] = (byte) (n >>> 8);
        bs[off3 + 1] = (byte) n;
    }

    public static void intToBigEndian(int[] ns, byte[] bs, int off) {
        for (int i : ns) {
            intToBigEndian(i, bs, off);
            off += 4;
        }
    }

    public static long bigEndianToLong(byte[] bs, int off) {
        int hi = bigEndianToInt(bs, off);
        int lo = bigEndianToInt(bs, off + 4);
        return ((hi & 4294967295L) << 32) | (lo & 4294967295L);
    }

    public static void longToBigEndian(long n, byte[] bs, int off) {
        intToBigEndian((int) (n >>> 32), bs, off);
        intToBigEndian((int) (4294967295L & n), bs, off + 4);
    }

    public static int littleEndianToInt(byte[] bs, int off) {
        int n = bs[off] & 255;
        int off2 = off + 1;
        int off3 = off2 + 1;
        return n | ((bs[off2] & 255) << 8) | ((bs[off3] & 255) << 16) | (bs[off3 + 1] << 24);
    }

    public static void littleEndianToInt(byte[] bs, int off, int[] ns) {
        for (int i = 0; i < ns.length; i++) {
            ns[i] = littleEndianToInt(bs, off);
            off += 4;
        }
    }

    public static void intToLittleEndian(int n, byte[] bs, int off) {
        bs[off] = (byte) n;
        int off2 = off + 1;
        bs[off2] = (byte) (n >>> 8);
        int off3 = off2 + 1;
        bs[off3] = (byte) (n >>> 16);
        bs[off3 + 1] = (byte) (n >>> 24);
    }

    public static void intToLittleEndian(int[] ns, byte[] bs, int off) {
        for (int i : ns) {
            intToLittleEndian(i, bs, off);
            off += 4;
        }
    }

    public static long littleEndianToLong(byte[] bs, int off) {
        int lo = littleEndianToInt(bs, off);
        int hi = littleEndianToInt(bs, off + 4);
        return ((hi & 4294967295L) << 32) | (lo & 4294967295L);
    }

    public static void longToLittleEndian(long n, byte[] bs, int off) {
        intToLittleEndian((int) (4294967295L & n), bs, off);
        intToLittleEndian((int) (n >>> 32), bs, off + 4);
    }
}