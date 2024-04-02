package org.spongycastle.asn1.esf;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.asn1.x509.NoticeReference;

/* loaded from: classes.dex */
public class SPUserNotice extends ASN1Object {
    private DisplayText explicitText;
    private NoticeReference noticeRef;

    public static SPUserNotice getInstance(Object obj) {
        if (obj instanceof SPUserNotice) {
            return (SPUserNotice) obj;
        }
        if (obj != null) {
            return new SPUserNotice(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private SPUserNotice(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1Encodable object = (ASN1Encodable) e.nextElement();
            if (object instanceof NoticeReference) {
                this.noticeRef = NoticeReference.getInstance(object);
            } else if (object instanceof DisplayText) {
                this.explicitText = DisplayText.getInstance(object);
            } else {
                throw new IllegalArgumentException("Invalid element in 'SPUserNotice'.");
            }
        }
    }

    public SPUserNotice(NoticeReference noticeRef, DisplayText explicitText) {
        this.noticeRef = noticeRef;
        this.explicitText = explicitText;
    }

    public NoticeReference getNoticeRef() {
        return this.noticeRef;
    }

    public DisplayText getExplicitText() {
        return this.explicitText;
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.noticeRef != null) {
            v.add(this.noticeRef);
        }
        if (this.explicitText != null) {
            v.add(this.explicitText);
        }
        return new DERSequence(v);
    }
}