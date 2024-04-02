package net.freehaven.tor.control;

/* loaded from: classes.dex */
public class TorControlError extends RuntimeException {
    static final long serialVersionUID = 2;
    int errorType;

    public TorControlError(int i, String str) {
        super(str);
        this.errorType = i;
    }

    public TorControlError(String str) {
        this(-1, str);
    }

    public int getErrorType() {
        return this.errorType;
    }

    public String getErrorMsg() {
        try {
            if (this.errorType == -1) {
                return null;
            }
            return TorControlCommands.ERROR_MSGS[this.errorType];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Unrecongized error #" + this.errorType;
        }
    }
}