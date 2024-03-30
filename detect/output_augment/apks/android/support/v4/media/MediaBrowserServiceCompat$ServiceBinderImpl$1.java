package android.support.v4.media;
 class MediaBrowserServiceCompat$ServiceBinderImpl$1 implements java.lang.Runnable {
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$ServiceBinderImpl this$1;
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacks val$callbacks;
    final synthetic String val$pkg;
    final synthetic android.os.Bundle val$rootHints;
    final synthetic int val$uid;

    MediaBrowserServiceCompat$ServiceBinderImpl$1(android.support.v4.media.MediaBrowserServiceCompat$ServiceBinderImpl p1, android.support.v4.media.MediaBrowserServiceCompat$ServiceCallbacks p2, String p3, android.os.Bundle p4, int p5)
    {
        this.this$1 = p1;
        this.val$callbacks = p2;
        this.val$pkg = p3;
        this.val$rootHints = p4;
        this.val$uid = p5;
        return;
    }

    public void run()
    {
        android.os.IBinder v0_1 = this.val$callbacks.asBinder();
        this.this$1.this$0.mConnections.remove(v0_1);
        android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord v1_4 = new android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord();
        v1_4.pkg = this.val$pkg;
        v1_4.rootHints = this.val$rootHints;
        v1_4.callbacks = this.val$callbacks;
        v1_4.root = this.this$1.this$0.onGetRoot(this.val$pkg, this.val$uid, this.val$rootHints);
        if (v1_4.root != null) {
            try {
                this.this$1.this$0.mConnections.put(v0_1, v1_4);
            } catch (android.os.RemoteException v2) {
                String v4_2 = new StringBuilder();
                v4_2.append("Calling onConnect() failed. Dropping client. pkg=");
                v4_2.append(this.val$pkg);
                android.util.Log.w("MBServiceCompat", v4_2.toString());
                this.this$1.this$0.mConnections.remove(v0_1);
            }
            if (this.this$1.this$0.mSession == null) {
            } else {
                this.val$callbacks.onConnect(v1_4.root.getRootId(), this.this$1.this$0.mSession, v1_4.root.getExtras());
            }
        } else {
            String v3_8 = new StringBuilder();
            v3_8.append("No root for client ");
            v3_8.append(this.val$pkg);
            v3_8.append(" from service ");
            v3_8.append(this.getClass().getName());
            android.util.Log.i("MBServiceCompat", v3_8.toString());
            try {
                this.val$callbacks.onConnectFailed();
            } catch (android.os.RemoteException v2) {
                String v4_13 = new StringBuilder();
                v4_13.append("Calling onConnectFailed() failed. Ignoring. pkg=");
                v4_13.append(this.val$pkg);
                android.util.Log.w("MBServiceCompat", v4_13.toString());
            }
        }
        return;
    }
}
