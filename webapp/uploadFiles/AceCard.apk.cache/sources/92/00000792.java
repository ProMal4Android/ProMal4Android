package org.torproject.android.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface ITorServiceCallback extends IInterface {
    void logMessage(String str) throws RemoteException;

    void statusChanged(String str) throws RemoteException;

    void updateBandwidth(long j, long j2, long j3, long j4) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ITorServiceCallback {
        private static final String DESCRIPTOR = "org.torproject.android.service.ITorServiceCallback";
        static final int TRANSACTION_logMessage = 3;
        static final int TRANSACTION_statusChanged = 1;
        static final int TRANSACTION_updateBandwidth = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ITorServiceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ITorServiceCallback)) {
                return (ITorServiceCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0 = data.readString();
                    statusChanged(_arg0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    long _arg02 = data.readLong();
                    long _arg1 = data.readLong();
                    long _arg2 = data.readLong();
                    long _arg3 = data.readLong();
                    updateBandwidth(_arg02, _arg1, _arg2, _arg3);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    String _arg03 = data.readString();
                    logMessage(_arg03);
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements ITorServiceCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // org.torproject.android.service.ITorServiceCallback
            public void statusChanged(String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(value);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // org.torproject.android.service.ITorServiceCallback
            public void updateBandwidth(long upload, long download, long written, long read) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(upload);
                    _data.writeLong(download);
                    _data.writeLong(written);
                    _data.writeLong(read);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // org.torproject.android.service.ITorServiceCallback
            public void logMessage(String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(value);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }
    }
}