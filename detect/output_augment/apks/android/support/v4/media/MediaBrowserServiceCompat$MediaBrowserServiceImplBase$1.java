package android.support.v4.media;
 class MediaBrowserServiceCompat$MediaBrowserServiceImplBase$1 implements java.lang.Runnable {
    final synthetic android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplBase this$1;
    final synthetic android.support.v4.media.session.MediaSessionCompat$Token val$token;

    MediaBrowserServiceCompat$MediaBrowserServiceImplBase$1(android.support.v4.media.MediaBrowserServiceCompat$MediaBrowserServiceImplBase p1, android.support.v4.media.session.MediaSessionCompat$Token p2)
    {
        this.this$1 = p1;
        this.val$token = p2;
        return;
    }

    public void run()
    {
        java.util.Iterator v0_4 = this.this$1.this$0.mConnections.values().iterator();
        while (v0_4.hasNext()) {
            android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord v1_1 = ((android.support.v4.media.MediaBrowserServiceCompat$ConnectionRecord) v0_4.next());
            try {
                v1_1.callbacks.onConnect(v1_1.root.getRootId(), this.val$token, v1_1.root.getExtras());
            } catch (android.os.RemoteException v2) {
                String v4_2 = new StringBuilder();
                v4_2.append("Connection for ");
                v4_2.append(v1_1.pkg);
                v4_2.append(" is no longer valid.");
                android.util.Log.w("MBServiceCompat", v4_2.toString());
                v0_4.remove();
            }
        }
        return;
    }
}
