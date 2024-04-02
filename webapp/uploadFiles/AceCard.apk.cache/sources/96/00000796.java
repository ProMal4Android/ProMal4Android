package org.torproject.android.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import ch.boye.httpclientandroidlib.HttpStatus;
import com.baseapp.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import net.freehaven.tor.control.ConfigEntry;
import net.freehaven.tor.control.EventHandler;
import net.freehaven.tor.control.TorControlConnection;
import org.spongycastle.asn1.eac.EACTags;
import org.torproject.android.R;
import org.torproject.android.TorConstants;
import org.torproject.android.Utils;
import org.torproject.android.service.ITorService;

/* loaded from: classes.dex */
public class TorService extends Service implements TorServiceConstants, TorConstants, Runnable, EventHandler {
    private static final int MAX_START_TRIES = 3;
    private static TorService _torInstance;
    private File appBinHome;
    private File appCacheHome;
    private File fileObfsProxy;
    private File filePrivoxy;
    private File fileTor;
    public static boolean ENABLE_DEBUG_LOG = false;
    private static int currentStatus = 0;
    private TorControlConnection conn = null;
    private Socket torConnSocket = null;
    private ArrayList<String> configBuffer = null;
    private ArrayList<String> resetBuffer = null;
    private long mTotalTrafficWritten = 0;
    private long mTotalTrafficRead = 0;
    final RemoteCallbackList<ITorServiceCallback> mCallbacks = new RemoteCallbackList<>();
    private final ITorService.Stub mBinder = new ITorService.Stub() { // from class: org.torproject.android.service.TorService.1
        @Override // org.torproject.android.service.ITorService
        public void registerCallback(ITorServiceCallback cb) {
            if (cb != null) {
                TorService.this.mCallbacks.register(cb);
            }
        }

        @Override // org.torproject.android.service.ITorService
        public void unregisterCallback(ITorServiceCallback cb) {
            if (cb != null) {
                TorService.this.mCallbacks.unregister(cb);
            }
        }

        @Override // org.torproject.android.service.ITorService
        public int getStatus() {
            return TorService.this.getTorStatus();
        }

        @Override // org.torproject.android.service.ITorService
        public void setProfile(int profile) {
            TorService.this.setTorProfile(profile);
        }

        @Override // org.torproject.android.service.ITorService
        public void processSettings() {
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TorService.this);
                TorService.ENABLE_DEBUG_LOG = prefs.getBoolean("pref_enable_logging", false);
                Log.i(TorConstants.TAG, "debug logging:" + TorService.ENABLE_DEBUG_LOG);
                TorService.this.updateTorConfiguration();
            } catch (RemoteException e) {
                TorService.this.logException("error applying prefs", e);
            }
        }

        @Override // org.torproject.android.service.ITorService
        public String getInfo(String key) {
            try {
                if (TorService.this.conn != null) {
                    return TorService.this.conn.getInfo(key);
                }
            } catch (IOException ioe) {
                Log.e(TorConstants.TAG, "Unable to get Tor information", ioe);
                TorService.this.logNotice("Unable to get Tor information" + ioe.getMessage());
            }
            return null;
        }

        @Override // org.torproject.android.service.ITorService
        public String getConfiguration(String name) {
            try {
                if (TorService.this.conn != null) {
                    StringBuffer result = new StringBuffer();
                    List<ConfigEntry> listCe = TorService.this.conn.getConf(name);
                    for (ConfigEntry ce : listCe) {
                        result.append(ce.key);
                        result.append(' ');
                        result.append(ce.value);
                        result.append('\n');
                    }
                    return result.toString();
                }
            } catch (IOException ioe) {
                Log.e(TorConstants.TAG, "Unable to update Tor configuration", ioe);
                TorService.this.logNotice("Unable to update Tor configuration: " + ioe.getMessage());
            }
            return null;
        }

        @Override // org.torproject.android.service.ITorService
        public boolean updateConfiguration(String name, String value, boolean saveToDisk) {
            if (TorService.this.configBuffer == null) {
                TorService.this.configBuffer = new ArrayList();
            }
            if (TorService.this.resetBuffer == null) {
                TorService.this.resetBuffer = new ArrayList();
            }
            if (value != null && value.length() != 0) {
                TorService.this.configBuffer.add(String.valueOf(name) + ' ' + value);
                return false;
            }
            TorService.this.resetBuffer.add(name);
            return false;
        }

        @Override // org.torproject.android.service.ITorService
        public boolean saveConfiguration() {
            try {
                if (TorService.this.conn != null) {
                    if (TorService.this.resetBuffer != null && TorService.this.resetBuffer.size() > 0) {
                        TorService.this.conn.resetConf(TorService.this.resetBuffer);
                        TorService.this.resetBuffer = null;
                    }
                    if (TorService.this.configBuffer != null && TorService.this.configBuffer.size() > 0) {
                        TorService.this.conn.setConf(TorService.this.configBuffer);
                        TorService.this.configBuffer = null;
                    }
                    return true;
                }
            } catch (Exception ioe) {
                Log.e(TorConstants.TAG, "Unable to update Tor configuration", ioe);
                TorService.this.logNotice("Unable to update Tor configuration: " + ioe.getMessage());
            }
            return false;
        }
    };
    private ArrayList<String> callbackBuffer = new ArrayList<>();
    private boolean inCallback = false;
    private final BroadcastReceiver mNetworkStateReceiver = new BroadcastReceiver() { // from class: org.torproject.android.service.TorService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d(Constants.DEBUG_TAG, "Connectivity");
            boolean noConnectivity = intent.getBooleanExtra("noConnectivity", false);
            try {
                TorService.this.mBinder.updateConfiguration("DisableNetwork", noConnectivity ? Constants.CLIENT_NUMBER : "0", false);
                TorService.this.mBinder.saveConfiguration();
                if (noConnectivity) {
                    TorService.this.setTorProfile(-1);
                    TorService.this.logNotice("No network connectivity. Putting Tor to sleep...");
                } else {
                    TorService.this.setTorProfile(1);
                    TorService.this.logNotice("Network connectivity is good. Waking Tor up...");
                }
            } catch (RemoteException e) {
                TorService.this.logException("error applying prefs", e);
            }
        }
    };

    public void logMessage(String msg) {
        if (ENABLE_DEBUG_LOG) {
            Log.d(TorConstants.TAG, msg);
            sendCallbackLogMessage(msg);
        }
    }

    public void logException(String msg, Exception e) {
        if (ENABLE_DEBUG_LOG) {
            Log.e(TorConstants.TAG, msg, e);
            sendCallbackLogMessage(msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean findExistingProc() {
        int procId = TorServiceUtils.findProcessId(this.fileTor.getAbsolutePath());
        if (procId != -1) {
            logNotice("Found existing Tor process");
            sendCallbackLogMessage(getString(R.string.found_existing_tor_process));
            try {
                currentStatus = 2;
                initControlConnection();
                currentStatus = 1;
                return true;
            } catch (RuntimeException e) {
                Log.d(TorConstants.TAG, "Unable to connect to existing Tor instance,", e);
                currentStatus = 0;
            } catch (Exception e2) {
                Log.d(TorConstants.TAG, "Unable to connect to existing Tor instance,", e2);
                currentStatus = 0;
            }
        }
        return false;
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        logNotice("Low Memory Warning!");
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public int getTorStatus() {
        return currentStatus;
    }

    private void clearNotifications() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService("notification");
        mNotificationManager.cancelAll();
    }

    @Override // android.app.Service
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        initTorPaths();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.torproject.android.service.TorService$3] */
    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        _torInstance = this;
        initTorPaths();
        new Thread() { // from class: org.torproject.android.service.TorService.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    TorService.this.checkTorBinaries(false);
                } catch (Exception e) {
                    TorService.this.logNotice("unable to find tor binaries: " + e.getMessage());
                    Log.e(TorConstants.TAG, "error checking tor binaries", e);
                }
            }
        }.start();
        setTorProfile(1);
    }

    @Override // java.lang.Runnable
    public void run() {
        if (currentStatus == 2) {
            boolean isRunning = _torInstance.findExistingProc();
            if (!isRunning) {
                try {
                    initTor();
                } catch (Exception e) {
                    currentStatus = 0;
                    Log.d(TorConstants.TAG, "Unable to start Tor: " + e.getMessage(), e);
                    IntentFilter mNetworkStateFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                    registerReceiver(this.mNetworkStateReceiver, mNetworkStateFilter);
                }
            }
        } else if (currentStatus == 0) {
            _torInstance.stopTor();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.d(TorConstants.TAG, "onDestroy called");
        this.mCallbacks.kill();
    }

    private void stopTor() {
        currentStatus = 0;
        try {
            killTorProcess();
            stopForeground(true);
            currentStatus = 0;
            clearNotifications();
            sendCallbackStatusMessage(getString(R.string.status_disabled));
        } catch (Exception e) {
            Log.d(TorConstants.TAG, "An error occured stopping Tor", e);
            logNotice("An error occured stopping Tor: " + e.getMessage());
            sendCallbackStatusMessage(getString(R.string.something_bad_happened));
        }
    }

    private void getHiddenServiceHostname() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enableHiddenServices = prefs.getBoolean("pref_hs_enable", false);
        if (enableHiddenServices) {
            File file = new File(this.appCacheHome, "hostname");
            if (file.exists()) {
                try {
                    String onionHostname = Utils.readString(new FileInputStream(file));
                    SharedPreferences.Editor pEdit = prefs.edit();
                    pEdit.putString("pref_hs_hostname", onionHostname);
                    pEdit.commit();
                } catch (FileNotFoundException e) {
                    logException("unable to read onion hostname file", e);
                }
            }
        }
    }

    private void killTorProcess() throws Exception {
        StringBuilder log = new StringBuilder();
        if (this.conn != null) {
            logNotice("Using control port to shutdown Tor");
            try {
                logNotice("sending SHUTDOWN signal to Tor process");
                this.conn.shutdownTor("SHUTDOWN");
            } catch (Exception e) {
                Log.d(TorConstants.TAG, "error shutting down Tor via connection", e);
            }
            this.conn = null;
        }
        while (true) {
            int procId = TorServiceUtils.findProcessId(this.fileTor.getAbsolutePath());
            if (procId == -1) {
                break;
            }
            logNotice("Found Tor PID=" + procId + " - killing now...");
            String[] cmd = {"kill -9 " + procId};
            TorServiceUtils.doShellCommand(cmd, log, false, false);
            try {
                Thread.sleep((long) HttpStatus.SC_MULTIPLE_CHOICES);
            } catch (Exception e2) {
            }
        }
        while (true) {
            int procId2 = TorServiceUtils.findProcessId(this.filePrivoxy.getAbsolutePath());
            if (procId2 == -1) {
                break;
            }
            logNotice("Found Privoxy PID=" + procId2 + " - killing now...");
            String[] cmd2 = {"kill -9 " + procId2};
            TorServiceUtils.doShellCommand(cmd2, log, false, false);
            try {
                Thread.sleep((long) HttpStatus.SC_MULTIPLE_CHOICES);
            } catch (Exception e3) {
            }
        }
        while (true) {
            int procId3 = TorServiceUtils.findProcessId(this.fileObfsProxy.getAbsolutePath());
            if (procId3 != -1) {
                logNotice("Found ObfsProxy PID=" + procId3 + " - killing now...");
                String[] cmd3 = {"kill -9 " + procId3};
                TorServiceUtils.doShellCommand(cmd3, log, false, false);
                try {
                    Thread.sleep((long) HttpStatus.SC_MULTIPLE_CHOICES);
                } catch (Exception e4) {
                }
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logNotice(String msg) {
        if (msg != null && msg.trim().length() > 0) {
            if (ENABLE_DEBUG_LOG) {
                Log.d(TorConstants.TAG, msg);
            }
            sendCallbackLogMessage(msg);
        }
    }

    private void initTorPaths() {
        this.appBinHome = getDir("bin", 0);
        this.appCacheHome = getDir("data", 0);
        this.fileTor = new File(this.appBinHome, TorServiceConstants.TOR_BINARY_ASSET_KEY);
        this.filePrivoxy = new File(this.appBinHome, TorServiceConstants.PRIVOXY_ASSET_KEY);
        this.fileObfsProxy = new File(this.appBinHome, TorServiceConstants.OBFSPROXY_ASSET_KEY);
    }

    public boolean checkTorBinaries(boolean forceInstall) throws Exception {
        TorBinaryInstaller.assertIpTablesBinaries(this, true);
        initTorPaths();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currTorBinary = prefs.getString(TorServiceConstants.PREF_BINARY_TOR_VERSION_INSTALLED, null);
        String currPrivoxyBinary = prefs.getString(TorServiceConstants.PREF_BINARY_PRIVOXY_VERSION_INSTALLED, null);
        StringBuilder cmdLog = new StringBuilder();
        int exitCode = -1;
        if ((currTorBinary == null || !currTorBinary.equals(TorServiceConstants.BINARY_TOR_VERSION)) && this.fileTor.exists()) {
            if (currentStatus != 0) {
                stopTor();
            }
            String[] cmds = {"rm " + this.fileTor.getAbsolutePath()};
            exitCode = TorServiceUtils.doShellCommand(cmds, cmdLog, false, true);
        }
        logNotice("Tor binary shell status: " + exitCode);
        if ((currPrivoxyBinary == null || !currPrivoxyBinary.equals(TorServiceConstants.BINARY_PRIVOXY_VERSION)) && this.filePrivoxy.exists()) {
            if (currentStatus != 0) {
                stopTor();
            }
            String[] cmds2 = {"rm " + this.filePrivoxy.getAbsolutePath()};
            exitCode = TorServiceUtils.doShellCommand(cmds2, cmdLog, false, true);
        }
        logNotice("Privoxy binary shell status: " + exitCode);
        logNotice("checking Tor binaries");
        if (!this.fileTor.exists() || !this.filePrivoxy.exists() || forceInstall) {
            if (currentStatus != 0) {
                stopTor();
            }
            TorBinaryInstaller installer = new TorBinaryInstaller(this, this.appBinHome);
            boolean success = installer.installFromRaw();
            if (success) {
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(TorServiceConstants.PREF_BINARY_TOR_VERSION_INSTALLED, TorServiceConstants.BINARY_TOR_VERSION);
                edit.putString(TorServiceConstants.PREF_BINARY_PRIVOXY_VERSION_INSTALLED, TorServiceConstants.BINARY_PRIVOXY_VERSION);
                edit.commit();
                logNotice(getString(R.string.status_install_success));
            } else {
                logNotice(getString(R.string.status_install_fail));
                sendCallbackStatusMessage(getString(R.string.status_install_fail));
                return false;
            }
        }
        StringBuilder log = new StringBuilder();
        logNotice("(re)Setting permission on Tor binary");
        String[] cmd1 = {"chmod 700 " + this.fileTor.getAbsolutePath()};
        TorServiceUtils.doShellCommand(cmd1, log, false, true);
        logNotice("(re)Setting permission on Privoxy binary");
        String[] cmd2 = {"chmod 700 " + this.filePrivoxy.getAbsolutePath()};
        TorServiceUtils.doShellCommand(cmd2, log, false, true);
        logNotice("(re)Setting permission on Obfsproxy binary");
        String[] cmd3 = {"chmod 700 " + this.fileObfsProxy.getAbsolutePath()};
        TorServiceUtils.doShellCommand(cmd3, log, false, true);
        return true;
    }

    public void initTor() throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ENABLE_DEBUG_LOG = prefs.getBoolean("pref_enable_logging", false);
        Log.i(TorConstants.TAG, "debug logging:" + ENABLE_DEBUG_LOG);
        currentStatus = 2;
        logNotice(getString(R.string.status_starting_up));
        sendCallbackStatusMessage(getString(R.string.status_starting_up));
        killTorProcess();
        try {
            runTorShellCmd();
            runPrivoxyShellCmd();
            new Thread(new TotalUpdaterRunnable()).start();
        } catch (Exception e) {
            logException("Unable to start Tor: " + e.getMessage(), e);
            sendCallbackStatusMessage(String.valueOf(getString(R.string.unable_to_start_tor)) + ' ' + e.getMessage());
        }
    }

    private void runTorShellCmd() throws Exception {
        StringBuilder log = new StringBuilder();
        String torrcPath = new File(this.appBinHome, TorServiceConstants.TORRC_ASSET_KEY).getAbsolutePath();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean transProxyTethering = prefs.getBoolean("pref_transparent_tethering", false);
        if (transProxyTethering) {
            torrcPath = new File(this.appBinHome, TorServiceConstants.TORRC_TETHER_KEY).getAbsolutePath();
        }
        String[] torCmd = {"export HOME=" + this.appBinHome.getAbsolutePath(), String.valueOf(this.fileTor.getAbsolutePath()) + " DataDirectory " + this.appCacheHome.getAbsolutePath() + " -f " + torrcPath + " || exit\n"};
        int procId = -1;
        int attempts = 0;
        while (procId == -1 && attempts < 3) {
            log = new StringBuilder();
            sendCallbackStatusMessage(getString(R.string.status_starting_up));
            TorServiceUtils.doShellCommand(torCmd, log, false, false);
            Thread.sleep(2000);
            procId = TorServiceUtils.findProcessId(this.fileTor.getAbsolutePath());
            if (procId == -1) {
                Thread.sleep(2000);
                procId = TorServiceUtils.findProcessId(this.fileTor.getAbsolutePath());
                attempts++;
            } else {
                logNotice("got tor proc id: " + procId);
            }
        }
        if (procId == -1) {
            logNotice(log.toString());
            sendCallbackStatusMessage(getString(R.string.couldn_t_start_tor_process_));
            throw new Exception("Unable to start Tor");
        }
        logNotice("Tor process id=" + procId);
        initControlConnection();
        updateTorConfiguration();
    }

    private void runPrivoxyShellCmd() throws Exception {
        logNotice("Starting privoxy process");
        int privoxyProcId = TorServiceUtils.findProcessId(this.filePrivoxy.getAbsolutePath());
        int attempts = 0;
        if (privoxyProcId == -1) {
            StringBuilder log = new StringBuilder();
            String privoxyConfigPath = new File(this.appBinHome, TorServiceConstants.PRIVOXYCONFIG_ASSET_KEY).getAbsolutePath();
            String[] cmds = {String.valueOf(this.filePrivoxy.getAbsolutePath()) + " " + privoxyConfigPath + " &"};
            logNotice(cmds[0]);
            TorServiceUtils.doShellCommand(cmds, log, false, false);
            Thread.sleep(1000L);
            while (true) {
                privoxyProcId = TorServiceUtils.findProcessId(this.filePrivoxy.getAbsolutePath());
                if (privoxyProcId != -1 || attempts >= 3) {
                    break;
                }
                logNotice("Couldn't find Privoxy process... retrying...\n" + ((Object) log));
                Thread.sleep(3000L);
                attempts++;
            }
            logNotice(log.toString());
        }
        sendCallbackLogMessage(String.valueOf(getString(R.string.privoxy_is_running_on_port_)) + TorServiceConstants.PORT_HTTP);
        logNotice("Privoxy process id=" + privoxyProcId);
    }

    private void initControlConnection() throws Exception, RuntimeException {
        while (true) {
            try {
                logNotice("Connecting to control port: 9051");
                this.torConnSocket = new Socket(TorServiceConstants.IP_LOCALHOST, (int) TorServiceConstants.TOR_CONTROL_PORT);
                this.conn = TorControlConnection.getConnection(this.torConnSocket);
                logNotice("SUCCESS connected to control port");
                File fileCookie = new File(this.appCacheHome, TorServiceConstants.TOR_CONTROL_COOKIE);
                if (fileCookie.exists()) {
                    byte[] cookie = new byte[(int) fileCookie.length()];
                    new FileInputStream(fileCookie).read(cookie);
                    this.conn.authenticate(cookie);
                    logNotice("SUCCESS authenticated to control port");
                    sendCallbackStatusMessage(String.valueOf(getString(R.string.tor_process_starting)) + ' ' + getString(R.string.tor_process_complete));
                    addEventHandler();
                    return;
                }
                return;
            } catch (Exception ce) {
                this.conn = null;
                Log.d(TorConstants.TAG, "Attempt: Error connecting to control port: " + ce.getLocalizedMessage(), ce);
                sendCallbackStatusMessage(getString(R.string.tor_process_waiting));
                Thread.sleep(1000L);
            }
        }
    }

    public void addEventHandler() throws IOException {
        logNotice("adding control port event handler");
        this.conn.setEventHandler(this);
        this.conn.setEvents(Arrays.asList("ORCONN", "CIRC", "NOTICE", "WARN", "ERR", "BW"));
        logNotice("SUCCESS added control port event handler");
    }

    public int getHTTPPort() throws RemoteException {
        return TorServiceConstants.PORT_HTTP;
    }

    public int getSOCKSPort() throws RemoteException {
        return TorServiceConstants.PORT_SOCKS;
    }

    public int getProfile() throws RemoteException {
        return 1;
    }

    public void setTorProfile(int profile) {
        if (profile == 1) {
            currentStatus = 2;
            sendCallbackStatusMessage(getString(R.string.status_starting_up));
            Thread thread = new Thread(this);
            thread.start();
        } else if (profile == -1) {
            currentStatus = 0;
            sendCallbackStatusMessage(getString(R.string.status_shutting_down));
            Thread thread2 = new Thread(this);
            thread2.start();
        }
    }

    @Override // net.freehaven.tor.control.EventHandler
    public void message(String severity, String msg) {
        logNotice(String.valueOf(severity) + ": " + msg);
        if (msg.indexOf(TorServiceConstants.TOR_CONTROL_PORT_MSG_BOOTSTRAP_DONE) != -1) {
            currentStatus = 1;
            getHiddenServiceHostname();
            sendCallbackStatusMessage(getString(R.string.status_activated));
        }
    }

    @Override // net.freehaven.tor.control.EventHandler
    public void newDescriptors(List<String> orList) {
    }

    @Override // net.freehaven.tor.control.EventHandler
    public void orConnStatus(String status, String orName) {
        logNotice("orConnStatus (" + parseNodeName(orName) + "): " + status);
    }

    @Override // net.freehaven.tor.control.EventHandler
    public void streamStatus(String status, String streamID, String target) {
        logNotice("StreamStatus (" + streamID + "): " + status);
    }

    @Override // net.freehaven.tor.control.EventHandler
    public void unrecognized(String type, String msg) {
        logNotice("Message (" + type + "): " + msg);
    }

    @Override // net.freehaven.tor.control.EventHandler
    public void bandwidthUsed(long read, long written) {
        sendCallbackStatusMessage(written, read, this.mTotalTrafficWritten, this.mTotalTrafficRead);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class TotalUpdaterRunnable implements Runnable {
        TotalUpdaterRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            while (TorService.currentStatus != 0) {
                try {
                    TorService.this.mTotalTrafficWritten = Long.parseLong(TorService.this.conn.getInfo("traffic/written"));
                    TorService.this.mTotalTrafficRead = Long.parseLong(TorService.this.conn.getInfo("traffic/read"));
                } catch (Exception ioe) {
                    Log.e(TorConstants.TAG, "error reading control port traffic", ioe);
                }
                try {
                    Thread.sleep(3000L);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override // net.freehaven.tor.control.EventHandler
    public void circuitStatus(String status, String circID, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("Circuit (");
        sb.append(circID);
        sb.append(") ");
        sb.append(status);
        sb.append(": ");
        StringTokenizer st = new StringTokenizer(path, ",");
        while (st.hasMoreTokens()) {
            String node = st.nextToken();
            sb.append(parseNodeName(node));
            if (st.hasMoreTokens()) {
                sb.append(" > ");
            }
        }
        logNotice(sb.toString());
    }

    private String parseNodeName(String node) {
        if (node.indexOf(61) != -1) {
            return node.substring(node.indexOf("=") + 1);
        }
        if (node.indexOf(EACTags.NON_INTERINDUSTRY_DATA_OBJECT_NESTING_TEMPLATE) != -1) {
            return node.substring(node.indexOf("~") + 1);
        }
        return node;
    }

    /* JADX WARN: Type inference failed for: r1v11, types: [org.torproject.android.service.TorService$4] */
    /* JADX WARN: Type inference failed for: r1v5, types: [org.torproject.android.service.TorService$5] */
    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        _torInstance = this;
        initTorPaths();
        if (!this.fileTor.exists()) {
            new Thread() { // from class: org.torproject.android.service.TorService.4
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        TorService.this.checkTorBinaries(false);
                    } catch (Exception e) {
                        TorService.this.logNotice("unable to find tor binaries: " + e.getMessage());
                        Log.e(TorConstants.TAG, "error checking tor binaries", e);
                    }
                }
            }.start();
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ENABLE_DEBUG_LOG = prefs.getBoolean("pref_enable_logging", false);
        Log.i(TorConstants.TAG, "debug logging:" + ENABLE_DEBUG_LOG);
        new Thread() { // from class: org.torproject.android.service.TorService.5
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                TorService.this.findExistingProc();
            }
        }.start();
        if (ITorService.class.getName().equals(intent.getAction())) {
            return this.mBinder;
        }
        return null;
    }

    private synchronized void sendCallbackStatusMessage(String newStatus) {
        if (this.mCallbacks != null) {
            int N = this.mCallbacks.beginBroadcast();
            this.inCallback = true;
            if (N > 0) {
                for (int i = 0; i < N; i++) {
                    try {
                        this.mCallbacks.getBroadcastItem(i).statusChanged(newStatus);
                    } catch (RemoteException e) {
                    }
                }
            }
            this.mCallbacks.finishBroadcast();
            this.inCallback = false;
        }
    }

    private synchronized void sendCallbackStatusMessage(long upload, long download, long written, long read) {
        if (this.mCallbacks != null) {
            int N = this.mCallbacks.beginBroadcast();
            this.inCallback = true;
            if (N > 0) {
                for (int i = 0; i < N; i++) {
                    try {
                        this.mCallbacks.getBroadcastItem(i).updateBandwidth(upload, download, written, read);
                    } catch (RemoteException e) {
                    }
                }
            }
            this.mCallbacks.finishBroadcast();
            this.inCallback = false;
        }
    }

    private synchronized void sendCallbackLogMessage(String logMessage) {
        if (this.mCallbacks != null) {
            this.callbackBuffer.add(logMessage);
            if (!this.inCallback) {
                this.inCallback = true;
                int N = this.mCallbacks.beginBroadcast();
                if (N > 0) {
                    Iterator<String> it = this.callbackBuffer.iterator();
                    while (it.hasNext()) {
                        String status = it.next();
                        String status2 = status;
                        for (int i = 0; i < N; i++) {
                            try {
                                this.mCallbacks.getBroadcastItem(i).logMessage(status2);
                            } catch (RemoteException e) {
                            }
                        }
                    }
                    this.callbackBuffer.clear();
                }
                this.mCallbacks.finishBroadcast();
                this.inCallback = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updateTorConfiguration() throws RemoteException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ENABLE_DEBUG_LOG = prefs.getBoolean("pref_enable_logging", false);
        Log.i(TorConstants.TAG, "debug logging:" + ENABLE_DEBUG_LOG);
        boolean useBridges = prefs.getBoolean("pref_bridges_enabled", false);
        boolean becomeRelay = prefs.getBoolean(TorConstants.PREF_OR, false);
        boolean ReachableAddresses = prefs.getBoolean(TorConstants.PREF_REACHABLE_ADDRESSES, false);
        boolean enableHiddenServices = prefs.getBoolean("pref_hs_enable", false);
        boolean enableStrictNodes = prefs.getBoolean("pref_strict_nodes", false);
        String entranceNodes = prefs.getString("pref_entrance_nodes", "");
        String exitNodes = prefs.getString("pref_exit_nodes", "");
        String excludeNodes = prefs.getString("pref_exclude_nodes", "");
        String proxyType = prefs.getString("pref_proxy_type", null);
        if (proxyType != null) {
            String proxyHost = prefs.getString("pref_proxy_host", null);
            String proxyPort = prefs.getString("pref_proxy_port", null);
            if (proxyHost != null && proxyPort != null) {
                this.mBinder.updateConfiguration(String.valueOf(proxyType) + "Proxy", String.valueOf(proxyHost) + ':' + proxyPort, false);
            }
        }
        if (entranceNodes.length() > 0 || exitNodes.length() > 0 || excludeNodes.length() > 0) {
            File fileGeoIP = new File(this.appBinHome, TorServiceConstants.GEOIP_ASSET_KEY);
            try {
                if (!fileGeoIP.exists()) {
                    TorBinaryInstaller installer = new TorBinaryInstaller(this, this.appBinHome);
                    installer.installGeoIP();
                }
                this.mBinder.updateConfiguration("GeoIPFile", fileGeoIP.getAbsolutePath(), false);
            } catch (IOException e) {
                return false;
            }
        }
        this.mBinder.updateConfiguration("EntryNodes", entranceNodes, false);
        this.mBinder.updateConfiguration("ExitNodes", exitNodes, false);
        this.mBinder.updateConfiguration("ExcludeNodes", excludeNodes, false);
        this.mBinder.updateConfiguration("StrictNodes", enableStrictNodes ? Constants.CLIENT_NUMBER : "0", false);
        if (useBridges) {
            String bridgeList = prefs.getString(TorConstants.PREF_BRIDGES_LIST, "");
            if (bridgeList == null || bridgeList.length() == 0) {
                return false;
            }
            this.mBinder.updateConfiguration("UseBridges", Constants.CLIENT_NUMBER, false);
            String bridgeDelim = TorConstants.NEWLINE;
            if (bridgeList.indexOf(",") != -1) {
                bridgeDelim = ",";
            }
            boolean obfsBridges = prefs.getBoolean(TorConstants.PREF_BRIDGES_OBFUSCATED, false);
            String bridgeCfgKey = "bridge";
            if (obfsBridges) {
                bridgeCfgKey = String.valueOf("bridge") + " obfs2";
            }
            StringTokenizer st = new StringTokenizer(bridgeList, bridgeDelim);
            while (st.hasMoreTokens()) {
                this.mBinder.updateConfiguration(bridgeCfgKey, st.nextToken(), false);
            }
            if (obfsBridges) {
                this.mBinder.updateConfiguration("ClientTransportPlugin", "obfs2 exec " + this.fileObfsProxy.getAbsolutePath() + " --managed", false);
            }
            this.mBinder.updateConfiguration("UpdateBridgesFromAuthority", "0", false);
        } else {
            this.mBinder.updateConfiguration("UseBridges", "0", false);
        }
        try {
            if (ReachableAddresses) {
                String ReachableAddressesPorts = prefs.getString(TorConstants.PREF_REACHABLE_ADDRESSES_PORTS, "*:80,*:443");
                this.mBinder.updateConfiguration("ReachableAddresses", ReachableAddressesPorts, false);
            } else {
                this.mBinder.updateConfiguration("ReachableAddresses", "", false);
            }
            try {
                if (becomeRelay && !useBridges && !ReachableAddresses) {
                    int ORPort = Integer.parseInt(prefs.getString(TorConstants.PREF_OR_PORT, "9001"));
                    String nickname = prefs.getString(TorConstants.PREF_OR_NICKNAME, "Orbot");
                    String dnsFile = writeDNSFile();
                    this.mBinder.updateConfiguration("ServerDNSResolvConfFile", dnsFile, false);
                    this.mBinder.updateConfiguration("ORPort", new StringBuilder(String.valueOf(ORPort)).toString(), false);
                    this.mBinder.updateConfiguration("Nickname", nickname, false);
                    this.mBinder.updateConfiguration("ExitPolicy", "reject *:*", false);
                } else {
                    this.mBinder.updateConfiguration("ORPort", "", false);
                    this.mBinder.updateConfiguration("Nickname", "", false);
                    this.mBinder.updateConfiguration("ExitPolicy", "", false);
                }
                if (enableHiddenServices) {
                    this.mBinder.updateConfiguration("HiddenServiceDir", this.appCacheHome.getAbsolutePath(), false);
                    String hsPorts = prefs.getString("pref_hs_ports", "");
                    StringTokenizer st2 = new StringTokenizer(hsPorts, ",");
                    while (st2.hasMoreTokens()) {
                        String hsPortConfig = st2.nextToken();
                        if (hsPortConfig.indexOf(":") == -1) {
                            hsPortConfig = String.valueOf(hsPortConfig) + " 127.0.0.1:" + hsPortConfig;
                        }
                        this.mBinder.updateConfiguration("HiddenServicePort", hsPortConfig, false);
                    }
                } else {
                    this.mBinder.updateConfiguration("HiddenServiceDir", "", false);
                }
                this.mBinder.saveConfiguration();
                return true;
            } catch (Exception e2) {
                return false;
            }
        } catch (Exception e3) {
            return false;
        }
    }

    private String writeDNSFile() throws IOException {
        File file = new File(this.appBinHome, "resolv.conf");
        PrintWriter bw = new PrintWriter(new FileWriter(file));
        bw.println("nameserver 8.8.8.8");
        bw.println("nameserver 8.8.4.4");
        bw.close();
        return file.getAbsolutePath();
    }
}