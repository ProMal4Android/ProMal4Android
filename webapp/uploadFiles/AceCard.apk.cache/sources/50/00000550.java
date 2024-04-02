package org.spongycastle.asn1.util;

import java.io.FileInputStream;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Primitive;

/* loaded from: classes.dex */
public class Dump {
    public static void main(String[] args) throws Exception {
        FileInputStream fIn = new FileInputStream(args[0]);
        ASN1InputStream bIn = new ASN1InputStream(fIn);
        while (true) {
            ASN1Primitive obj = bIn.readObject();
            if (obj != null) {
                System.out.println(ASN1Dump.dumpAsString(obj));
            } else {
                return;
            }
        }
    }
}