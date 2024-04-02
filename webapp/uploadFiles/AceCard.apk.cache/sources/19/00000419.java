package org.spongycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.eac.CertificateBody;

/* loaded from: classes.dex */
class StreamUtil {
    private static final long MAX_MEMORY = Runtime.getRuntime().maxMemory();

    StreamUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findLimit(InputStream in) {
        if (in instanceof LimitedInputStream) {
            return ((LimitedInputStream) in).getRemaining();
        }
        if (in instanceof ASN1InputStream) {
            return ((ASN1InputStream) in).getLimit();
        }
        if (in instanceof ByteArrayInputStream) {
            return ((ByteArrayInputStream) in).available();
        }
        if (in instanceof FileInputStream) {
            try {
                long size = ((FileInputStream) in).getChannel().size();
                if (size < 2147483647L) {
                    return (int) size;
                }
            } catch (IOException e) {
            }
        }
        if (MAX_MEMORY > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int) MAX_MEMORY;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int calculateBodyLength(int length) {
        int count = 1;
        if (length > 127) {
            int size = 1;
            int val = length;
            while (true) {
                val >>>= 8;
                if (val == 0) {
                    break;
                }
                size++;
            }
            for (int i = (size - 1) * 8; i >= 0; i -= 8) {
                count++;
            }
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int calculateTagLength(int tagNo) throws IOException {
        if (tagNo < 31) {
            return 1;
        }
        if (tagNo < 128) {
            int length = 1 + 1;
            return length;
        }
        byte[] stack = new byte[5];
        int pos = stack.length - 1;
        stack[pos] = (byte) (tagNo & CertificateBody.profileType);
        do {
            tagNo >>= 7;
            pos--;
            stack[pos] = (byte) ((tagNo & CertificateBody.profileType) | 128);
        } while (tagNo > 127);
        int length2 = 1 + (stack.length - pos);
        return length2;
    }
}