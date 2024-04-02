package ch.boye.httpclientandroidlib.conn.ssl;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.conn.util.InetAddressUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

@Immutable
/* loaded from: classes.dex */
public abstract class AbstractVerifier implements X509HostnameVerifier {
    private static final String[] BAD_COUNTRY_2LDS = {"ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org"};

    static {
        Arrays.sort(BAD_COUNTRY_2LDS);
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.X509HostnameVerifier
    public final void verify(String host, SSLSocket ssl) throws IOException {
        if (host == null) {
            throw new NullPointerException("host to verify is null");
        }
        SSLSession session = ssl.getSession();
        if (session == null) {
            InputStream in = ssl.getInputStream();
            in.available();
            session = ssl.getSession();
            if (session == null) {
                ssl.startHandshake();
                session = ssl.getSession();
            }
        }
        Certificate[] certs = session.getPeerCertificates();
        X509Certificate x509 = (X509Certificate) certs[0];
        verify(host, x509);
    }

    @Override // javax.net.ssl.HostnameVerifier
    public final boolean verify(String host, SSLSession session) {
        try {
            Certificate[] certs = session.getPeerCertificates();
            X509Certificate x509 = (X509Certificate) certs[0];
            verify(host, x509);
            return true;
        } catch (SSLException e) {
            return false;
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.X509HostnameVerifier
    public final void verify(String host, X509Certificate cert) throws SSLException {
        String[] cns = getCNs(cert);
        String[] subjectAlts = getSubjectAlts(cert, host);
        verify(host, cns, subjectAlts);
    }

    public final void verify(String host, String[] cns, String[] subjectAlts, boolean strictWithSubDomains) throws SSLException {
        LinkedList<String> names = new LinkedList<>();
        if (cns != null && cns.length > 0 && cns[0] != null) {
            names.add(cns[0]);
        }
        if (subjectAlts != null) {
            for (String subjectAlt : subjectAlts) {
                if (subjectAlt != null) {
                    names.add(subjectAlt);
                }
            }
        }
        if (names.isEmpty()) {
            String msg = "Certificate for <" + host + "> doesn't contain CN or DNS subjectAlt";
            throw new SSLException(msg);
        }
        StringBuilder buf = new StringBuilder();
        String hostName = host.trim().toLowerCase(Locale.US);
        boolean match = false;
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            String cn = it.next().toLowerCase(Locale.US);
            buf.append(" <");
            buf.append(cn);
            buf.append('>');
            if (it.hasNext()) {
                buf.append(" OR");
            }
            String[] parts = cn.split("\\.");
            boolean doWildcard = parts.length >= 3 && parts[0].endsWith("*") && acceptableCountryWildcard(cn) && !isIPAddress(host);
            if (doWildcard) {
                String firstpart = parts[0];
                if (firstpart.length() > 1) {
                    String prefix = firstpart.substring(0, firstpart.length() - 1);
                    String suffix = cn.substring(firstpart.length());
                    String hostSuffix = hostName.substring(prefix.length());
                    match = hostName.startsWith(prefix) && hostSuffix.endsWith(suffix);
                } else {
                    match = hostName.endsWith(cn.substring(1));
                }
                if (match && strictWithSubDomains) {
                    if (countDots(hostName) == countDots(cn)) {
                        match = true;
                        continue;
                    } else {
                        match = false;
                        continue;
                    }
                }
            } else {
                match = hostName.equals(cn);
                continue;
            }
            if (match) {
                break;
            }
        }
        if (!match) {
            throw new SSLException("hostname in certificate didn't match: <" + host + "> !=" + ((Object) buf));
        }
    }

    public static boolean acceptableCountryWildcard(String cn) {
        String[] parts = cn.split("\\.");
        return (parts.length == 3 && parts[2].length() == 2 && Arrays.binarySearch(BAD_COUNTRY_2LDS, parts[1]) >= 0) ? false : true;
    }

    public static String[] getCNs(X509Certificate cert) {
        LinkedList<String> cnList = new LinkedList<>();
        String subjectPrincipal = cert.getSubjectX500Principal().toString();
        StringTokenizer st = new StringTokenizer(subjectPrincipal, ",");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken().trim();
            if (tok.length() > 3 && tok.substring(0, 3).equalsIgnoreCase("CN=")) {
                cnList.add(tok.substring(3));
            }
        }
        if (cnList.isEmpty()) {
            return null;
        }
        String[] cns = new String[cnList.size()];
        cnList.toArray(cns);
        return cns;
    }

    private static String[] getSubjectAlts(X509Certificate cert, String hostname) {
        int subjectType;
        if (isIPAddress(hostname)) {
            subjectType = 7;
        } else {
            subjectType = 2;
        }
        LinkedList<String> subjectAltList = new LinkedList<>();
        Collection<List<?>> c = null;
        try {
            c = cert.getSubjectAlternativeNames();
        } catch (CertificateParsingException e) {
        }
        if (c != null) {
            for (List<?> aC : c) {
                int type = ((Integer) aC.get(0)).intValue();
                if (type == subjectType) {
                    String s = (String) aC.get(1);
                    subjectAltList.add(s);
                }
            }
        }
        if (subjectAltList.isEmpty()) {
            return null;
        }
        String[] subjectAlts = new String[subjectAltList.size()];
        subjectAltList.toArray(subjectAlts);
        return subjectAlts;
    }

    public static String[] getDNSSubjectAlts(X509Certificate cert) {
        return getSubjectAlts(cert, null);
    }

    public static int countDots(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '.') {
                count++;
            }
        }
        return count;
    }

    private static boolean isIPAddress(String hostname) {
        return hostname != null && (InetAddressUtils.isIPv4Address(hostname) || InetAddressUtils.isIPv6Address(hostname));
    }
}