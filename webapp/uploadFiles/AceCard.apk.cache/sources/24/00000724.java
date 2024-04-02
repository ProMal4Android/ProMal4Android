package org.spongycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.Mac;
import org.spongycastle.crypto.macs.HMac;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;

/* loaded from: classes.dex */
public class TlsMac {
    protected TlsClientContext context;
    protected Mac mac;
    protected byte[] secret;
    protected long seqNo = 0;

    public TlsMac(TlsClientContext context, Digest digest, byte[] key_block, int offset, int len) {
        this.context = context;
        KeyParameter param = new KeyParameter(key_block, offset, len);
        this.secret = Arrays.clone(param.getKey());
        boolean isTls = context.getServerVersion().getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        if (isTls) {
            this.mac = new HMac(digest);
        } else {
            this.mac = new SSL3Mac(digest);
        }
        this.mac.init(param);
    }

    public byte[] getMACSecret() {
        return this.secret;
    }

    public long getSequenceNumber() {
        return this.seqNo;
    }

    public void incSequenceNumber() {
        this.seqNo++;
    }

    public int getSize() {
        return this.mac.getMacSize();
    }

    public byte[] calculateMac(short type, byte[] message, int offset, int len) {
        ProtocolVersion serverVersion = this.context.getServerVersion();
        boolean isTls = serverVersion.getFullVersion() >= ProtocolVersion.TLSv10.getFullVersion();
        ByteArrayOutputStream bosMac = new ByteArrayOutputStream(isTls ? 13 : 11);
        try {
            long j = this.seqNo;
            this.seqNo = 1 + j;
            TlsUtils.writeUint64(j, bosMac);
            TlsUtils.writeUint8(type, bosMac);
            if (isTls) {
                TlsUtils.writeVersion(serverVersion, bosMac);
            }
            TlsUtils.writeUint16(len, bosMac);
            byte[] macHeader = bosMac.toByteArray();
            this.mac.update(macHeader, 0, macHeader.length);
            this.mac.update(message, offset, len);
            byte[] result = new byte[this.mac.getMacSize()];
            this.mac.doFinal(result, 0);
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Internal error during mac calculation");
        }
    }
}