package org.spongycastle.util.test;

/* loaded from: classes.dex */
public class SimpleTestResult implements TestResult {
    private static final String SEPARATOR = System.getProperty("line.separator");
    private Throwable exception;
    private String message;
    private boolean success;

    public SimpleTestResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public SimpleTestResult(boolean success, String message, Throwable exception) {
        this.success = success;
        this.message = message;
        this.exception = exception;
    }

    public static TestResult successful(Test test, String message) {
        return new SimpleTestResult(true, test.getName() + ": " + message);
    }

    public static TestResult failed(Test test, String message) {
        return new SimpleTestResult(false, test.getName() + ": " + message);
    }

    public static TestResult failed(Test test, String message, Throwable t) {
        return new SimpleTestResult(false, test.getName() + ": " + message, t);
    }

    public static TestResult failed(Test test, String message, Object expected, Object found) {
        return failed(test, message + SEPARATOR + "Expected: " + expected + SEPARATOR + "Found   : " + found);
    }

    public static String failedMessage(String algorithm, String testName, String expected, String actual) {
        StringBuffer sb = new StringBuffer(algorithm);
        sb.append(" failing ").append(testName);
        sb.append(SEPARATOR).append("    expected: ").append(expected);
        sb.append(SEPARATOR).append("    got     : ").append(actual);
        return sb.toString();
    }

    @Override // org.spongycastle.util.test.TestResult
    public boolean isSuccessful() {
        return this.success;
    }

    @Override // org.spongycastle.util.test.TestResult
    public String toString() {
        return this.message;
    }

    @Override // org.spongycastle.util.test.TestResult
    public Throwable getException() {
        return this.exception;
    }
}