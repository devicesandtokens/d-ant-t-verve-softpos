package com.alcineo.bonappetit.domain.example;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.alcineo.administrative.FileId;
import com.alcineo.administrative.Kernel;
import com.alcineo.softpos.payment.api.KernelsAdministrationAPI;
import com.alcineo.softpos.payment.error.ExecutorException;
import com.alcineo.softpos.payment.model.SettingsSheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * LoadTerminalSettingsExample is used to demonstrate how to upload terminal settings into the native part.
 * <p>
 * In this example, we are writing assets/settings/TerminalSettings_pw.bin, and then overwriting it with assets/settings/TerminalSettings_pw2.bin.
 * <p>
 * The 0x9F4E tag is used to demonstrate the setting modification.
 */
public class LoadTerminalSettingsExample {
    private static final String TAG = LoadTerminalSettingsExample.class.getSimpleName();

    public LoadTerminalSettingsExample(Context context) {
        byte[] tag = new byte[]{(byte) 0x9F, 0x4E};

        try {
            final AssetManager assetManager = context.getAssets();
// change terminal settings
            final InputStream open2 = assetManager.open("settings/TerminalSettings_pw_9F4E_modify.bin");
            KernelsAdministrationAPI.uploadTerminalSettingsFile(FileId.PAYWAVE, open2);
            byte[] oldTag = KernelsAdministrationAPI.getTag(Kernel.K_PAYWAVE, tag, SettingsSheet.Terminal, (byte) 0);
            Log.d(TAG, "doWork: " + Arrays.toString(oldTag));

            // init with good terminal settings
            final InputStream open = assetManager.open("settings/TerminalSettings_pw.bin");
            KernelsAdministrationAPI.uploadTerminalSettingsFile(FileId.PAYWAVE, open);
            byte[] newTag = KernelsAdministrationAPI.getTag(Kernel.K_PAYWAVE, tag, SettingsSheet.Terminal, (byte) 0);
            Log.d(TAG, "doWork: " + Arrays.toString(newTag));

        } catch (ExecutorException | IOException e) {
            Log.e(TAG, "doWork: ", e);
        }
    }

}
