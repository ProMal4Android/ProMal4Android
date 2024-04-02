package org.spongycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

/* loaded from: classes.dex */
public abstract class ASN1Sequence extends ASN1Primitive {
    protected Vector seq = new Vector();

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    public static ASN1Sequence getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Sequence)) {
            return (ASN1Sequence) obj;
        }
        if (obj instanceof ASN1SequenceParser) {
            return getInstance(((ASN1SequenceParser) obj).toASN1Primitive());
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(fromByteArray((byte[]) obj));
            } catch (IOException e) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + e.getMessage());
            }
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable) obj).toASN1Primitive();
            if (primitive instanceof ASN1Sequence) {
                return (ASN1Sequence) primitive;
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Sequence getInstance(ASN1TaggedObject obj, boolean explicit) {
        if (explicit) {
            if (!obj.isExplicit()) {
                throw new IllegalArgumentException("object implicit - explicit expected.");
            }
            return getInstance(obj.getObject().toASN1Primitive());
        } else if (obj.isExplicit()) {
            if (obj instanceof BERTaggedObject) {
                return new BERSequence(obj.getObject());
            }
            return new DLSequence(obj.getObject());
        } else if (obj.getObject() instanceof ASN1Sequence) {
            return (ASN1Sequence) obj.getObject();
        } else {
            throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence(ASN1Encodable obj) {
        this.seq.addElement(obj);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence(ASN1EncodableVector v) {
        for (int i = 0; i != v.size(); i++) {
            this.seq.addElement(v.get(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ASN1Sequence(ASN1Encodable[] array) {
        for (int i = 0; i != array.length; i++) {
            this.seq.addElement(array[i]);
        }
    }

    public ASN1Encodable[] toArray() {
        ASN1Encodable[] values = new ASN1Encodable[size()];
        for (int i = 0; i != size(); i++) {
            values[i] = getObjectAt(i);
        }
        return values;
    }

    public Enumeration getObjects() {
        return this.seq.elements();
    }

    public ASN1SequenceParser parser() {
        return new ASN1SequenceParser() { // from class: org.spongycastle.asn1.ASN1Sequence.1
            private int index;
            private final int max;

            {
                this.max = ASN1Sequence.this.size();
            }

            @Override // org.spongycastle.asn1.ASN1SequenceParser
            public ASN1Encodable readObject() throws IOException {
                if (this.index == this.max) {
                    return null;
                }
                ASN1Sequence aSN1Sequence = ASN1Sequence.this;
                int i = this.index;
                this.index = i + 1;
                ASN1Encodable obj = aSN1Sequence.getObjectAt(i);
                if (obj instanceof ASN1Sequence) {
                    return ((ASN1Sequence) obj).parser();
                }
                if (obj instanceof ASN1Set) {
                    return ((ASN1Set) obj).parser();
                }
                return obj;
            }

            @Override // org.spongycastle.asn1.InMemoryRepresentable
            public ASN1Primitive getLoadedObject() {
                return outer;
            }

            @Override // org.spongycastle.asn1.ASN1Encodable
            public ASN1Primitive toASN1Primitive() {
                return outer;
            }
        };
    }

    public ASN1Encodable getObjectAt(int index) {
        return (ASN1Encodable) this.seq.elementAt(index);
    }

    public int size() {
        return this.seq.size();
    }

    @Override // org.spongycastle.asn1.ASN1Primitive, org.spongycastle.asn1.ASN1Object
    public int hashCode() {
        Enumeration e = getObjects();
        int hashCode = size();
        while (e.hasMoreElements()) {
            ASN1Encodable o = getNext(e);
            hashCode = (hashCode * 17) ^ o.hashCode();
        }
        return hashCode;
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean asn1Equals(ASN1Primitive o) {
        if (o instanceof ASN1Sequence) {
            ASN1Sequence other = (ASN1Sequence) o;
            if (size() == other.size()) {
                Enumeration s1 = getObjects();
                Enumeration s2 = other.getObjects();
                while (s1.hasMoreElements()) {
                    ASN1Encodable obj1 = getNext(s1);
                    ASN1Encodable obj2 = getNext(s2);
                    ASN1Primitive o1 = obj1.toASN1Primitive();
                    ASN1Primitive o2 = obj2.toASN1Primitive();
                    if (o1 != o2 && !o1.equals(o2)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    private ASN1Encodable getNext(Enumeration e) {
        ASN1Encodable encObj = (ASN1Encodable) e.nextElement();
        return encObj;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public ASN1Primitive toDERObject() {
        ASN1Sequence derSeq = new DERSequence();
        derSeq.seq = this.seq;
        return derSeq;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public ASN1Primitive toDLObject() {
        ASN1Sequence dlSeq = new DLSequence();
        dlSeq.seq = this.seq;
        return dlSeq;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.spongycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return true;
    }

    public String toString() {
        return this.seq.toString();
    }
}