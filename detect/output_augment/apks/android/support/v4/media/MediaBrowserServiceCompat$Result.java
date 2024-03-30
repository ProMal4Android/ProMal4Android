package android.support.v4.media;
public class MediaBrowserServiceCompat$Result {
    private final Object mDebug;
    private boolean mDetachCalled;
    private int mFlags;
    private boolean mSendErrorCalled;
    private boolean mSendProgressUpdateCalled;
    private boolean mSendResultCalled;

    MediaBrowserServiceCompat$Result(Object p1)
    {
        this.mDebug = p1;
        return;
    }

    private void checkExtraFields(android.os.Bundle p4)
    {
        if (p4 != null) {
            if (!p4.containsKey("android.media.browse.extra.DOWNLOAD_PROGRESS")) {
            } else {
                float v0_3 = p4.getFloat("android.media.browse.extra.DOWNLOAD_PROGRESS");
                if ((v0_3 < -1222130260) || (v0_3 > 1065353300)) {
                    throw new IllegalArgumentException("The value of the EXTRA_DOWNLOAD_PROGRESS field must be a float number within [0.0, 1.0].");
                }
            }
            return;
        } else {
            return;
        }
    }

    public void detach()
    {
        if (this.mDetachCalled) {
            String v1_8 = new StringBuilder();
            v1_8.append("detach() called when detach() had already been called for: ");
            v1_8.append(this.mDebug);
            throw new IllegalStateException(v1_8.toString());
        } else {
            if (this.mSendResultCalled) {
                String v1_2 = new StringBuilder();
                v1_2.append("detach() called when sendResult() had already been called for: ");
                v1_2.append(this.mDebug);
                throw new IllegalStateException(v1_2.toString());
            } else {
                if (this.mSendErrorCalled) {
                    String v1_6 = new StringBuilder();
                    v1_6.append("detach() called when sendError() had already been called for: ");
                    v1_6.append(this.mDebug);
                    throw new IllegalStateException(v1_6.toString());
                } else {
                    this.mDetachCalled = 1;
                    return;
                }
            }
        }
    }

    int getFlags()
    {
        return this.mFlags;
    }

    boolean isDone()
    {
        if ((!this.mDetachCalled) && ((!this.mSendResultCalled) && (!this.mSendErrorCalled))) {
            int v0_3 = 0;
        } else {
            v0_3 = 1;
        }
        return v0_3;
    }

    void onErrorSent(android.os.Bundle p4)
    {
        String v1_1 = new StringBuilder();
        v1_1.append("It is not supported to send an error for ");
        v1_1.append(this.mDebug);
        throw new UnsupportedOperationException(v1_1.toString());
    }

    void onProgressUpdateSent(android.os.Bundle p4)
    {
        String v1_1 = new StringBuilder();
        v1_1.append("It is not supported to send an interim update for ");
        v1_1.append(this.mDebug);
        throw new UnsupportedOperationException(v1_1.toString());
    }

    void onResultSent(Object p1)
    {
        return;
    }

    public void sendError(android.os.Bundle p4)
    {
        if ((this.mSendResultCalled) || (this.mSendErrorCalled)) {
            String v1_2 = new StringBuilder();
            v1_2.append("sendError() called when either sendResult() or sendError() had already been called for: ");
            v1_2.append(this.mDebug);
            throw new IllegalStateException(v1_2.toString());
        } else {
            this.mSendErrorCalled = 1;
            this.onErrorSent(p4);
            return;
        }
    }

    public void sendProgressUpdate(android.os.Bundle p4)
    {
        if ((this.mSendResultCalled) || (this.mSendErrorCalled)) {
            String v1_2 = new StringBuilder();
            v1_2.append("sendProgressUpdate() called when either sendResult() or sendError() had already been called for: ");
            v1_2.append(this.mDebug);
            throw new IllegalStateException(v1_2.toString());
        } else {
            this.checkExtraFields(p4);
            this.mSendProgressUpdateCalled = 1;
            this.onProgressUpdateSent(p4);
            return;
        }
    }

    public void sendResult(Object p4)
    {
        if ((this.mSendResultCalled) || (this.mSendErrorCalled)) {
            String v1_2 = new StringBuilder();
            v1_2.append("sendResult() called when either sendResult() or sendError() had already been called for: ");
            v1_2.append(this.mDebug);
            throw new IllegalStateException(v1_2.toString());
        } else {
            this.mSendResultCalled = 1;
            this.onResultSent(p4);
            return;
        }
    }

    void setFlags(int p1)
    {
        this.mFlags = p1;
        return;
    }
}
