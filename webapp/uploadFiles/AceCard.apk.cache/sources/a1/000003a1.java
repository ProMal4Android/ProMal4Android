package info.guardianproject.onionkit.trust;

import android.content.Context;
import ch.boye.httpclientandroidlib.conn.scheme.LayeredSchemeSocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.params.HttpParams;
import java.io.IOException;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

/* loaded from: classes.dex */
public class StrongSSLSocketFactory extends SSLSocketFactory implements LayeredSchemeSocketFactory {
    public static final String SSL = "SSL";
    public static final String SSLV2 = "SSLv2";
    public static final String TLS = "TLS";
    private javax.net.ssl.SSLSocketFactory mFactory;
    private Proxy mProxy;
    private TrustManager mTrustManager;

    public StrongSSLSocketFactory(Context context, TrustManager trustManager, KeyStore kStore, String kStorePasswd) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        super(kStore);
        this.mFactory = null;
        this.mProxy = null;
        this.mTrustManager = trustManager;
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager[] tm = {this.mTrustManager};
        KeyManager[] km = createKeyManagers(kStore, kStorePasswd);
        sslContext.init(km, tm, new SecureRandom());
        this.mFactory = sslContext.getSocketFactory();
    }

    public TrustManager getStrongTrustManager() {
        return this.mTrustManager;
    }

    private KeyManager[] createKeyManagers(KeyStore keystore, String password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        if (keystore == null) {
            throw new IllegalArgumentException("Keystore may not be null");
        }
        KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmfactory.init(keystore, password != null ? password.toCharArray() : null);
        return kmfactory.getKeyManagers();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory, ch.boye.httpclientandroidlib.conn.scheme.SocketFactory
    public Socket createSocket() throws IOException {
        return this.mFactory.createSocket();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory, ch.boye.httpclientandroidlib.conn.scheme.LayeredSocketFactory
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return this.mFactory.createSocket(socket, host, port, autoClose);
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory, ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory
    public boolean isSecure(Socket sock) throws IllegalArgumentException {
        return sock instanceof SSLSocket;
    }

    public void setProxy(Proxy proxy) {
        this.mProxy = proxy;
    }

    public Proxy getProxy() {
        return this.mProxy;
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory, ch.boye.httpclientandroidlib.conn.scheme.SchemeSocketFactory
    public Socket createSocket(HttpParams arg0) throws IOException {
        return this.mFactory.createSocket();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory, ch.boye.httpclientandroidlib.conn.scheme.LayeredSchemeSocketFactory
    public Socket createLayeredSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException, UnknownHostException {
        return ((LayeredSchemeSocketFactory) this.mFactory).createLayeredSocket(arg0, arg1, arg2, arg3);
    }
}