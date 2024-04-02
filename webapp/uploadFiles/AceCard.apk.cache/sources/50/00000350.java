package ch.boye.httpclientandroidlib.pool;

import ch.boye.httpclientandroidlib.concurrent.FutureCallback;
import java.util.concurrent.Future;

/* loaded from: classes.dex */
public interface ConnPool<T, E> {
    Future<E> lease(T t, Object obj, FutureCallback<E> futureCallback);

    void release(E e, boolean z);
}