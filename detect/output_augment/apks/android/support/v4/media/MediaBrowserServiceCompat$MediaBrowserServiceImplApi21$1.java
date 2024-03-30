package android.support.v4.media;
 class MediaBrowserServiceCompat$MediaBrowserServiceImplApi21$1 implements java.lang.Runnable {
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplApi21 this$1;
    final synthetic android.support.v4.media.session.MediaSessionCompat$Token val$token;

    MediaBrowserServiceCompat$MediaBrowserServiceImplApi21$1(android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplApi21 p1, android.support.v4.media.session.MediaSessionCompat$Token p2)
    {
        this.this$1 = p1;
        this.val$token = p2;
        return;
    }

    public void run()
    {
        if (this.this$1.mRootExtrasList.isEmpty()) {
        } else {
            Object v0_6 = this.val$token.getExtraBinder();
            if (v0_6 == null) {
            } else {
                java.util.List v1_2 = this.this$1.mRootExtrasList.iterator();
                while (v1_2.hasNext()) {
                    android.support.v4.app.BundleCompat.putBinder(((android.os.Bundle) v1_2.next()), "extra_session_binder", v0_6.asBinder());
                }
            }
            this.this$1.mRootExtrasList.clear();
        }
        android.support.v4.media.MediaBrowserServiceCompatApi21.setSessionToken(this.this$1.mServiceObj, this.val$token.getToken());
        return;
    }
}
