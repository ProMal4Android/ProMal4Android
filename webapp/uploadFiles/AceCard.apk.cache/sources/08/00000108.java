package android.support.v4.view.accessibility;

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

/* loaded from: classes.dex */
class AccessibilityNodeInfoCompatJellyBean {
    AccessibilityNodeInfoCompatJellyBean() {
    }

    public static void addChild(Object info2, View child, int virtualDescendantId) {
        ((AccessibilityNodeInfo) info2).addChild(child, virtualDescendantId);
    }

    public static void setSource(Object info2, View root, int virtualDescendantId) {
        ((AccessibilityNodeInfo) info2).setSource(root, virtualDescendantId);
    }

    public static boolean isVisibleToUser(Object info2) {
        return ((AccessibilityNodeInfo) info2).isVisibleToUser();
    }

    public static void setVisibleToUser(Object info2, boolean visibleToUser) {
        ((AccessibilityNodeInfo) info2).setVisibleToUser(visibleToUser);
    }

    public static boolean performAction(Object info2, int action, Bundle arguments) {
        return ((AccessibilityNodeInfo) info2).performAction(action, arguments);
    }

    public static void setMovementGranularities(Object info2, int granularities) {
        ((AccessibilityNodeInfo) info2).setMovementGranularities(granularities);
    }

    public static int getMovementGranularities(Object info2) {
        return ((AccessibilityNodeInfo) info2).getMovementGranularities();
    }

    public static Object obtain(View root, int virtualDescendantId) {
        return AccessibilityNodeInfo.obtain(root, virtualDescendantId);
    }

    public static Object findFocus(Object info2, int focus) {
        return ((AccessibilityNodeInfo) info2).findFocus(focus);
    }

    public static Object focusSearch(Object info2, int direction) {
        return ((AccessibilityNodeInfo) info2).focusSearch(direction);
    }

    public static void setParent(Object info2, View root, int virtualDescendantId) {
        ((AccessibilityNodeInfo) info2).setParent(root, virtualDescendantId);
    }

    public static boolean isAccessibilityFocused(Object info2) {
        return ((AccessibilityNodeInfo) info2).isAccessibilityFocused();
    }

    public static void setAccesibilityFocused(Object info2, boolean focused) {
        ((AccessibilityNodeInfo) info2).setAccessibilityFocused(focused);
    }
}