package org.spongycastle.util.test;

/* loaded from: classes.dex */
public class TestFailedException extends RuntimeException {
    private TestResult _result;

    public TestFailedException(TestResult result) {
        this._result = result;
    }

    public TestResult getResult() {
        return this._result;
    }
}