package ch.boye.httpclientandroidlib.impl.client.cache;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.client.cache.InputLimit;
import ch.boye.httpclientandroidlib.client.cache.Resource;
import ch.boye.httpclientandroidlib.client.cache.ResourceFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Immutable
/* loaded from: classes.dex */
public class HeapResourceFactory implements ResourceFactory {
    @Override // ch.boye.httpclientandroidlib.client.cache.ResourceFactory
    public Resource generate(String requestId, InputStream instream, InputLimit limit) throws IOException {
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        long total = 0;
        while (true) {
            int l = instream.read(buf);
            if (l == -1) {
                break;
            }
            outstream.write(buf, 0, l);
            total += l;
            if (limit != null && total > limit.getValue()) {
                limit.reached();
                break;
            }
        }
        return new HeapResource(outstream.toByteArray());
    }

    @Override // ch.boye.httpclientandroidlib.client.cache.ResourceFactory
    public Resource copy(String requestId, Resource resource) throws IOException {
        byte[] body;
        if (resource instanceof HeapResource) {
            body = ((HeapResource) resource).getByteArray();
        } else {
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
            IOUtils.copyAndClose(resource.getInputStream(), outstream);
            body = outstream.toByteArray();
        }
        return new HeapResource(body);
    }
}