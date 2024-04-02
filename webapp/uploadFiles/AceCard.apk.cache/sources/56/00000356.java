package ch.boye.httpclientandroidlib.protocol;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicHttpContext implements HttpContext {
    private Map<String, Object> map;
    private final HttpContext parentContext;

    public BasicHttpContext() {
        this(null);
    }

    public BasicHttpContext(HttpContext parentContext) {
        this.map = null;
        this.parentContext = parentContext;
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public Object getAttribute(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        Object obj = null;
        if (this.map != null) {
            obj = this.map.get(id);
        }
        if (obj == null && this.parentContext != null) {
            return this.parentContext.getAttribute(id);
        }
        return obj;
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public void setAttribute(String id, Object obj) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (this.map == null) {
            this.map = new HashMap();
        }
        this.map.put(id, obj);
    }

    @Override // ch.boye.httpclientandroidlib.protocol.HttpContext
    public Object removeAttribute(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id may not be null");
        }
        if (this.map != null) {
            return this.map.remove(id);
        }
        return null;
    }

    public void clear() {
        if (this.map != null) {
            this.map.clear();
        }
    }

    public String toString() {
        return this.map != null ? this.map.toString() : "{}";
    }
}