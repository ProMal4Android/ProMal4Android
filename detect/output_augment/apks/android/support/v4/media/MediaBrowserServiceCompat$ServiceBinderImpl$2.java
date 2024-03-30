package android.support.v4.media;
 class MediaBrowserServiceCompat$ServiceBinderImpl$2 implements java.lang.Runnable {
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$ServiceBinderImpl this$1;
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacks val$callbacks;

    MediaBrowserServiceCompat$ServiceBinderImpl$2(android.support.v4.media.MediaBrowserServiceCompat$ServiceBinderImpl p1, android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacks p2)
    {
        this.this$1 = p1;
        this.val$callbacks = p2;
        return;
    }

    public void run()
    {
        this.this$1.this$0.mConnections.remove(this.val$callbacks.asBinder());
        return;
    }
}
