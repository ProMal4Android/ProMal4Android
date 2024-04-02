package android.support.v4.accessibilityservice;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ResolveInfo;

/* loaded from: classes.dex */
class AccessibilityServiceInfoCompatIcs {
    AccessibilityServiceInfoCompatIcs() {
    }

    public static boolean getCanRetrieveWindowContent(AccessibilityServiceInfo info2) {
        return info2.getCanRetrieveWindowContent();
    }

    public static String getDescription(AccessibilityServiceInfo info2) {
        return info2.getDescription();
    }

    public static String getId(AccessibilityServiceInfo info2) {
        return info2.getId();
    }

    public static ResolveInfo getResolveInfo(AccessibilityServiceInfo info2) {
        return info2.getResolveInfo();
    }

    public static String getSettingsActivityName(AccessibilityServiceInfo info2) {
        return info2.getSettingsActivityName();
    }
}