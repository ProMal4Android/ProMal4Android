package net.freehaven.tor.control;

import java.util.List;

/* loaded from: classes.dex */
public interface EventHandler {
    void bandwidthUsed(long j, long j2);

    void circuitStatus(String str, String str2, String str3);

    void message(String str, String str2);

    void newDescriptors(List<String> list);

    void orConnStatus(String str, String str2);

    void streamStatus(String str, String str2, String str3);

    void unrecognized(String str, String str2);
}