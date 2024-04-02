package org.spongycastle.crypto.params;

/* loaded from: classes.dex */
public class DHKeyParameters extends AsymmetricKeyParameter {
    private DHParameters params;

    /* JADX INFO: Access modifiers changed from: protected */
    public DHKeyParameters(boolean isPrivate, DHParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DHKeyParameters) {
            DHKeyParameters dhKey = (DHKeyParameters) obj;
            if (this.params == null) {
                return dhKey.getParameters() == null;
            }
            return this.params.equals(dhKey.getParameters());
        }
        return false;
    }

    public int hashCode() {
        int code = isPrivate() ? 0 : 1;
        if (this.params != null) {
            return code ^ this.params.hashCode();
        }
        return code;
    }
}