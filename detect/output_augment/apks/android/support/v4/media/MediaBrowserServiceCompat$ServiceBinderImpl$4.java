package android.support.v4.media;
 class MediaBrowserServiceCompat$ServiceBinderImpl$4 implements java.lang.Runnable {
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$ServiceBinderImpl this$1;
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacks val$callbacks;
    final synthetic String val$id;
    final synthetic android.os.IBinder val$token;

    MediaBrowserServiceCompat$ServiceBinderImpl$4(android.support.v4.media.MediaBrowserServiceCompat$ServiceBinderImpl p1, android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacks p2, String p3, android.os.IBinder p4)
    {
        this.this$1 = p1;
        this.val$callbacks = p2;
        this.val$id = p3;
        this.val$token = p4;
        return;
    }

    public void run()
    {
        android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord v1_4 = ((android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord) this.this$1.this$0.mConnections.get(this.val$callbacks.asBinder()));
        if (v1_4 != null) {
            if (this.this$1.this$0.removeSubscription(this.val$id, v1_4, this.val$token)) {
            } else {
                String v3_2 = new StringBuilder();
                v3_2.append("removeSubscription called for ");
                v3_2.append(this.val$id);
                v3_2.append(" which is not subscribed");
                android.util.Log.w("MBServiceCompat", v3_2.toString());
            }
            return;
        } else {
            String v3_5 = new StringBuilder();
            v3_5.append("removeSubscription for callback that isn\'t registered id=");
            v3_5.append(this.val$id);
            android.util.Log.w("MBServiceCompat", v3_5.toString());
            return;
        }
    }
}
