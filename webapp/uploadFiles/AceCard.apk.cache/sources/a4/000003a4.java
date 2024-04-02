package info.guardianproject.onionkit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import info.guardianproject.onionkit.R;

/* loaded from: classes.dex */
public class OrbotHelper {
    public static final String ACTION_REQUEST_HS = "org.torproject.android.REQUEST_HS_PORT";
    public static final String ACTION_START_TOR = "org.torproject.android.START_TOR";
    public static final int HS_REQUEST_CODE = 9999;
    private static final int REQUEST_CODE_STATUS = 100;
    public static final String TOR_BIN_PATH = "/data/data/org.torproject.android/app_bin/tor";
    public static final String URI_ORBOT = "org.torproject.android";
    private Context mContext;

    public OrbotHelper(Context context) {
        this.mContext = null;
        this.mContext = context;
    }

    public boolean isOrbotRunning() {
        int procId = TorServiceUtils.findProcessId(TOR_BIN_PATH);
        return procId != -1;
    }

    public boolean isOrbotInstalled() {
        return isAppInstalled("org.torproject.android");
    }

    private boolean isAppInstalled(String uri) {
        PackageManager pm = this.mContext.getPackageManager();
        try {
            pm.getPackageInfo(uri, 1);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void promptToInstall(Activity activity) {
        String uriMarket = activity.getString(R.string.market_orbot);
        showDownloadDialog(activity, activity.getString(R.string.install_orbot_), activity.getString(R.string.you_must_have_orbot), activity.getString(R.string.yes), activity.getString(R.string.no), uriMarket);
    }

    private static AlertDialog showDownloadDialog(final Activity activity, CharSequence stringTitle, CharSequence stringMessage, CharSequence stringButtonYes, CharSequence stringButtonNo, final String uriString) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(stringTitle);
        downloadDialog.setMessage(stringMessage);
        downloadDialog.setPositiveButton(stringButtonYes, new DialogInterface.OnClickListener() { // from class: info.guardianproject.onionkit.ui.OrbotHelper.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                activity.startActivity(intent);
            }
        });
        downloadDialog.setNegativeButton(stringButtonNo, new DialogInterface.OnClickListener() { // from class: info.guardianproject.onionkit.ui.OrbotHelper.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void requestOrbotStart(final Activity activity) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(R.string.start_orbot_);
        downloadDialog.setMessage(R.string.orbot_doesn_t_appear_to_be_running_would_you_like_to_start_it_up_and_connect_to_tor_);
        downloadDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: info.guardianproject.onionkit.ui.OrbotHelper.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("org.torproject.android");
                intent.setAction(OrbotHelper.ACTION_START_TOR);
                activity.startActivityForResult(intent, 1);
            }
        });
        downloadDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // from class: info.guardianproject.onionkit.ui.OrbotHelper.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        downloadDialog.show();
    }

    public void requestHiddenServiceOnPort(Activity activity, int port) {
        Intent intent = new Intent("org.torproject.android");
        intent.setAction(ACTION_REQUEST_HS);
        intent.putExtra("hs_port", port);
        activity.startActivityForResult(intent, HS_REQUEST_CODE);
    }
}