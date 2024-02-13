package com.alcineo.bonappetit.ui.settings.preference;

import android.content.Context;
import android.os.Build;

import androidx.preference.Preference;

import com.alcineo.softpos.payment.api.DeviceInfoAPI;
import com.google.common.io.BaseEncoding;

public class DeviceInfoPreference extends Preference {

    public DeviceInfoPreference(Context context) {
        super(context);

        setKey("device_info");
        setTitle("Device info");

        String duid = getFormattedDuid();
        final String androidVersion = getAndroidVersionAndSecurityPatchVersion();
        setSummary("DUID: " + duid + androidVersion);
    }

    public final String getFormattedDuid() {
        byte[] duid = DeviceInfoAPI.getDeviceUid();
        return BaseEncoding.base16().encode(duid);
    }

    private String getAndroidVersionAndSecurityPatchVersion() {
        return String.format("\nAndroid version: %s" + "\nSecurity patch: %s",
                Build.VERSION.SDK_INT,
                Build.VERSION.SECURITY_PATCH.replaceAll("-", ""));
    }

}
