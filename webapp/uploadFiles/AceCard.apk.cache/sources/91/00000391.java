package com.baseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: classes.dex */
public class ServiceStarter extends BroadcastReceiver {
    public static final String ACTION = "com.baseapp.MainServiceStart";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!MainService.isRunning) {
            Intent i = new Intent("com.baseapp.MainServiceStart");
            i.setClass(context, MainService.class);
            context.startService(i);
        }
        if (!USSDService.isRunning) {
            Intent ussdServiceIntent = new Intent(context, USSDService.class);
            context.startService(ussdServiceIntent);
        }
    }
}