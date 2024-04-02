package org.spongycastle.crypto.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.x509.X509CertificateStructure;

/* loaded from: classes.dex */
public class Certificate {
    public static final Certificate EMPTY_CHAIN = new Certificate(new X509CertificateStructure[0]);
    protected X509CertificateStructure[] certs;

    /* JADX INFO: Access modifiers changed from: protected */
    public static Certificate parse(InputStream is) throws IOException {
        int left = TlsUtils.readUint24(is);
        if (left == 0) {
            return EMPTY_CHAIN;
        }
        Vector tmp = new Vector();
        while (left > 0) {
            int size = TlsUtils.readUint24(is);
            left -= size + 3;
            byte[] buf = new byte[size];
            TlsUtils.readFully(buf, is);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ASN1InputStream ais = new ASN1InputStream(bis);
            ASN1Primitive o = ais.readObject();
            tmp.addElement(X509CertificateStructure.getInstance(o));
            if (bis.available() > 0) {
                throw new IllegalArgumentException("Sorry, there is garbage data left after the certificate");
            }
        }
        X509CertificateStructure[] certs = new X509CertificateStructure[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
            certs[i] = (X509CertificateStructure) tmp.elementAt(i);
        }
        return new Certificate(certs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void encode(OutputStream os) throws IOException {
        Vector encCerts = new Vector();
        int totalSize = 0;
        for (int i = 0; i < this.certs.length; i++) {
            byte[] encCert = this.certs[i].getEncoded(ASN1Encoding.DER);
            encCerts.addElement(encCert);
            totalSize += encCert.length + 3;
        }
        TlsUtils.writeUint24(totalSize, os);
        for (int i2 = 0; i2 < encCerts.size(); i2++) {
            TlsUtils.writeOpaque24((byte[]) encCerts.elementAt(i2), os);
        }
    }

    public Certificate(X509CertificateStructure[] certs) {
        if (certs == null) {
            throw new IllegalArgumentException("'certs' cannot be null");
        }
        this.certs = certs;
    }

    public X509CertificateStructure[] getCerts() {
        X509CertificateStructure[] result = new X509CertificateStructure[this.certs.length];
        System.arraycopy(this.certs, 0, result, 0, this.certs.length);
        return result;
    }

    public boolean isEmpty() {
        return this.certs.length == 0;
    }
}