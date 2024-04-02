package ch.boye.httpclientandroidlib.impl.cookie;

import ch.boye.httpclientandroidlib.annotation.NotThreadSafe;
import ch.boye.httpclientandroidlib.cookie.SetCookie2;
import java.io.Serializable;
import java.util.Date;

@NotThreadSafe
/* loaded from: classes.dex */
public class BasicClientCookie2 extends BasicClientCookie implements SetCookie2, Serializable {
    private static final long serialVersionUID = -7744598295706617057L;
    private String commentURL;
    private boolean discard;
    private int[] ports;

    public BasicClientCookie2(String name, String value) {
        super(name, value);
    }

    @Override // ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie, ch.boye.httpclientandroidlib.cookie.Cookie
    public int[] getPorts() {
        return this.ports;
    }

    @Override // ch.boye.httpclientandroidlib.cookie.SetCookie2
    public void setPorts(int[] ports) {
        this.ports = ports;
    }

    @Override // ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie, ch.boye.httpclientandroidlib.cookie.Cookie
    public String getCommentURL() {
        return this.commentURL;
    }

    @Override // ch.boye.httpclientandroidlib.cookie.SetCookie2
    public void setCommentURL(String commentURL) {
        this.commentURL = commentURL;
    }

    @Override // ch.boye.httpclientandroidlib.cookie.SetCookie2
    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    @Override // ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie, ch.boye.httpclientandroidlib.cookie.Cookie
    public boolean isPersistent() {
        return !this.discard && super.isPersistent();
    }

    @Override // ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie, ch.boye.httpclientandroidlib.cookie.Cookie
    public boolean isExpired(Date date) {
        return this.discard || super.isExpired(date);
    }

    @Override // ch.boye.httpclientandroidlib.impl.cookie.BasicClientCookie
    public Object clone() throws CloneNotSupportedException {
        BasicClientCookie2 clone = (BasicClientCookie2) super.clone();
        if (this.ports != null) {
            clone.ports = (int[]) this.ports.clone();
        }
        return clone;
    }
}