package org.spongycastle.asn1.crmf;

import com.baseapp.Constants;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;

/* loaded from: classes.dex */
public interface CRMFObjectIdentifiers {
    public static final ASN1ObjectIdentifier id_pkix = new ASN1ObjectIdentifier("1.3.6.1.5.5.7");
    public static final ASN1ObjectIdentifier id_pkip = id_pkix.branch("5");
    public static final ASN1ObjectIdentifier id_regCtrl = id_pkip.branch(Constants.CLIENT_NUMBER);
    public static final ASN1ObjectIdentifier id_regCtrl_regToken = id_regCtrl.branch(Constants.CLIENT_NUMBER);
    public static final ASN1ObjectIdentifier id_regCtrl_authenticator = id_regCtrl.branch("2");
    public static final ASN1ObjectIdentifier id_regCtrl_pkiPublicationInfo = id_regCtrl.branch("3");
    public static final ASN1ObjectIdentifier id_regCtrl_pkiArchiveOptions = id_regCtrl.branch("4");
    public static final ASN1ObjectIdentifier id_ct_encKeyWithID = new ASN1ObjectIdentifier(PKCSObjectIdentifiers.id_ct + ".21");
}