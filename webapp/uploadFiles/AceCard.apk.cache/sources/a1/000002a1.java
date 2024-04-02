package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.annotation.ThreadSafe;
import ch.boye.httpclientandroidlib.client.cache.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@ThreadSafe
/* loaded from: classes.dex */
public class FileResource implements Resource {
    private static final long serialVersionUID = 4132244415919043397L;
    private volatile boolean disposed = false;
    private final File file;

    public FileResource(File file) {
        this.file = file;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized File getFile() {
        return this.file;
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.Resource
    public synchronized InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.Resource
    public synchronized long length() {
        return this.file.length();
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.Resource
    public synchronized void dispose() {
        if (!this.disposed) {
            this.disposed = true;
            this.file.delete();
        }
    }
}