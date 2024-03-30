package android.support.v4.media;
public final class MediaBrowserServiceCompat$BrowserRoot {
    public static final String EXTRA_OFFLINE = "android.service.media.extra.OFFLINE";
    public static final String EXTRA_RECENT = "android.service.media.extra.RECENT";
    public static final String EXTRA_SUGGESTED = "android.service.media.extra.SUGGESTED";
    public static final String EXTRA_SUGGESTION_KEYWORDS = "android.service.media.extra.SUGGESTION_KEYWORDS";
    private final android.os.Bundle mExtras;
    private final String mRootId;

    public MediaBrowserServiceCompat$BrowserRoot(String p3, android.os.Bundle p4)
    {
        if (p3 == null) {
            throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead.");
        } else {
            this.mRootId = p3;
            this.mExtras = p4;
            return;
        }
    }

    public android.os.Bundle getExtras()
    {
        return this.mExtras;
    }

    public String getRootId()
    {
        return this.mRootId;
    }
}
