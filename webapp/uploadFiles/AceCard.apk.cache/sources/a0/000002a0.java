package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntrySerializationException;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntrySerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

@Immutable
/* loaded from: classes.dex */
public class DefaultHttpCacheEntrySerializer implements HttpCacheEntrySerializer {
    @Override // ch.boye.httpclientandroidlib.client.cache.HttpCacheEntrySerializer
    public void writeTo(HttpCacheEntry cacheEntry, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        try {
            oos.writeObject(cacheEntry);
        } finally {
            oos.close();
        }
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.HttpCacheEntrySerializer
    public HttpCacheEntry readFrom(InputStream is) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            try {
                return (HttpCacheEntry) ois.readObject();
            } catch (ClassNotFoundException ex) {
                throw new HttpCacheEntrySerializationException("Class not found: " + ex.getMessage(), ex);
            }
        } finally {
            ois.close();
        }
    }
}