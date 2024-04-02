package ch.boye.httpclientandroidlib.conn;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.entity.HttpEntityWrapper;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicManagedEntity extends HttpEntityWrapper implements ConnectionReleaseTrigger, EofSensorWatcher {
    protected final boolean attemptReuse;
    protected ManagedClientConnection managedConn;

    public BasicManagedEntity(HttpEntity entity, ManagedClientConnection conn, boolean reuse) {
        super(entity);
        if (conn == null) {
            throw new IllegalArgumentException("Connection may not be null.");
        }
        this.managedConn = conn;
        this.attemptReuse = reuse;
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public boolean isRepeatable() {
        return false;
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public InputStream getContent() throws IOException {
        return new EofSensorInputStream(this.wrappedEntity.getContent(), this);
    }

    private void ensureConsumed() throws IOException {
        if (this.managedConn != null) {
            try {
                if (this.attemptReuse) {
                    EntityUtils.consume(this.wrappedEntity);
                    this.managedConn.markReusable();
                }
            } finally {
                releaseManagedConnection();
            }
        }
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public void consumeContent() throws IOException {
        ensureConsumed();
    }

    @Override // ch.boye.httpclientandroidlib.entity.HttpEntityWrapper, ch.boye.httpclientandroidlib.HttpEntity
    public void writeTo(OutputStream outstream) throws IOException {
        super.writeTo(outstream);
        ensureConsumed();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ConnectionReleaseTrigger
    public void releaseConnection() throws IOException {
        ensureConsumed();
    }

    @Override // ch.boye.httpclientandroidlib.conn.ConnectionReleaseTrigger
    public void abortConnection() throws IOException {
        if (this.managedConn != null) {
            try {
                this.managedConn.abortConnection();
            } finally {
                this.managedConn = null;
            }
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.EofSensorWatcher
    public boolean eofDetected(InputStream wrapped) throws IOException {
        try {
            if (this.attemptReuse && this.managedConn != null) {
                wrapped.close();
                this.managedConn.markReusable();
            }
            releaseManagedConnection();
            return false;
        } catch (Throwable th) {
            releaseManagedConnection();
            throw th;
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.EofSensorWatcher
    public boolean streamClosed(InputStream wrapped) throws IOException {
        try {
            if (this.attemptReuse && this.managedConn != null) {
                boolean valid = this.managedConn.isOpen();
                try {
                    wrapped.close();
                    this.managedConn.markReusable();
                } catch (SocketException ex) {
                    if (valid) {
                        throw ex;
                    }
                }
            }
            releaseManagedConnection();
            return false;
        } catch (Throwable th) {
            releaseManagedConnection();
            throw th;
        }
    }

    @Override // ch.boye.httpclientandroidlib.conn.EofSensorWatcher
    public boolean streamAbort(InputStream wrapped) throws IOException {
        if (this.managedConn != null) {
            this.managedConn.abortConnection();
            return false;
        }
        return false;
    }

    protected void releaseManagedConnection() throws IOException {
        if (this.managedConn != null) {
            try {
                this.managedConn.releaseConnection();
            } finally {
                this.managedConn = null;
            }
        }
    }
}