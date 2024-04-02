package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/* loaded from: classes.dex */
class ConnectivityManagerCompatHoneycombMR2 {
    ConnectivityManagerCompatHoneycombMR2() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        NetworkInfo info2 = cm.getActiveNetworkInfo();
        if (info2 == null) {
            return true;
        }
        int type = info2.getType();
        switch (type) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
            default:
                return true;
            case 1:
            case 7:
            case 9:
                return false;
        }
    }
}