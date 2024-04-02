package info.guardianproject.onionkit.proxy;

import android.content.Context;
import android.util.Log;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import info.guardianproject.onionkit.trust.StrongHttpsClient;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* loaded from: classes.dex */
public class HttpManager {
    private static final String POST_MIME_TYPE = "application/x-www-form-urlencoded";
    private static final String TAG = "HttpManager";

    public static String doGet(Context context, String serviceEndpoint, Properties props) throws Exception {
        HttpClient httpClient = new StrongHttpsClient(context);
        StringBuilder uriBuilder = new StringBuilder(serviceEndpoint);
        StringBuffer sbResponse = new StringBuffer();
        Enumeration<Object> enumProps = props.keys();
        uriBuilder.append('?');
        while (enumProps.hasMoreElements()) {
            String key = (String) enumProps.nextElement();
            String value = (String) props.get(key);
            uriBuilder.append(key);
            uriBuilder.append('=');
            uriBuilder.append(URLEncoder.encode(value));
            uriBuilder.append('&');
        }
        HttpGet request = new HttpGet(uriBuilder.toString());
        HttpResponse response = httpClient.execute(request);
        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            response.getEntity().writeTo(ostream);
            Log.e("HTTP CLIENT", ostream.toString());
            return null;
        }
        InputStream content = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                sbResponse.append(line);
            } else {
                content.close();
                return sbResponse.toString();
            }
        }
    }

    public static String doPost(Context context, String serviceEndpoint, Properties props) throws Exception {
        DefaultHttpClient httpClient = new StrongHttpsClient(context);
        HttpPost request = new HttpPost(serviceEndpoint);
        StringBuffer sbResponse = new StringBuffer();
        Enumeration<Object> enumProps = props.keys();
        List<NameValuePair> nvps = new ArrayList<>();
        while (enumProps.hasMoreElements()) {
            String key = (String) enumProps.nextElement();
            String value = (String) props.get(key);
            nvps.add(new BasicNameValuePair(key, value));
            Log.i(TAG, "adding nvp:" + key + "=" + value);
        }
        UrlEncodedFormEntity uf = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
        Log.i(TAG, uf.toString());
        request.setEntity(uf);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        Log.i(TAG, "http post request: " + request.toString());
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        int status = response.getStatusLine().getStatusCode();
        if (status != 200) {
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            entity.writeTo(ostream);
            Log.e(TAG, " error status code=" + status);
            Log.e(TAG, ostream.toString());
            return null;
        }
        InputStream content = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        while (true) {
            String line = reader.readLine();
            if (line != null) {
                sbResponse.append(line);
            } else {
                content.close();
                return sbResponse.toString();
            }
        }
    }

    public static String uploadFile(String serviceEndpoint, Properties properties, String fileParam, String file) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(serviceEndpoint);
        MultipartEntity entity = new MultipartEntity();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            entity.addPart(key, new StringBody(val));
        }
        File upload = new File(file);
        Log.i("httpman", "upload file (" + upload.getAbsolutePath() + ") size=" + upload.length());
        entity.addPart(fileParam, new FileBody(upload));
        request.setEntity(entity);
        HttpResponse response = httpClient.execute(request);
        response.getStatusLine().getStatusCode();
        return response.toString();
    }
}