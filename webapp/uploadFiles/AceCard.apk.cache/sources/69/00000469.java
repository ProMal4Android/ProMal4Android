package org.spongycastle.asn1.cms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import org.spongycastle.asn1.ASN1Choice;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERGeneralizedTime;
import org.spongycastle.asn1.DERUTCTime;

/* loaded from: classes.dex */
public class Time extends ASN1Object implements ASN1Choice {
    ASN1Primitive time;

    public static Time getInstance(ASN1TaggedObject obj, boolean explicit) {
        return getInstance(obj.getObject());
    }

    public Time(ASN1Primitive time) {
        if (!(time instanceof DERUTCTime) && !(time instanceof DERGeneralizedTime)) {
            throw new IllegalArgumentException("unknown object passed to Time");
        }
        this.time = time;
    }

    public Time(Date date) {
        SimpleTimeZone tz = new SimpleTimeZone(0, "Z");
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss");
        dateF.setTimeZone(tz);
        String d = dateF.format(date) + "Z";
        int year = Integer.parseInt(d.substring(0, 4));
        if (year < 1950 || year > 2049) {
            this.time = new DERGeneralizedTime(d);
        } else {
            this.time = new DERUTCTime(d.substring(2));
        }
    }

    public static Time getInstance(Object obj) {
        if (obj == null || (obj instanceof Time)) {
            return (Time) obj;
        }
        if (obj instanceof DERUTCTime) {
            return new Time((DERUTCTime) obj);
        }
        if (obj instanceof DERGeneralizedTime) {
            return new Time((DERGeneralizedTime) obj);
        }
        throw new IllegalArgumentException("unknown object in factory: " + obj.getClass().getName());
    }

    public String getTime() {
        return this.time instanceof DERUTCTime ? ((DERUTCTime) this.time).getAdjustedTime() : ((DERGeneralizedTime) this.time).getTime();
    }

    public Date getDate() {
        try {
            return this.time instanceof DERUTCTime ? ((DERUTCTime) this.time).getAdjustedDate() : ((DERGeneralizedTime) this.time).getDate();
        } catch (ParseException e) {
            throw new IllegalStateException("invalid date string: " + e.getMessage());
        }
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return this.time;
    }
}