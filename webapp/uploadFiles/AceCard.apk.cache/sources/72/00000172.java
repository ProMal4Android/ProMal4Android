package ch.boye.httpclientandroidlib.auth;

import ch.boye.httpclientandroidlib.params.HttpParams;

/* loaded from: classes.dex */
public interface AuthSchemeFactory {
    AuthScheme newInstance(HttpParams httpParams);
}