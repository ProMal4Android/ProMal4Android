package org.spongycastle.crypto.agreement.kdf;

import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERObjectIdentifier;
import org.spongycastle.crypto.DerivationParameters;

/* loaded from: classes.dex */
public class DHKDFParameters implements DerivationParameters {
    private ASN1ObjectIdentifier algorithm;
    private byte[] extraInfo;
    private int keySize;
    private byte[] z;

    public DHKDFParameters(DERObjectIdentifier algorithm, int keySize, byte[] z) {
        this(algorithm, keySize, z, null);
    }

    public DHKDFParameters(DERObjectIdentifier algorithm, int keySize, byte[] z, byte[] extraInfo) {
        this.algorithm = new ASN1ObjectIdentifier(algorithm.getId());
        this.keySize = keySize;
        this.z = z;
        this.extraInfo = extraInfo;
    }

    public ASN1ObjectIdentifier getAlgorithm() {
        return this.algorithm;
    }

    public int getKeySize() {
        return this.keySize;
    }

    public byte[] getZ() {
        return this.z;
    }

    public byte[] getExtraInfo() {
        return this.extraInfo;
    }
}