package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/* loaded from: classes.dex */
public class BEROctetString extends ASN1OctetString {
    private static final int MAX_LENGTH = 1000;
    private ASN1OctetString[] octs;

    private static byte[] toBytes(ASN1OctetString[] octs) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != octs.length; i++) {
            try {
                DEROctetString o = (DEROctetString) octs[i];
                bOut.write(o.getOctets());
            } catch (IOException e) {
                throw new IllegalArgumentException("exception converting octets " + e.toString());
            } catch (ClassCastException e2) {
                throw new IllegalArgumentException(octs[i].getClass().getName() + " found in input should only contain DEROctetString");
            }
        }
        return bOut.toByteArray();
    }

    public BEROctetString(byte[] string) {
        super(string);
    }

    public BEROctetString(ASN1OctetString[] octs) {
        super(toBytes(octs));
        this.octs = octs;
    }

    @Override // org.spongycastle.asn1.ASN1OctetString
    public byte[] getOctets() {
        return this.string;
    }

    public Enumeration getObjects() {
        return this.octs == null ? generateOcts().elements() : new Enumeration() { // from class: org.spongycastle.asn1.BEROctetString.1
            int counter = 0;

            @Override // java.util.Enumeration
            public boolean hasMoreElements() {
                return this.counter < BEROctetString.this.octs.length;
            }

            @Override // java.util.Enumeration
            public Object nextElement() {
                ASN1OctetString[] aSN1OctetStringArr = BEROctetString.this.octs;
                int i = this.counter;
                this.counter = i + 1;
                return aSN1OctetStringArr[i];
            }
        };
    }

    private Vector generateOcts() {
        int end;
        Vector vec = new Vector();
        for (int i = 0; i < this.string.length; i += 1000) {
            if (i + 1000 > this.string.length) {
                end = this.string.length;
            } else {
                end = i + 1000;
            }
            byte[] nStr = new byte[end - i];
            System.arraycopy(this.string, i, nStr, 0, nStr.length);
            vec.addElement(new DEROctetString(nStr));
        }
        return vec;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public int encodedLength() throws IOException {
        int length = 0;
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            length += ((ASN1Encodable) e.nextElement()).toASN1Primitive().encodedLength();
        }
        return length + 2 + 2;
    }

    @Override // org.spongycastle.asn1.ASN1OctetString, org.spongycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out) throws IOException {
        out.write(36);
        out.write(128);
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            out.writeObject((ASN1Encodable) e.nextElement());
        }
        out.write(0);
        out.write(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BEROctetString fromSequence(ASN1Sequence seq) {
        ASN1OctetString[] v = new ASN1OctetString[seq.size()];
        Enumeration e = seq.getObjects();
        int index = 0;
        while (e.hasMoreElements()) {
            v[index] = (ASN1OctetString) e.nextElement();
            index++;
        }
        return new BEROctetString(v);
    }
}