package org.spongycastle.crypto.params;

/* loaded from: classes.dex */
public class ElGamalKeyParameters extends AsymmetricKeyParameter {
    private ElGamalParameters params;

    /* JADX INFO: Access modifiers changed from: protected */
    public ElGamalKeyParameters(boolean isPrivate, ElGamalParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public ElGamalParameters getParameters() {
        return this.params;
    }

    public int hashCode() {
        if (this.params != null) {
            return this.params.hashCode();
        }
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ElGamalKeyParameters) {
            ElGamalKeyParameters dhKey = (ElGamalKeyParameters) obj;
            if (this.params == null) {
                return dhKey.getParameters() == null;
            }
            return this.params.equals(dhKey.getParameters());
        }
        return false;
    }
}