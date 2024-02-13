package com.alcineo.bonappetit.android;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class NfcCheck {

    private static final String TAG = NfcCheck.class.getSimpleName();

    public NfcCheck(Context context) {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            throw new RuntimeException("Nfc is not enabled.");
        }

        if (ContextCompat.checkSelfPermission(context, permission.NFC) == PackageManager.PERMISSION_DENIED) {
            throw new RuntimeException("NFC is not authorized in application permissions");
        }
        Log.d(TAG, "run: Nfc is present and ready to handle cards");
    }

}
