package com.alcineo.bonappetit.domain;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.alcineo.administrative.Kernel;
import com.alcineo.bonappetit.android.ConnectivityCheck;
import com.alcineo.bonappetit.android.HardwareCheck;
import com.alcineo.bonappetit.android.NfcCheck;
import com.alcineo.bonappetit.domain.example.LoadTerminalSettingsExample;
import com.alcineo.softpos.payment.api.DeviceInfoAPI;
import com.alcineo.softpos.payment.api.KernelsAdministrationAPI;
import com.alcineo.softpos.payment.api.SoftposAPI;
import com.alcineo.softpos.payment.api.SoftposPropertiesAPI;

import java.util.Arrays;
import java.util.Observable;

public class SoftposInitialization extends Observable {

    private static final String  TAG = SoftposInitialization.class.getSimpleName();
    private              boolean isDeviceReady;

    /**
     * Initialization process
     * Tasks are ordered and should not be reorganized
     */
    public SoftposInitialization(Context context) {
        new Thread(() -> {

            new ConnectivityCheck();
            new HardwareCheck(context);
            new NfcCheck(context);

            SoftposAPI.initialize(context);

            //new LoadTerminalSettingsExample(context);

            Log.i(TAG,
                    "\n-----------------------------------------------------" +
                    "\nDUID: " + Arrays.toString(DeviceInfoAPI.getDeviceUid()) +
//                    "\nConfigured Server hostname: " + AttestationAgent.getServerHostname() +
                    "\nSoftPOS library version: " + SoftposPropertiesAPI.getSoftposLibraryVersion() +
                    String.format("\nAndroid version %s, security patch %s", android.os.Build.VERSION.SDK_INT, Build.VERSION.SECURITY_PATCH.replaceAll("-", "")) +
                    "\n--------------------------------------------------------------------------");

            setChanged();
            isDeviceReady = true;
            notifyObservers(true);
        }).start();
    }

    public boolean isDeviceReady() {
        return isDeviceReady;
    }

}
