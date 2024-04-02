package ch.boye.httpclientandroidlib.conn.scheme;

import ch.boye.httpclientandroidlib.params.HttpParams;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

@Deprecated
/* loaded from: classes.dex */
class SchemeLayeredSocketFactoryAdaptor extends SchemeSocketFactoryAdaptor implements SchemeLayeredSocketFactory {
    private final LayeredSocketFactory factory;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SchemeLayeredSocketFactoryAdaptor(LayeredSocketFactory factory) {
        super(factory);
        this.factory = factory;
    }

    @Override // ch.boye.httpclientandroidlib.conn.scheme.SchemeLayeredSocketFactory
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpParams params) throws IOException, UnknownHostException {
        return this.factory.createSocket(socket, target, port, true);
    }
}