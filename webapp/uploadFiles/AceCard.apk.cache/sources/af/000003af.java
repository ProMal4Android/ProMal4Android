package net.freehaven.tor.control;

/* loaded from: classes.dex */
public class ConfigEntry {
    public final boolean is_default;
    public final String key;
    public final String value;

    public ConfigEntry(String str, String str2) {
        this.key = str;
        this.value = str2;
        this.is_default = false;
    }

    public ConfigEntry(String str) {
        this.key = str;
        this.value = "";
        this.is_default = true;
    }
}