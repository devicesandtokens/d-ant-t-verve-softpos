package com.alcineo.bonappetit.ui.settings.preference;

import android.content.Context;

import androidx.preference.Preference;

import com.alcineo.bonappetit.BuildConfig;
import com.alcineo.softpos.payment.api.SoftposPropertiesAPI;

public class ApplicationVersionPreference extends Preference {

    public ApplicationVersionPreference(Context context) {
        super(context);

        setKey("versions");
        setTitle("Versions");
        setSummary(String.format("Bon Appetit V%s\nSoftPOS Lib V%s", BuildConfig.VERSION_NAME, SoftposPropertiesAPI.getSoftposLibraryVersion()));
    }

}
