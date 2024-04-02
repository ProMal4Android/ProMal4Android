package org.torproject.android.service;

import android.content.Context;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
import org.torproject.android.R;
import org.torproject.android.TorConstants;

/* loaded from: classes.dex */
public class TorBinaryInstaller implements TorServiceConstants {
    Context context;
    File installFolder;
    private static int isARMv6 = -1;
    private static String CHMOD_EXEC = "700";

    public TorBinaryInstaller(Context context, File installFolder) {
        this.installFolder = installFolder;
        this.context = context;
    }

    public boolean installFromRaw() throws IOException, FileNotFoundException {
        InputStream is = this.context.getResources().openRawResource(R.raw.tor);
        File outFile = new File(this.installFolder, TorServiceConstants.TOR_BINARY_ASSET_KEY);
        streamToFile(is, outFile, false, true);
        InputStream is2 = this.context.getResources().openRawResource(R.raw.torrc);
        File outFile2 = new File(this.installFolder, TorServiceConstants.TORRC_ASSET_KEY);
        streamToFile(is2, outFile2, false, false);
        InputStream is3 = this.context.getResources().openRawResource(R.raw.torrctether);
        File outFile3 = new File(this.installFolder, TorServiceConstants.TORRC_TETHER_KEY);
        streamToFile(is3, outFile3, false, false);
        InputStream is4 = this.context.getResources().openRawResource(R.raw.privoxy);
        File outFile4 = new File(this.installFolder, TorServiceConstants.PRIVOXY_ASSET_KEY);
        streamToFile(is4, outFile4, false, false);
        InputStream is5 = this.context.getResources().openRawResource(R.raw.privoxy_config);
        File outFile5 = new File(this.installFolder, TorServiceConstants.PRIVOXYCONFIG_ASSET_KEY);
        streamToFile(is5, outFile5, false, false);
        InputStream is6 = this.context.getResources().openRawResource(R.raw.obfsproxy);
        File outFile6 = new File(this.installFolder, TorServiceConstants.OBFSPROXY_ASSET_KEY);
        streamToFile(is6, outFile6, false, false);
        return true;
    }

    public boolean installGeoIP() throws IOException, FileNotFoundException {
        InputStream is = this.context.getResources().openRawResource(R.raw.geoip);
        File outFile = new File(this.installFolder, TorServiceConstants.GEOIP_ASSET_KEY);
        streamToFile(is, outFile, false, true);
        return true;
    }

    private static boolean streamToFile(InputStream stm, File outFile, boolean append, boolean zip) throws IOException {
        byte[] buffer = new byte[2048];
        OutputStream stmOut = new FileOutputStream(outFile, append);
        if (zip) {
            ZipInputStream zis = new ZipInputStream(stm);
            zis.getNextEntry();
            stm = zis;
        }
        while (true) {
            int bytecount = stm.read(buffer);
            if (bytecount > 0) {
                stmOut.write(buffer, 0, bytecount);
            } else {
                stmOut.close();
                stm.close();
                return true;
            }
        }
    }

    public void copyFile(InputStream is, File outputFile) {
        try {
            outputFile.createNewFile();
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));
            DataInputStream in = new DataInputStream(is);
            byte[] data = new byte[1024];
            while (true) {
                int b = in.read(data);
                if (b != -1) {
                    out.write(data);
                } else {
                    if (b == -1) {
                    }
                    out.flush();
                    out.close();
                    in.close();
                    return;
                }
            }
        } catch (IOException ex) {
            Log.e(TorConstants.TAG, "error copying binary", ex);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0026 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x006e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean isARMv6() {
        /*
            r4 = 0
            r3 = 1
            int r5 = org.torproject.android.service.TorBinaryInstaller.isARMv6
            r6 = -1
            if (r5 != r6) goto L22
            r1 = 0
            r5 = 0
            org.torproject.android.service.TorBinaryInstaller.isARMv6 = r5     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            java.io.FileReader r5 = new java.io.FileReader     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            java.lang.String r6 = "/proc/cpuinfo"
            r5.<init>(r6)     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            r2.<init>(r5)     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            java.lang.String r0 = r2.readLine()     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
        L1b:
            if (r0 != 0) goto L27
        L1d:
            if (r2 == 0) goto L22
            r2.close()     // Catch: java.lang.Exception -> L72
        L22:
            int r5 = org.torproject.android.service.TorBinaryInstaller.isARMv6
            if (r5 != r3) goto L6e
        L26:
            return r3
        L27:
            java.lang.String r5 = "Processor"
            boolean r5 = r0.startsWith(r5)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            if (r5 == 0) goto L45
            java.lang.String r5 = "ARMv6"
            boolean r5 = r0.contains(r5)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            if (r5 == 0) goto L45
            r5 = 1
            org.torproject.android.service.TorBinaryInstaller.isARMv6 = r5     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            goto L1d
        L3b:
            r5 = move-exception
            r1 = r2
        L3d:
            if (r1 == 0) goto L22
            r1.close()     // Catch: java.lang.Exception -> L43
            goto L22
        L43:
            r5 = move-exception
            goto L22
        L45:
            java.lang.String r5 = "CPU architecture"
            boolean r5 = r0.startsWith(r5)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            if (r5 == 0) goto L69
            java.lang.String r5 = "6TE"
            boolean r5 = r0.contains(r5)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            if (r5 != 0) goto L5d
            java.lang.String r5 = "5TE"
            boolean r5 = r0.contains(r5)     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            if (r5 == 0) goto L69
        L5d:
            r5 = 1
            org.torproject.android.service.TorBinaryInstaller.isARMv6 = r5     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            goto L1d
        L61:
            r3 = move-exception
            r1 = r2
        L63:
            if (r1 == 0) goto L68
            r1.close()     // Catch: java.lang.Exception -> L70
        L68:
            throw r3
        L69:
            java.lang.String r0 = r2.readLine()     // Catch: java.lang.Exception -> L3b java.lang.Throwable -> L61
            goto L1b
        L6e:
            r3 = r4
            goto L26
        L70:
            r4 = move-exception
            goto L68
        L72:
            r5 = move-exception
            goto L22
        L74:
            r3 = move-exception
            goto L63
        L76:
            r5 = move-exception
            goto L3d
        */
        throw new UnsupportedOperationException("Method not decompiled: org.torproject.android.service.TorBinaryInstaller.isARMv6():boolean");
    }

    private static void copyRawFile(Context ctx, int resid, File file, String mode, boolean isZipd) throws IOException, InterruptedException {
        String abspath = file.getAbsolutePath();
        FileOutputStream out = new FileOutputStream(file);
        InputStream is = ctx.getResources().openRawResource(resid);
        if (isZipd) {
            ZipInputStream zis = new ZipInputStream(is);
            zis.getNextEntry();
            is = zis;
        }
        byte[] buf = new byte[1024];
        while (true) {
            int len = is.read(buf);
            if (len > 0) {
                out.write(buf, 0, len);
            } else {
                out.close();
                is.close();
                Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
                return;
            }
        }
    }

    public static boolean assertIpTablesBinaries(Context ctx, boolean showErrors) throws Exception {
        File file = new File(ctx.getDir("bin", 0), "iptables");
        if (!file.exists() && isARMv6()) {
            copyRawFile(ctx, R.raw.iptables_g1, file, CHMOD_EXEC, false);
        }
        File file2 = new File(ctx.getDir("bin", 0), "iptables");
        if (!file2.exists() && !isARMv6()) {
            copyRawFile(ctx, R.raw.iptables_n1, file2, CHMOD_EXEC, false);
            return true;
        }
        return true;
    }
}