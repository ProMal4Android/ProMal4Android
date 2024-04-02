package org.spongycastle.asn1.util;

import java.io.IOException;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BERApplicationSpecific;
import org.spongycastle.asn1.BERConstructedOctetString;
import org.spongycastle.asn1.BERSequence;
import org.spongycastle.asn1.BERSet;
import org.spongycastle.asn1.BERTaggedObject;
import org.spongycastle.asn1.DERApplicationSpecific;
import org.spongycastle.asn1.DERBMPString;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERBoolean;
import org.spongycastle.asn1.DEREnumerated;
import org.spongycastle.asn1.DERExternal;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERPrintableString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.DERT61String;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.DERUTCTime;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.DERVisibleString;
import org.spongycastle.util.encoders.Hex;

/* loaded from: classes.dex */
public class ASN1Dump {
    private static final int SAMPLE_SIZE = 32;
    private static final String TAB = "    ";

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void _dumpAsString(String indent, boolean verbose, ASN1Primitive obj, StringBuffer buf) {
        String nl = System.getProperty("line.separator");
        if (obj instanceof ASN1Sequence) {
            Enumeration e = ((ASN1Sequence) obj).getObjects();
            String tab = indent + TAB;
            buf.append(indent);
            if (obj instanceof BERSequence) {
                buf.append("BER Sequence");
            } else if (obj instanceof DERSequence) {
                buf.append("DER Sequence");
            } else {
                buf.append("Sequence");
            }
            buf.append(nl);
            while (e.hasMoreElements()) {
                Object o = e.nextElement();
                if (o == null || o.equals(new DERNull())) {
                    buf.append(tab);
                    buf.append("NULL");
                    buf.append(nl);
                } else if (o instanceof ASN1Primitive) {
                    _dumpAsString(tab, verbose, (ASN1Primitive) o, buf);
                } else {
                    _dumpAsString(tab, verbose, ((ASN1Encodable) o).toASN1Primitive(), buf);
                }
            }
        } else if (obj instanceof DERTaggedObject) {
            String tab2 = indent + TAB;
            buf.append(indent);
            if (obj instanceof BERTaggedObject) {
                buf.append("BER Tagged [");
            } else {
                buf.append("Tagged [");
            }
            DERTaggedObject o2 = (DERTaggedObject) obj;
            buf.append(Integer.toString(o2.getTagNo()));
            buf.append(']');
            if (!o2.isExplicit()) {
                buf.append(" IMPLICIT ");
            }
            buf.append(nl);
            if (o2.isEmpty()) {
                buf.append(tab2);
                buf.append("EMPTY");
                buf.append(nl);
                return;
            }
            _dumpAsString(tab2, verbose, o2.getObject(), buf);
        } else if (obj instanceof BERSet) {
            Enumeration e2 = ((ASN1Set) obj).getObjects();
            String tab3 = indent + TAB;
            buf.append(indent);
            buf.append("BER Set");
            buf.append(nl);
            while (e2.hasMoreElements()) {
                Object o3 = e2.nextElement();
                if (o3 == null) {
                    buf.append(tab3);
                    buf.append("NULL");
                    buf.append(nl);
                } else if (o3 instanceof ASN1Primitive) {
                    _dumpAsString(tab3, verbose, (ASN1Primitive) o3, buf);
                } else {
                    _dumpAsString(tab3, verbose, ((ASN1Encodable) o3).toASN1Primitive(), buf);
                }
            }
        } else if (obj instanceof DERSet) {
            Enumeration e3 = ((ASN1Set) obj).getObjects();
            String tab4 = indent + TAB;
            buf.append(indent);
            buf.append("DER Set");
            buf.append(nl);
            while (e3.hasMoreElements()) {
                Object o4 = e3.nextElement();
                if (o4 == null) {
                    buf.append(tab4);
                    buf.append("NULL");
                    buf.append(nl);
                } else if (o4 instanceof ASN1Primitive) {
                    _dumpAsString(tab4, verbose, (ASN1Primitive) o4, buf);
                } else {
                    _dumpAsString(tab4, verbose, ((ASN1Encodable) o4).toASN1Primitive(), buf);
                }
            }
        } else if (obj instanceof ASN1ObjectIdentifier) {
            buf.append(indent + "ObjectIdentifier(" + ((ASN1ObjectIdentifier) obj).getId() + ")" + nl);
        } else if (obj instanceof DERBoolean) {
            buf.append(indent + "Boolean(" + ((DERBoolean) obj).isTrue() + ")" + nl);
        } else if (obj instanceof ASN1Integer) {
            buf.append(indent + "Integer(" + ((ASN1Integer) obj).getValue() + ")" + nl);
        } else if (obj instanceof BERConstructedOctetString) {
            ASN1OctetString oct = (ASN1OctetString) obj;
            buf.append(indent + "BER Constructed Octet String[" + oct.getOctets().length + "] ");
            if (verbose) {
                buf.append(dumpBinaryDataAsString(indent, oct.getOctets()));
            } else {
                buf.append(nl);
            }
        } else if (obj instanceof DEROctetString) {
            ASN1OctetString oct2 = (ASN1OctetString) obj;
            buf.append(indent + "DER Octet String[" + oct2.getOctets().length + "] ");
            if (verbose) {
                buf.append(dumpBinaryDataAsString(indent, oct2.getOctets()));
            } else {
                buf.append(nl);
            }
        } else if (obj instanceof DERBitString) {
            DERBitString bt = (DERBitString) obj;
            buf.append(indent + "DER Bit String[" + bt.getBytes().length + ", " + bt.getPadBits() + "] ");
            if (verbose) {
                buf.append(dumpBinaryDataAsString(indent, bt.getBytes()));
            } else {
                buf.append(nl);
            }
        } else if (obj instanceof DERIA5String) {
            buf.append(indent + "IA5String(" + ((DERIA5String) obj).getString() + ") " + nl);
        } else if (obj instanceof DERUTF8String) {
            buf.append(indent + "UTF8String(" + ((DERUTF8String) obj).getString() + ") " + nl);
        } else if (obj instanceof DERPrintableString) {
            buf.append(indent + "PrintableString(" + ((DERPrintableString) obj).getString() + ") " + nl);
        } else if (obj instanceof DERVisibleString) {
            buf.append(indent + "VisibleString(" + ((DERVisibleString) obj).getString() + ") " + nl);
        } else if (obj instanceof DERBMPString) {
            buf.append(indent + "BMPString(" + ((DERBMPString) obj).getString() + ") " + nl);
        } else if (obj instanceof DERT61String) {
            buf.append(indent + "T61String(" + ((DERT61String) obj).getString() + ") " + nl);
        } else if (obj instanceof DERUTCTime) {
            buf.append(indent + "UTCTime(" + ((DERUTCTime) obj).getTime() + ") " + nl);
        } else if (obj instanceof DERGeneralizedTime) {
            buf.append(indent + "GeneralizedTime(" + ((DERGeneralizedTime) obj).getTime() + ") " + nl);
        } else if (obj instanceof BERApplicationSpecific) {
            buf.append(outputApplicationSpecific(ASN1Encoding.BER, indent, verbose, obj, nl));
        } else if (obj instanceof DERApplicationSpecific) {
            buf.append(outputApplicationSpecific(ASN1Encoding.DER, indent, verbose, obj, nl));
        } else if (obj instanceof DEREnumerated) {
            DEREnumerated en = (DEREnumerated) obj;
            buf.append(indent + "DER Enumerated(" + en.getValue() + ")" + nl);
        } else if (obj instanceof DERExternal) {
            DERExternal ext = (DERExternal) obj;
            buf.append(indent + "External " + nl);
            String tab5 = indent + TAB;
            if (ext.getDirectReference() != null) {
                buf.append(tab5 + "Direct Reference: " + ext.getDirectReference().getId() + nl);
            }
            if (ext.getIndirectReference() != null) {
                buf.append(tab5 + "Indirect Reference: " + ext.getIndirectReference().toString() + nl);
            }
            if (ext.getDataValueDescriptor() != null) {
                _dumpAsString(tab5, verbose, ext.getDataValueDescriptor(), buf);
            }
            buf.append(tab5 + "Encoding: " + ext.getEncoding() + nl);
            _dumpAsString(tab5, verbose, ext.getExternalContent(), buf);
        } else {
            buf.append(indent + obj.toString() + nl);
        }
    }

    private static String outputApplicationSpecific(String type, String indent, boolean verbose, ASN1Primitive obj, String nl) {
        DERApplicationSpecific app = (DERApplicationSpecific) obj;
        StringBuffer buf = new StringBuffer();
        if (app.isConstructed()) {
            try {
                ASN1Sequence s = ASN1Sequence.getInstance(app.getObject(16));
                buf.append(indent + type + " ApplicationSpecific[" + app.getApplicationTag() + "]" + nl);
                Enumeration e = s.getObjects();
                while (e.hasMoreElements()) {
                    _dumpAsString(indent + TAB, verbose, (ASN1Primitive) e.nextElement(), buf);
                }
            } catch (IOException e2) {
                buf.append(e2);
            }
            return buf.toString();
        }
        return indent + type + " ApplicationSpecific[" + app.getApplicationTag() + "] (" + new String(Hex.encode(app.getContents())) + ")" + nl;
    }

    public static String dumpAsString(Object obj) {
        return dumpAsString(obj, false);
    }

    public static String dumpAsString(Object obj, boolean verbose) {
        StringBuffer buf = new StringBuffer();
        if (obj instanceof ASN1Primitive) {
            _dumpAsString("", verbose, (ASN1Primitive) obj, buf);
        } else if (obj instanceof ASN1Encodable) {
            _dumpAsString("", verbose, ((ASN1Encodable) obj).toASN1Primitive(), buf);
        } else {
            return "unknown object type " + obj.toString();
        }
        return buf.toString();
    }

    private static String dumpBinaryDataAsString(String indent, byte[] bytes) {
        String nl = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();
        String indent2 = indent + TAB;
        buf.append(nl);
        for (int i = 0; i < bytes.length; i += 32) {
            if (bytes.length - i > 32) {
                buf.append(indent2);
                buf.append(new String(Hex.encode(bytes, i, 32)));
                buf.append(TAB);
                buf.append(calculateAscString(bytes, i, 32));
                buf.append(nl);
            } else {
                buf.append(indent2);
                buf.append(new String(Hex.encode(bytes, i, bytes.length - i)));
                for (int j = bytes.length - i; j != 32; j++) {
                    buf.append("  ");
                }
                buf.append(TAB);
                buf.append(calculateAscString(bytes, i, bytes.length - i));
                buf.append(nl);
            }
        }
        return buf.toString();
    }

    private static String calculateAscString(byte[] bytes, int off, int len) {
        StringBuffer buf = new StringBuffer();
        for (int i = off; i != off + len; i++) {
            if (bytes[i] >= 32 && bytes[i] <= 126) {
                buf.append((char) bytes[i]);
            }
        }
        return buf.toString();
    }
}