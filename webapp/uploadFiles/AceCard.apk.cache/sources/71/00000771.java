package org.spongycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: classes.dex */
public class HexEncoder implements Encoder {
    protected final byte[] encodingTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    protected final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        for (int i = 0; i < this.encodingTable.length; i++) {
            this.decodingTable[this.encodingTable[i]] = (byte) i;
        }
        this.decodingTable[65] = this.decodingTable[97];
        this.decodingTable[66] = this.decodingTable[98];
        this.decodingTable[67] = this.decodingTable[99];
        this.decodingTable[68] = this.decodingTable[100];
        this.decodingTable[69] = this.decodingTable[101];
        this.decodingTable[70] = this.decodingTable[102];
    }

    public HexEncoder() {
        initialiseDecodingTable();
    }

    @Override // org.spongycastle.util.encoders.Encoder
    public int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        for (int i = off; i < off + length; i++) {
            int v = data[i] & 255;
            out.write(this.encodingTable[v >>> 4]);
            out.write(this.encodingTable[v & 15]);
        }
        return length * 2;
    }

    private boolean ignore(char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }

    @Override // org.spongycastle.util.encoders.Encoder
    public int decode(byte[] data, int off, int length, OutputStream out) throws IOException {
        int outLen = 0;
        int end = off + length;
        while (end > off && ignore((char) data[end - 1])) {
            end--;
        }
        int i = off;
        while (i < end) {
            int i2 = i;
            while (i2 < end && ignore((char) data[i2])) {
                i2++;
            }
            byte b1 = this.decodingTable[data[i2]];
            int i3 = i2 + 1;
            while (i3 < end && ignore((char) data[i3])) {
                i3++;
            }
            i = i3 + 1;
            byte b2 = this.decodingTable[data[i3]];
            out.write((b1 << 4) | b2);
            outLen++;
        }
        return outLen;
    }

    @Override // org.spongycastle.util.encoders.Encoder
    public int decode(String data, OutputStream out) throws IOException {
        int length = 0;
        int end = data.length();
        while (end > 0 && ignore(data.charAt(end - 1))) {
            end--;
        }
        int i = 0;
        while (i < end) {
            int i2 = i;
            while (i2 < end && ignore(data.charAt(i2))) {
                i2++;
            }
            byte b1 = this.decodingTable[data.charAt(i2)];
            int i3 = i2 + 1;
            while (i3 < end && ignore(data.charAt(i3))) {
                i3++;
            }
            i = i3 + 1;
            byte b2 = this.decodingTable[data.charAt(i3)];
            out.write((b1 << 4) | b2);
            length++;
        }
        return length;
    }
}