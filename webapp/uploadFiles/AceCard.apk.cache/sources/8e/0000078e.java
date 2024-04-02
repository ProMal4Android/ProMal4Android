package org.torproject.android.service;

import android.os.AsyncTask;

/* loaded from: classes.dex */
public class CheckBinariesAsyncTask extends AsyncTask<TorService, Integer, Long> {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Long doInBackground(TorService... torService) {
        try {
            torService[0].checkTorBinaries(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 100L;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onProgressUpdate(Integer... progress) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(Long result) {
    }
}