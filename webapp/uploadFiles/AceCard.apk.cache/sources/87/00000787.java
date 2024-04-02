package org.spongycastle.util.test;

/* loaded from: classes.dex */
public interface TestResult {
    Throwable getException();

    boolean isSuccessful();

    String toString();
}