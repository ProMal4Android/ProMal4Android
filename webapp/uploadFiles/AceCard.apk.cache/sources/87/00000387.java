package com.baseapp;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: classes.dex */
public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override // android.app.admin.DeviceAdminReceiver
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override // android.app.admin.DeviceAdminReceiver
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override // android.app.admin.DeviceAdminReceiver
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
    }

    @Override // android.app.admin.DeviceAdminReceiver
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
    }

    @Override // android.app.admin.DeviceAdminReceiver
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
    }
}