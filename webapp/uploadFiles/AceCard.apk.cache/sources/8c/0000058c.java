package org.spongycastle.asn1.x509;

import net.freehaven.tor.control.TorControlCommands;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA1Digest;

/* loaded from: classes.dex */
public class SubjectKeyIdentifier extends ASN1Object {
    private byte[] keyidentifier;

    public static SubjectKeyIdentifier getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(ASN1OctetString.getInstance(obj, explicit));
    }

    public static SubjectKeyIdentifier getInstance(Object obj) {
        if (obj instanceof SubjectKeyIdentifier) {
            return (SubjectKeyIdentifier) obj;
        }
        if (obj != null) {
            return new SubjectKeyIdentifier(ASN1OctetString.getInstance(obj));
        }
        return null;
    }

    public SubjectKeyIdentifier(byte[] keyid) {
        this.keyidentifier = keyid;
    }

    protected SubjectKeyIdentifier(ASN1OctetString keyid) {
        this.keyidentifier = keyid.getOctets();
    }

    public byte[] getKeyIdentifier() {
        return this.keyidentifier;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.keyidentifier);
    }

    public SubjectKeyIdentifier(SubjectPublicKeyInfo spki) {
        this.keyidentifier = getDigest(spki);
    }

    public static SubjectKeyIdentifier createSHA1KeyIdentifier(SubjectPublicKeyInfo keyInfo) {
        return new SubjectKeyIdentifier(keyInfo);
    }

    public static SubjectKeyIdentifier createTruncatedSHA1KeyIdentifier(SubjectPublicKeyInfo keyInfo) {
        byte[] dig = getDigest(keyInfo);
        byte[] id = new byte[8];
        System.arraycopy(dig, dig.length - 8, id, 0, id.length);
        id[0] = (byte) (id[0] & TorControlCommands.SIGNAL_TERM);
        id[0] = (byte) (id[0] | 64);
        return new SubjectKeyIdentifier(id);
    }

    private static byte[] getDigest(SubjectPublicKeyInfo spki) {
        Digest digest = new SHA1Digest();
        byte[] resBuf = new byte[digest.getDigestSize()];
        byte[] bytes = spki.getPublicKeyData().getBytes();
        digest.update(bytes, 0, bytes.length);
        digest.doFinal(resBuf, 0);
        return resBuf;
    }
}