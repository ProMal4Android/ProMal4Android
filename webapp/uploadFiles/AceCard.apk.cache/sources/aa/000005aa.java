package org.spongycastle.asn1.x509.qualified;

import com.baseapp.Constants;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* loaded from: classes.dex */
public interface ETSIQCObjectIdentifiers {
    public static final ASN1ObjectIdentifier id_etsi_qcs = new ASN1ObjectIdentifier("0.4.0.1862.1");
    public static final ASN1ObjectIdentifier id_etsi_qcs_QcCompliance = id_etsi_qcs.branch(Constants.CLIENT_NUMBER);
    public static final ASN1ObjectIdentifier id_etsi_qcs_LimiteValue = id_etsi_qcs.branch("2");
    public static final ASN1ObjectIdentifier id_etsi_qcs_RetentionPeriod = id_etsi_qcs.branch("3");
    public static final ASN1ObjectIdentifier id_etsi_qcs_QcSSCD = id_etsi_qcs.branch("4");
}