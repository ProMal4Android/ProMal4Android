package org.spongycastle.util.io.pem;

import java.io.IOException;

/* loaded from: classes.dex */
public interface PemObjectParser {
    Object parseObject(PemObject pemObject) throws IOException;
}