package android.support.v4.media;
 class MediaBrowserServiceCompat$MediaBrowserServiceImplBase$2 implements java.lang.Runnable {
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplBase this$1;
    final synthetic android.os.Bundle val$options;
    final synthetic String val$parentId;

    MediaBrowserServiceCompat$MediaBrowserServiceImplBase$2(android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplBase p1, String p2, android.os.Bundle p3)
    {
        this.this$1 = p1;
        this.val$parentId = p2;
        this.val$options = p3;
        return;
    }

    public void run()
    {
        java.util.Iterator v0_4 = this.this$1.this$0.mConnections.keySet().iterator();
        while (v0_4.hasNext()) {
            android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord v2_4 = ((android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord) this.this$1.this$0.mConnections.get(((android.os.IBinder) v0_4.next())));
            java.util.List v3_2 = ((java.util.List) v2_4.subscriptions.get(this.val$parentId));
            if (v3_2 == null) {
            } else {
                java.util.Iterator v4_1 = v3_2.iterator();
                while (v4_1.hasNext()) {
                    android.support.v4.util.Pair v5_2 = ((android.support.v4.util.Pair) v4_1.next());
                    if (!android.support.v4.media.MediaBrowserCompatUtils.hasDuplicatedItems(this.val$options, ((android.os.Bundle) v5_2.second))) {
                    } else {
                        this.this$1.this$0.performLoadChildren(this.val$parentId, v2_4, ((android.os.Bundle) v5_2.second));
                    }
                }
            }
        }
        return;
    }
}
