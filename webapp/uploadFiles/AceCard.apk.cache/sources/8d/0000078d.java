package org.torproject.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class Utils {
    public static String readString(InputStream stream) {
        StringBuffer out = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                out.append(line);
                out.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public static String loadTextFile(String path) {
        StringBuffer out = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                out.append(line);
                out.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public static boolean saveTextFile(String path, String contents) {
        try {
            FileWriter writer = new FileWriter(path, false);
            writer.write(contents);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}