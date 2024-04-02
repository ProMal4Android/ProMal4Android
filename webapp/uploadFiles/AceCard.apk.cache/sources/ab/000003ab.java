package info.guardianproject.onionkit.web;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import info.guardianproject.onionkit.ui.OrbotHelper;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.http.HttpHost;

/* loaded from: classes.dex */
public class WebkitProxy {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8118;
    private static final int DEFAULT_SOCKS_PORT = 9050;
    private static final int REQUEST_CODE = 0;
    private static final String TAG = "OrbotHelpher";

    public static void setProxy(Context ctx) throws Exception {
        setProxy(ctx, DEFAULT_HOST, 8118);
    }

    public static boolean setProxy(Context ctx, String host, int port) throws Exception {
        setSystemProperties(host, port);
        if (Build.VERSION.SDK_INT < 14) {
            boolean worked = setWebkitProxyGingerbread(ctx, host, port);
            return worked;
        } else if (Build.VERSION.SDK_INT < 19) {
            boolean worked2 = setWebkitProxyICS(ctx, host, port);
            return worked2;
        } else {
            setWebkitProxyICS(ctx, host, port);
            boolean worked3 = setKitKatProxy(ctx, host, port);
            return worked3;
        }
    }

    private static void setSystemProperties(String host, int port) {
        System.setProperty("proxyHost", host);
        System.setProperty("proxyPort", new StringBuilder(String.valueOf(port)).toString());
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", new StringBuilder(String.valueOf(port)).toString());
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", new StringBuilder(String.valueOf(port)).toString());
        System.setProperty("socks.proxyHost", host);
        System.setProperty("socks.proxyPort", "9050");
        System.setProperty("socksProxyHost", host);
        System.setProperty("socksProxyPort", "9050");
    }

    private static boolean setWebkitProxyGingerbread(Context ctx, String host, int port) throws Exception {
        Object requestQueueObject = getRequestQueue(ctx);
        if (requestQueueObject != null) {
            HttpHost httpHost = new HttpHost(host, port, ch.boye.httpclientandroidlib.HttpHost.DEFAULT_SCHEME_NAME);
            setDeclaredField(requestQueueObject, "mProxyHost", httpHost);
            return true;
        }
        return false;
    }

    private static boolean setWebkitProxyICS(Context ctx, String host, int port) {
        try {
            Class webViewCoreClass = Class.forName("android.webkit.WebViewCore");
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (webViewCoreClass != null && proxyPropertiesClass != null) {
                Method m = webViewCoreClass.getDeclaredMethod("sendStaticMessage", Integer.TYPE, Object.class);
                Constructor c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE, String.class);
                if (m != null && c != null) {
                    m.setAccessible(true);
                    c.setAccessible(true);
                    Object properties = c.newInstance(host, Integer.valueOf(port), null);
                    m.invoke(null, 193, properties);
                    return true;
                }
            }
        } catch (Error e) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.webkit.Network: " + e.toString());
        } catch (Exception e2) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.net.ProxyProperties: " + e2.toString());
        }
        return false;
    }

    private static boolean sendProxyChangedIntent(Context ctx, String host, int port) {
        Constructor c;
        try {
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (proxyPropertiesClass != null && (c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE, String.class)) != null) {
                c.setAccessible(true);
                Object properties = c.newInstance(host, Integer.valueOf(port), null);
                Intent intent = new Intent("android.intent.action.PROXY_CHANGE");
                intent.putExtra("proxy", (Parcelable) properties);
                ctx.sendBroadcast(intent);
            }
        } catch (Error e) {
            Log.e("ProxySettings", "Exception sending Intent ", e);
        } catch (Exception e2) {
            Log.e("ProxySettings", "Exception sending Intent ", e2);
        }
        return false;
    }

    private static boolean setKitKatProxy0(Context ctx, String host, int port) {
        Constructor c;
        try {
            Class cmClass = Class.forName("android.net.ConnectivityManager");
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (cmClass != null && proxyPropertiesClass != null && (c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE, String.class)) != null) {
                c.setAccessible(true);
                Object proxyProps = c.newInstance(host, Integer.valueOf(port), null);
                ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService("connectivity");
                Method mSetGlobalProxy = cmClass.getDeclaredMethod("setGlobalProxy", proxyPropertiesClass);
                mSetGlobalProxy.invoke(cm, proxyProps);
                return true;
            }
        } catch (Exception e) {
            Log.e("ProxySettings", "ConnectivityManager.setGlobalProxy ", e);
        }
        return false;
    }

    private static boolean setKitKatProxy2(Context ctx, String host, int port) {
        Method[] declaredMethods;
        try {
            Class webViewCoreClass = Class.forName("org.chromium.content.common.CommandLine");
            if (webViewCoreClass != null) {
                for (Method method : webViewCoreClass.getDeclaredMethods()) {
                    Log.d("Orweb", "Proxy methods: " + method.getName());
                }
                Method m = webViewCoreClass.getDeclaredMethod("initFromFile", String.class);
                if (m != null) {
                    m.setAccessible(true);
                    m.invoke(null, "/data/local/tmp/orweb.conf");
                    return true;
                }
                return false;
            }
        } catch (Error e) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.webkit.Network: " + e.toString());
        } catch (Exception e2) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.net.ProxyProperties: " + e2.toString());
        }
        return false;
    }

    private static boolean setKitKatProxy(Context ctx, String host, int port) {
        Method[] declaredMethods;
        try {
            Class webViewCoreClass = Class.forName("android.net.Proxy");
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (webViewCoreClass != null && proxyPropertiesClass != null) {
                for (Method method : webViewCoreClass.getDeclaredMethods()) {
                    Log.d("Orweb", "Proxy methods: " + method.getName());
                }
                Method m = webViewCoreClass.getDeclaredMethod("setHttpProxySystemProperty", proxyPropertiesClass);
                Constructor c = proxyPropertiesClass.getConstructor(String.class, Integer.TYPE, String.class);
                if (m != null && c != null) {
                    m.setAccessible(true);
                    c.setAccessible(true);
                    Object properties = c.newInstance(host, Integer.valueOf(port), null);
                    m.invoke(null, properties);
                    return true;
                }
                return false;
            }
        } catch (Error e) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.webkit.Network: " + e.toString());
        } catch (Exception e2) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.net.ProxyProperties: " + e2.toString());
        }
        return false;
    }

    private static boolean resetProxyForKitKat() {
        Method[] declaredMethods;
        try {
            Class webViewCoreClass = Class.forName("android.net.Proxy");
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (webViewCoreClass != null && proxyPropertiesClass != null) {
                for (Method method : webViewCoreClass.getDeclaredMethods()) {
                    Log.d("Orweb", "Proxy methods: " + method.getName());
                }
                Method m = webViewCoreClass.getDeclaredMethod("setHttpProxySystemProperty", proxyPropertiesClass);
                if (m != null) {
                    m.setAccessible(true);
                    m.invoke(null, null);
                    return true;
                }
                return false;
            }
        } catch (Error e) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.webkit.Network: " + e.toString());
        } catch (Exception e2) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.net.ProxyProperties: " + e2.toString());
        }
        return false;
    }

    public static void resetProxy(Context ctx) throws Exception {
        if (Build.VERSION.SDK_INT < 14) {
            resetProxyForGingerBread(ctx);
            return;
        }
        resetProxyForICS();
        resetProxyForKitKat();
    }

    private static void resetProxyForICS() throws Exception {
        Method m;
        try {
            Class webViewCoreClass = Class.forName("android.webkit.WebViewCore");
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            if (webViewCoreClass != null && proxyPropertiesClass != null && (m = webViewCoreClass.getDeclaredMethod("sendStaticMessage", Integer.TYPE, Object.class)) != null) {
                m.setAccessible(true);
                m.invoke(null, 193, null);
            }
        } catch (Error e) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.webkit.Network: " + e.toString());
            throw e;
        } catch (Exception e2) {
            Log.e("ProxySettings", "Exception setting WebKit proxy through android.net.ProxyProperties: " + e2.toString());
            throw e2;
        }
    }

    private static void resetProxyForGingerBread(Context ctx) throws Exception {
        Object requestQueueObject = getRequestQueue(ctx);
        if (requestQueueObject != null) {
            setDeclaredField(requestQueueObject, "mProxyHost", null);
        }
    }

    public static Object getRequestQueue(Context ctx) throws Exception {
        Object networkObj;
        Class networkClass = Class.forName("android.webkit.Network");
        if (networkClass == null || (networkObj = invokeMethod(networkClass, "getInstance", new Object[]{ctx}, Context.class)) == null) {
            return null;
        }
        Object ret = getDeclaredField(networkObj, "mRequestQueue");
        return ret;
    }

    private static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    private static void setDeclaredField(Object obj, String name, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private static Object invokeMethod(Object object, String methodName, Object[] params, Class... types) throws Exception {
        Class c = object instanceof Class ? (Class) object : object.getClass();
        if (types != null) {
            Method method = c.getMethod(methodName, types);
            Object out = method.invoke(object, params);
            return out;
        }
        Method method2 = c.getMethod(methodName, new Class[0]);
        Object out2 = method2.invoke(object, new Object[0]);
        return out2;
    }

    public static Socket getSocket(Context context, String proxyHost, int proxyPort) throws IOException {
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress(proxyHost, proxyPort), 10000);
        return sock;
    }

    public static Socket getSocket(Context context) throws IOException {
        return getSocket(context, DEFAULT_HOST, 9050);
    }

    public static AlertDialog initOrbot(Activity activity, CharSequence stringTitle, CharSequence stringMessage, CharSequence stringButtonYes, CharSequence stringButtonNo, CharSequence stringDesiredBarcodeFormats) {
        Intent intentScan = new Intent(OrbotHelper.ACTION_START_TOR);
        intentScan.addCategory("android.intent.category.DEFAULT");
        try {
            activity.startActivityForResult(intentScan, 0);
            return null;
        } catch (ActivityNotFoundException e) {
            return showDownloadDialog(activity, stringTitle, stringMessage, stringButtonYes, stringButtonNo);
        }
    }

    private static AlertDialog showDownloadDialog(final Activity activity, CharSequence stringTitle, CharSequence stringMessage, CharSequence stringButtonYes, CharSequence stringButtonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(stringTitle);
        downloadDialog.setMessage(stringMessage);
        downloadDialog.setPositiveButton(stringButtonYes, new DialogInterface.OnClickListener() { // from class: info.guardianproject.onionkit.web.WebkitProxy.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:org.torproject.android");
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                activity.startActivity(intent);
            }
        });
        downloadDialog.setNegativeButton(stringButtonNo, new DialogInterface.OnClickListener() { // from class: info.guardianproject.onionkit.web.WebkitProxy.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }
}