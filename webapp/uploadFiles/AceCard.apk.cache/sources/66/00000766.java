package org.spongycastle.util;

import java.util.Collection;

/* loaded from: classes.dex */
public interface Store {
    Collection getMatches(Selector selector) throws StoreException;
}