package org.torproject.android.service;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;
import org.torproject.android.TorConstants;

/* loaded from: classes.dex */
public class TorServiceUtils implements TorServiceConstants {
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:19:0x0061 -> B:5:0x0014). Please submit an issue!!! */
    public static boolean isRootPossible() {
        File fileSU;
        StringBuilder log = new StringBuilder();
        try {
            fileSU = new File("/system/app/Superuser.apk");
        } catch (IOException e) {
            Log.e(TorConstants.TAG, "Error checking for root access", e);
        } catch (Exception e2) {
            Log.e(TorConstants.TAG, "Error checking for root access", e2);
        }
        if (fileSU.exists()) {
            return true;
        }
        File fileSU2 = new File("/system/app/superuser.apk");
        if (fileSU2.exists()) {
            return true;
        }
        File fileSU3 = new File("/system/bin/su");
        if (fileSU3.exists()) {
            String[] cmd = {"su"};
            int exitCode = doShellCommand(cmd, log, false, true);
            return exitCode == 0;
        }
        String[] cmd2 = {"which su"};
        int exitCode2 = doShellCommand(cmd2, log, false, true);
        if (exitCode2 == 0) {
            Log.d(TorConstants.TAG, "root exists, but not sure about permissions");
            return true;
        }
        Log.e(TorConstants.TAG, "Could not acquire root permissions");
        return false;
    }

    public static int findProcessId(String command) {
        try {
            int procId = findProcessIdWithPidOf(command);
            if (procId == -1) {
                return findProcessIdWithPS(command);
            }
            return procId;
        } catch (Exception e) {
            try {
                return findProcessIdWithPS(command);
            } catch (Exception e2) {
                Log.w(TorConstants.TAG, "Unable to get proc id for: " + command, e2);
                return -1;
            }
        }
    }

    public static int findProcessIdWithPidOf(String command) throws Exception {
        int procId = -1;
        Runtime r = Runtime.getRuntime();
        String baseName = new File(command).getName();
        Process procPs = r.exec(new String[]{"pidof", baseName});
        BufferedReader reader = new BufferedReader(new InputStreamReader(procPs.getInputStream()));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            try {
                procId = Integer.parseInt(line.trim());
                break;
            } catch (NumberFormatException e) {
                Log.e("TorServiceUtils", "unable to parse process pid: " + line, e);
            }
        }
        return procId;
    }

    public static int findProcessIdWithPS(String command) throws Exception {
        String line;
        Runtime r = Runtime.getRuntime();
        Process procPs = r.exec("ps");
        BufferedReader reader = new BufferedReader(new InputStreamReader(procPs.getInputStream()));
        do {
            line = reader.readLine();
            if (line == null) {
                return -1;
            }
        } while (line.indexOf(String.valueOf(' ') + command) == -1);
        StringTokenizer st = new StringTokenizer(line, " ");
        st.nextToken();
        int procId = Integer.parseInt(st.nextToken().trim());
        return procId;
    }

    public static int doShellCommand(String[] cmds, StringBuilder log, boolean runAsRoot, boolean waitFor) throws Exception {
        Process proc;
        if (runAsRoot) {
            proc = Runtime.getRuntime().exec("su");
        } else {
            proc = Runtime.getRuntime().exec("sh");
        }
        OutputStreamWriter out = new OutputStreamWriter(proc.getOutputStream());
        for (String str : cmds) {
            out.write(str);
            out.write(TorConstants.NEWLINE);
        }
        out.flush();
        out.write("exit\n");
        out.flush();
        if (!waitFor) {
            return -1;
        }
        char[] buf = new char[10];
        InputStreamReader reader = new InputStreamReader(proc.getInputStream());
        while (true) {
            int read = reader.read(buf);
            if (read == -1) {
                break;
            } else if (log != null) {
                log.append(buf, 0, read);
            }
        }
        InputStreamReader reader2 = new InputStreamReader(proc.getErrorStream());
        while (true) {
            int read2 = reader2.read(buf);
            if (read2 != -1) {
                if (log != null) {
                    log.append(buf, 0, read2);
                }
            } else {
                int exitCode = proc.waitFor();
                return exitCode;
            }
        }
    }
}