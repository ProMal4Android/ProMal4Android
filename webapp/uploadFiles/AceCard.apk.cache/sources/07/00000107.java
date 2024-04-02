package android.support.v4.view.accessibility;

import android.graphics.Rect;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

/* loaded from: classes.dex */
class AccessibilityNodeInfoCompatIcs {
    AccessibilityNodeInfoCompatIcs() {
    }

    public static Object obtain() {
        return AccessibilityNodeInfo.obtain();
    }

    public static Object obtain(View source) {
        return AccessibilityNodeInfo.obtain(source);
    }

    public static Object obtain(Object info2) {
        return AccessibilityNodeInfo.obtain((AccessibilityNodeInfo) info2);
    }

    public static void addAction(Object info2, int action) {
        ((AccessibilityNodeInfo) info2).addAction(action);
    }

    public static void addChild(Object info2, View child) {
        ((AccessibilityNodeInfo) info2).addChild(child);
    }

    public static List<Object> findAccessibilityNodeInfosByText(Object info2, String text) {
        List result = ((AccessibilityNodeInfo) info2).findAccessibilityNodeInfosByText(text);
        return result;
    }

    public static int getActions(Object info2) {
        return ((AccessibilityNodeInfo) info2).getActions();
    }

    public static void getBoundsInParent(Object info2, Rect outBounds) {
        ((AccessibilityNodeInfo) info2).getBoundsInParent(outBounds);
    }

    public static void getBoundsInScreen(Object info2, Rect outBounds) {
        ((AccessibilityNodeInfo) info2).getBoundsInScreen(outBounds);
    }

    public static Object getChild(Object info2, int index) {
        return ((AccessibilityNodeInfo) info2).getChild(index);
    }

    public static int getChildCount(Object info2) {
        return ((AccessibilityNodeInfo) info2).getChildCount();
    }

    public static CharSequence getClassName(Object info2) {
        return ((AccessibilityNodeInfo) info2).getClassName();
    }

    public static CharSequence getContentDescription(Object info2) {
        return ((AccessibilityNodeInfo) info2).getContentDescription();
    }

    public static CharSequence getPackageName(Object info2) {
        return ((AccessibilityNodeInfo) info2).getPackageName();
    }

    public static Object getParent(Object info2) {
        return ((AccessibilityNodeInfo) info2).getParent();
    }

    public static CharSequence getText(Object info2) {
        return ((AccessibilityNodeInfo) info2).getText();
    }

    public static int getWindowId(Object info2) {
        return ((AccessibilityNodeInfo) info2).getWindowId();
    }

    public static boolean isCheckable(Object info2) {
        return ((AccessibilityNodeInfo) info2).isCheckable();
    }

    public static boolean isChecked(Object info2) {
        return ((AccessibilityNodeInfo) info2).isChecked();
    }

    public static boolean isClickable(Object info2) {
        return ((AccessibilityNodeInfo) info2).isClickable();
    }

    public static boolean isEnabled(Object info2) {
        return ((AccessibilityNodeInfo) info2).isEnabled();
    }

    public static boolean isFocusable(Object info2) {
        return ((AccessibilityNodeInfo) info2).isFocusable();
    }

    public static boolean isFocused(Object info2) {
        return ((AccessibilityNodeInfo) info2).isFocused();
    }

    public static boolean isLongClickable(Object info2) {
        return ((AccessibilityNodeInfo) info2).isLongClickable();
    }

    public static boolean isPassword(Object info2) {
        return ((AccessibilityNodeInfo) info2).isPassword();
    }

    public static boolean isScrollable(Object info2) {
        return ((AccessibilityNodeInfo) info2).isScrollable();
    }

    public static boolean isSelected(Object info2) {
        return ((AccessibilityNodeInfo) info2).isSelected();
    }

    public static boolean performAction(Object info2, int action) {
        return ((AccessibilityNodeInfo) info2).performAction(action);
    }

    public static void setBoundsInParent(Object info2, Rect bounds) {
        ((AccessibilityNodeInfo) info2).setBoundsInParent(bounds);
    }

    public static void setBoundsInScreen(Object info2, Rect bounds) {
        ((AccessibilityNodeInfo) info2).setBoundsInScreen(bounds);
    }

    public static void setCheckable(Object info2, boolean checkable) {
        ((AccessibilityNodeInfo) info2).setCheckable(checkable);
    }

    public static void setChecked(Object info2, boolean checked) {
        ((AccessibilityNodeInfo) info2).setChecked(checked);
    }

    public static void setClassName(Object info2, CharSequence className) {
        ((AccessibilityNodeInfo) info2).setClassName(className);
    }

    public static void setClickable(Object info2, boolean clickable) {
        ((AccessibilityNodeInfo) info2).setClickable(clickable);
    }

    public static void setContentDescription(Object info2, CharSequence contentDescription) {
        ((AccessibilityNodeInfo) info2).setContentDescription(contentDescription);
    }

    public static void setEnabled(Object info2, boolean enabled) {
        ((AccessibilityNodeInfo) info2).setEnabled(enabled);
    }

    public static void setFocusable(Object info2, boolean focusable) {
        ((AccessibilityNodeInfo) info2).setFocusable(focusable);
    }

    public static void setFocused(Object info2, boolean focused) {
        ((AccessibilityNodeInfo) info2).setFocused(focused);
    }

    public static void setLongClickable(Object info2, boolean longClickable) {
        ((AccessibilityNodeInfo) info2).setLongClickable(longClickable);
    }

    public static void setPackageName(Object info2, CharSequence packageName) {
        ((AccessibilityNodeInfo) info2).setPackageName(packageName);
    }

    public static void setParent(Object info2, View parent) {
        ((AccessibilityNodeInfo) info2).setParent(parent);
    }

    public static void setPassword(Object info2, boolean password) {
        ((AccessibilityNodeInfo) info2).setPassword(password);
    }

    public static void setScrollable(Object info2, boolean scrollable) {
        ((AccessibilityNodeInfo) info2).setScrollable(scrollable);
    }

    public static void setSelected(Object info2, boolean selected) {
        ((AccessibilityNodeInfo) info2).setSelected(selected);
    }

    public static void setSource(Object info2, View source) {
        ((AccessibilityNodeInfo) info2).setSource(source);
    }

    public static void setText(Object info2, CharSequence text) {
        ((AccessibilityNodeInfo) info2).setText(text);
    }

    public static void recycle(Object info2) {
        ((AccessibilityNodeInfo) info2).recycle();
    }
}