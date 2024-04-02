package info.guardianproject.onionkit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import ch.boye.httpclientandroidlib.cookie.ClientCookie;

/* loaded from: classes.dex */
public class CertDisplayActivity extends Activity {
    private AlertDialog ad;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String issuer = getIntent().getStringExtra("issuer");
        String fingerprint = getIntent().getStringExtra("fingerprint");
        String subject = getIntent().getStringExtra("subject");
        String issuedOn = getIntent().getStringExtra("issued");
        String expiresOn = getIntent().getStringExtra(ClientCookie.EXPIRES_ATTR);
        String msg = getIntent().getStringExtra("msg");
        StringBuilder sb = new StringBuilder();
        if (msg != null) {
            sb.append(msg).append("\n\n");
        }
        if (subject != null) {
            sb.append("Certificate: ").append(subject).append("\n\n");
        }
        if (issuer != null) {
            sb.append("Issued by: ").append(issuer).append("\n\n");
        }
        if (fingerprint != null) {
            sb.append("SHA1 Fingerprint: ").append(fingerprint).append("\n\n");
        }
        if (issuedOn != null) {
            sb.append("Issued: ").append(issuedOn).append("\n\n");
        }
        if (expiresOn != null) {
            sb.append("Expires: ").append(expiresOn).append("\n\n");
        }
        showDialog(sb.toString());
    }

    private void showDialog(String msg) {
        this.ad = new AlertDialog.Builder(this).setTitle("Certificate Info").setMessage(msg).show();
        this.ad.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: info.guardianproject.onionkit.ui.CertDisplayActivity.1
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface arg0) {
                CertDisplayActivity.this.finish();
            }
        });
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        if (this.ad != null) {
            this.ad.cancel();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (this.ad != null) {
            this.ad.cancel();
        }
    }
}