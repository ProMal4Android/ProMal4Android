package info.guardianproject.onionkit.trust;

import android.content.Context;
import android.util.Log;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.conn.ClientConnectionOperator;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import info.guardianproject.onionkit.R;
import info.guardianproject.onionkit.proxy.MyThreadSafeClientConnManager;
import info.guardianproject.onionkit.proxy.SocksProxyClientConnOperator;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/* loaded from: classes.dex */
public class StrongHttpsClient extends DefaultHttpClient {
    private static final String TRUSTSTORE_PASSWORD = "changeit";
    private static final String TRUSTSTORE_TYPE = "BKS";
    final Context context;
    private SchemeRegistry mRegistry = new SchemeRegistry();
    private TrustManager mTrustManager;
    private HttpHost proxyHost;
    private String proxyType;
    private StrongSSLSocketFactory sFactory;

    public StrongHttpsClient(Context context) {
        this.context = context;
        this.mRegistry.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, 80, PlainSocketFactory.getSocketFactory()));
    }

    private KeyStore loadKeyStore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore trustStore = KeyStore.getInstance(TRUSTSTORE_TYPE);
        InputStream in = this.context.getResources().openRawResource(R.raw.debiancacerts);
        trustStore.load(in, TRUSTSTORE_PASSWORD.toCharArray());
        return trustStore;
    }

    public StrongHttpsClient(Context context, KeyStore keystore) {
        TrustManager[] trustManagers;
        this.context = context;
        this.mRegistry.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, 80, PlainSocketFactory.getSocketFactory()));
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
                if (trustManager instanceof X509TrustManager) {
                    this.mTrustManager = trustManagerFactory.getTrustManagers()[0];
                }
            }
            this.sFactory = new StrongSSLSocketFactory(context, this.mTrustManager, loadKeyStore(), TRUSTSTORE_PASSWORD);
            this.mRegistry.register(new Scheme("https", 443, this.sFactory));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.boye.httpclientandroidlib.impl.client.AbstractHttpClient
    public ThreadSafeClientConnManager createClientConnectionManager() {
        if (this.proxyHost == null && this.proxyType == null) {
            Log.d("StrongHTTPS", "not proxying");
            return new MyThreadSafeClientConnManager(getParams(), this.mRegistry);
        } else if (this.proxyHost != null && this.proxyType.equalsIgnoreCase("socks")) {
            Log.d("StrongHTTPS", "proxying using: " + this.proxyType);
            return new MyThreadSafeClientConnManager(getParams(), this.mRegistry) { // from class: info.guardianproject.onionkit.trust.StrongHttpsClient.1
                @Override // info.guardianproject.onionkit.proxy.MyThreadSafeClientConnManager, ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager
                protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
                    return new SocksProxyClientConnOperator(schreg, StrongHttpsClient.this.proxyHost.getHostName(), StrongHttpsClient.this.proxyHost.getPort());
                }
            };
        } else {
            Log.d("StrongHTTPS", "proxying with: " + this.proxyType);
            return new MyThreadSafeClientConnManager(getParams(), this.mRegistry);
        }
    }

    public TrustManager getTrustManager() {
        return this.mTrustManager;
    }

    public void useProxy(boolean enableTor, String type, String host, int port) {
        if (this.proxyType != null) {
            getParams().removeParameter(this.proxyType);
            this.proxyHost = null;
        }
        if (enableTor) {
            this.proxyType = type;
            HttpHost proxyHost = new HttpHost(host, port);
            getParams().setParameter(type, proxyHost);
            if (type.equalsIgnoreCase("socks")) {
                this.proxyHost = proxyHost;
            }
        }
    }
}