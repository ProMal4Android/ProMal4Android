package org.spongycastle.crypto.params;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.math.ntru.polynomial.IntegerPolynomial;

/* loaded from: classes.dex */
public class NTRUSigningPublicKeyParameters extends AsymmetricKeyParameter {
    public IntegerPolynomial h;
    private NTRUSigningParameters params;

    public NTRUSigningPublicKeyParameters(IntegerPolynomial h, NTRUSigningParameters params) {
        super(false);
        this.h = h;
        this.params = params;
    }

    public NTRUSigningPublicKeyParameters(byte[] b, NTRUSigningParameters params) {
        super(false);
        this.h = IntegerPolynomial.fromBinary(b, params.N, params.q);
        this.params = params;
    }

    public NTRUSigningPublicKeyParameters(InputStream is, NTRUSigningParameters params) throws IOException {
        super(false);
        this.h = IntegerPolynomial.fromBinary(is, params.N, params.q);
        this.params = params;
    }

    public byte[] getEncoded() {
        return this.h.toBinary(this.params.q);
    }

    public void writeTo(OutputStream os) throws IOException {
        os.write(getEncoded());
    }

    public int hashCode() {
        int result = (this.h == null ? 0 : this.h.hashCode()) + 31;
        return (result * 31) + (this.params != null ? this.params.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            NTRUSigningPublicKeyParameters other = (NTRUSigningPublicKeyParameters) obj;
            if (this.h == null) {
                if (other.h != null) {
                    return false;
                }
            } else if (!this.h.equals(other.h)) {
                return false;
            }
            return this.params == null ? other.params == null : this.params.equals(other.params);
        }
        return false;
    }
}