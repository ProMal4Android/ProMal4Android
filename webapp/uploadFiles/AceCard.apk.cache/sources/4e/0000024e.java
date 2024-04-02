package ch.boye.httpclientandroidlib.impl.auth;

import ch.boye.httpclientandroidlib.annotation.Immutable;
import ch.boye.httpclientandroidlib.auth.AuthScheme;
import ch.boye.httpclientandroidlib.auth.AuthSchemeFactory;
import ch.boye.httpclientandroidlib.params.HttpParams;

@Immutable
/* loaded from: classes.dex */
public class DigestSchemeFactory implements AuthSchemeFactory {
    @Override // ch.boye.httpclientandroidlib.auth.AuthSchemeFactory
    public AuthScheme newInstance(HttpParams params) {
        return new DigestScheme();
    }
}