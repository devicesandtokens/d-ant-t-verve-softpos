package com.alcineo.bonappetit.android;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class HardwareCheck {

    private static final String TAG = HardwareCheck.class.getSimpleName();

    public HardwareCheck(Context context) {
        PackageManager packageManager = context.getPackageManager();

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            throw new RuntimeException("NFC is not present on this device");
        }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)) {
            throw new RuntimeException("OpenGL Extension pack not present on this device.");
        }
        Log.d(TAG, "run: Device contain valid hardware to can execute application");
    }

}
