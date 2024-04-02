package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import net.freehaven.tor.control.TorControlCommands;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.io.Streams;

/* loaded from: classes.dex */
public class DERBitString extends ASN1Primitive implements ASN1String {
    private static final char[] table = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    protected byte[] data;
    protected int padBits;

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getPadBits(int bitString) {
        int val = 0;
        int i = 3;
        while (true) {
            if (i < 0) {
                break;
            } else if (i != 0) {
                if ((bitString >> (i * 8)) == 0) {
                    i--;
                } else {
                    val = (bitString >> (i * 8)) & 255;
                    break;
                }
            } else if (bitString == 0) {
                i--;
            } else {
                val = bitString & 255;
                break;
            }
        }
        if (val == 0) {
            return 7;
        }
        int bits = 1;
        while (true) {
            val <<= 1;
            if ((val & 255) != 0) {
                bits++;
            } else {
                return 8 - bits;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static byte[] getBytes(int bitString) {
        int bytes = 4;
        for (int i = 3; i >= 1 && ((255 << (i * 8)) & bitString) == 0; i--) {
            bytes--;
        }
        byte[] result = new byte[bytes];
        for (int i2 = 0; i2 < bytes; i2++) {
            result[i2] = (byte) ((bitString >> (i2 * 8)) & 255);
        }
        return result;
    }

    public static DERBitString getInstance(Object obj) {
        if (obj == null || (obj instanceof DERBitString)) {
            return (DERBitString) obj;
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static DERBitString getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        return (explicit || (o instanceof DERBitString)) ? getInstance(o) : fromOctetString(((ASN1OctetString) o).getOctets());
    }

    protected DERBitString(byte data, int padBits) {
        this.data = new byte[1];
        this.data[0] = data;
        this.padBits = padBits;
    }

    public DERBitString(byte[] data, int padBits) {
        this.data = data;
        this.padBits = padBits;
    }

    public DERBitString(byte[] data) {
        this(data, 0);
    }

    public DERBitString(ASN1Encodable obj) {
        try {
            this.data = obj.toASN1Primitive().getEncoded(ASN1Encoding.DER);
            this.padBits = 0;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error processing object : " + e.toString());
        }
    }

    public byte[] getBytes() {
        return this.data;
    }

    public int getPadBits() {
        return this.padBits;
    }

    public int intValue() {
        int value = 0;
        for (int i = 0; i != this.data.length && i != 4; i++) {
            value |= (this.data[i] & 255) << (i * 8);
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() {
        return StreamUtil.calculateBodyLength(this.data.length + 1) + 1 + this.data.length + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        byte[] bytes = new byte[getBytes().length + 1];
        bytes[0] = (byte) getPadBits();
        System.arraycopy(getBytes(), 0, bytes, 1, bytes.length - 1);
        out.writeEncoded(3, bytes);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return this.padBits ^ Arrays.hashCode(this.data);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    protected boolean asn1Equals(ASN1Primitive o) {
        if (o instanceof DERBitString) {
            DERBitString other = (DERBitString) o;
            return this.padBits == other.padBits && Arrays.areEqual(this.data, other.data);
        }
        return false;
    }

    @Override // org.spongycastle.asn1.ASN1String
    public String getString() {
        StringBuffer buf = new StringBuffer("#");
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = new ASN1OutputStream(bOut);
        try {
            aOut.writeObject(this);
            byte[] string = bOut.toByteArray();
            for (int i = 0; i != string.length; i++) {
                buf.append(table[(string[i] >>> 4) & 15]);
                buf.append(table[string[i] & TorControlCommands.SIGNAL_TERM]);
            }
            return buf.toString();
        } catch (IOException e) {
            throw new RuntimeException("internal error encoding BitString");
        }
    }

    public String toString() {
        return getString();
    }

    static DERBitString fromOctetString(byte[] bytes) {
        if (bytes.length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        int padBits = bytes[0];
        byte[] data = new byte[bytes.length - 1];
        if (data.length != 0) {
            System.arraycopy(bytes, 1, data, 0, bytes.length - 1);
        }
        return new DERBitString(data, padBits);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DERBitString fromInputStream(int length, InputStream stream) throws IOException {
        if (length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        int padBits = stream.read();
        byte[] data = new byte[length - 1];
        if (data.length != 0 && Streams.readFully(stream, data) != data.length) {
            throw new EOFException("EOF encountered in middle of BIT STRING");
        }
        return new DERBitString(data, padBits);
    }
}