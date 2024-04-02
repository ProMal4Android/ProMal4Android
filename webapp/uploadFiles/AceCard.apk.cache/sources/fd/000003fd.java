package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.spongycastle.asn1.eac.CertificateBody;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class DERObjectIdentifier extends ASN1Primitive {
    private static ASN1ObjectIdentifier[][] cache = new ASN1ObjectIdentifier[255];
    private byte[] body;
    String identifier;

    public static ASN1ObjectIdentifier getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1ObjectIdentifier)) {
            return (ASN1ObjectIdentifier) obj;
        }
        if (obj instanceof DERObjectIdentifier) {
            return new ASN1ObjectIdentifier(((DERObjectIdentifier) obj).getId());
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1ObjectIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Primitive o = obj.getObject();
        return (explicit || (o instanceof DERObjectIdentifier)) ? getInstance(o) : ASN1ObjectIdentifier.fromOctetString(ASN1OctetString.getInstance(obj.getObject()).getOctets());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DERObjectIdentifier(byte[] bytes) {
        StringBuffer objId = new StringBuffer();
        long value = 0;
        BigInteger bigValue = null;
        boolean first = true;
        for (int i = 0; i != bytes.length; i++) {
            int b = bytes[i] & 255;
            if (value < 36028797018963968L) {
                value = (128 * value) + (b & CertificateBody.profileType);
                if ((b & 128) == 0) {
                    if (first) {
                        switch (((int) value) / 40) {
                            case 0:
                                objId.append('0');
                                break;
                            case 1:
                                objId.append('1');
                                value -= 40;
                                break;
                            default:
                                objId.append('2');
                                value -= 80;
                                break;
                        }
                        first = false;
                    }
                    objId.append('.');
                    objId.append(value);
                    value = 0;
                }
            } else {
                bigValue = (bigValue == null ? BigInteger.valueOf(value) : bigValue).shiftLeft(7).or(BigInteger.valueOf(b & CertificateBody.profileType));
                if ((b & 128) == 0) {
                    objId.append('.');
                    objId.append(bigValue);
                    bigValue = null;
                    value = 0;
                }
            }
        }
        this.identifier = objId.toString();
    }

    public DERObjectIdentifier(String identifier) {
        if (!isValidIdentifier(identifier)) {
            throw new IllegalArgumentException("string " + identifier + " not an OID");
        }
        this.identifier = identifier;
    }

    public String getId() {
        return this.identifier;
    }

    private void writeField(ByteArrayOutputStream out, long fieldValue) {
        byte[] result = new byte[9];
        int pos = 8;
        result[8] = (byte) (((int) fieldValue) & CertificateBody.profileType);
        while (fieldValue >= 128) {
            fieldValue >>= 7;
            pos--;
            result[pos] = (byte) ((((int) fieldValue) & CertificateBody.profileType) | 128);
        }
        out.write(result, pos, 9 - pos);
    }

    private void writeField(ByteArrayOutputStream out, BigInteger fieldValue) {
        int byteCount = (fieldValue.bitLength() + 6) / 7;
        if (byteCount == 0) {
            out.write(0);
            return;
        }
        BigInteger tmpValue = fieldValue;
        byte[] tmp = new byte[byteCount];
        for (int i = byteCount - 1; i >= 0; i--) {
            tmp[i] = (byte) ((tmpValue.intValue() & CertificateBody.profileType) | 128);
            tmpValue = tmpValue.shiftRight(7);
        }
        int i2 = byteCount - 1;
        tmp[i2] = (byte) (tmp[i2] & Byte.MAX_VALUE);
        out.write(tmp, 0, tmp.length);
    }

    private void doOutput(ByteArrayOutputStream aOut) {
        OIDTokenizer tok = new OIDTokenizer(this.identifier);
        writeField(aOut, (Integer.parseInt(tok.nextToken()) * 40) + Integer.parseInt(tok.nextToken()));
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            if (token.length() < 18) {
                writeField(aOut, Long.parseLong(token));
            } else {
                writeField(aOut, new BigInteger(token));
            }
        }
    }

    protected byte[] getBody() {
        if (this.body == null) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            doOutput(bOut);
            this.body = bOut.toByteArray();
        }
        return this.body;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int length = getBody().length;
        return StreamUtil.calculateBodyLength(length) + 1 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        byte[] enc = getBody();
        out.write(6);
        out.writeLength(enc.length);
        out.write(enc);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (o instanceof DERObjectIdentifier) {
            return this.identifier.equals(((DERObjectIdentifier) o).identifier);
        }
        return false;
    }

    public String toString() {
        return getId();
    }

    private static boolean isValidIdentifier(String identifier) {
        if (identifier.length() < 3 || identifier.charAt(1) != '.') {
            return false;
        }
        char first = identifier.charAt(0);
        if (first < '0' || first > '2') {
            return false;
        }
        boolean periodAllowed = false;
        for (int i = identifier.length() - 1; i >= 2; i--) {
            char ch2 = identifier.charAt(i);
            if ('0' <= ch2 && ch2 <= '9') {
                periodAllowed = true;
            } else {
                periodAllowed = (ch2 == '.' && periodAllowed) ? false : false;
                return false;
            }
        }
        return periodAllowed;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ASN1ObjectIdentifier fromOctetString(byte[] enc) {
        if (enc.length < 3) {
            return new ASN1ObjectIdentifier(enc);
        }
        int idx1 = enc[enc.length - 2] & 255;
        ASN1ObjectIdentifier[] first = cache[idx1];
        if (first == null) {
            first = new ASN1ObjectIdentifier[255];
            cache[idx1] = first;
        }
        int idx2 = enc[enc.length - 1] & 255;
        ASN1ObjectIdentifier possibleMatch = first[idx2];
        if (possibleMatch == null) {
            ASN1ObjectIdentifier possibleMatch2 = new ASN1ObjectIdentifier(enc);
            first[idx2] = possibleMatch2;
            return possibleMatch2;
        } else if (!Arrays.areEqual(enc, possibleMatch.getBody())) {
            int idx12 = (idx1 + 1) % 256;
            ASN1ObjectIdentifier[] first2 = cache[idx12];
            if (first2 == null) {
                first2 = new ASN1ObjectIdentifier[255];
                cache[idx12] = first2;
            }
            ASN1ObjectIdentifier possibleMatch3 = first2[idx2];
            if (possibleMatch3 == null) {
                ASN1ObjectIdentifier possibleMatch4 = new ASN1ObjectIdentifier(enc);
                first2[idx2] = possibleMatch4;
                return possibleMatch4;
            } else if (!Arrays.areEqual(enc, possibleMatch3.getBody())) {
                int idx22 = (idx2 + 1) % 256;
                ASN1ObjectIdentifier possibleMatch5 = first2[idx22];
                if (possibleMatch5 != null) {
                    return !Arrays.areEqual(enc, possibleMatch5.getBody()) ? new ASN1ObjectIdentifier(enc) : possibleMatch5;
                }
                ASN1ObjectIdentifier possibleMatch6 = new ASN1ObjectIdentifier(enc);
                first2[idx22] = possibleMatch6;
                return possibleMatch6;
            } else {
                return possibleMatch3;
            }
        } else {
            return possibleMatch;
        }
    }
}