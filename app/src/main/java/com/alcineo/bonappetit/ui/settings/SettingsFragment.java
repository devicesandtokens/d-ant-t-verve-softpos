package com.alcineo.bonappetit.ui.settings;

import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.alcineo.bonappetit.ui.settings.preference.ApplicationVersionPreference;
import com.alcineo.bonappetit.ui.settings.preference.CurrencyPreference;
import com.alcineo.bonappetit.ui.settings.preference.DeviceInfoPreference;
import com.alcineo.bonappetit.ui.settings.preference.FileUploadPreference;
import com.alcineo.bonappetit.ui.settings.preference.ProductVersionPreference;
import com.alcineo.bonappetit.ui.settings.preference.SignaturePreference;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class containing all code for Preferences fragment UI is created here as we need to interact with services when some
 * buttons are fired
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private PreferenceScreen screen;
    private Preference serverVersionPref;
    private FileUploadPreference fileUploadPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        screen = getPreferenceManager().createPreferenceScreen(requireContext());

        addGlobalCategory();
        addUploadFileCategory();
        addSoftposCategory();

        setPreferenceScreen(screen);
    }

    private void addGlobalCategory() {
        PreferenceCategory globalCategory = new PreferenceCategory(requireContext());
        globalCategory.setKey("global_category");
        globalCategory.setTitle("Global");
        screen.addPreference(globalCategory);

        globalCategory.addPreference(new CurrencyPreference(requireContext()));
    }

    private void addUploadFileCategory() {
        PreferenceCategory settingsFileUploadCategory = new PreferenceCategory(requireContext());
        settingsFileUploadCategory.setKey("fileupload_category");
        settingsFileUploadCategory.setTitle("Settings file upload");
        screen.addPreference(settingsFileUploadCategory);

        fileUploadPreference = new FileUploadPreference(requireContext());
        fileUploadPreference.setKey("upload_settings_file");
        fileUploadPreference.setTitle("Upload settings file");
        fileUploadPreference.setSummary("Upload a settings file to the terminal");
        fileUploadPreference.setOnPreferenceClickListener(fileUploadPreference);

        final ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
            fileUploadPreference.setFilePath(result);
        });

        fileUploadPreference.registerFileIdPathOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityResultLauncher.launch(new String[]{"*/*"});
            }
        });

        settingsFileUploadCategory.addPreference(fileUploadPreference);
    }

    private void addSoftposCategory() {
        PreferenceCategory softposInfo = new PreferenceCategory(requireContext());
        softposInfo.setKey("softpos_category");
        softposInfo.setTitle("SoftPOS info");
        screen.addPreference(softposInfo);

        softposInfo.addPreference(new ApplicationVersionPreference(requireContext()));
        softposInfo.addPreference(new ProductVersionPreference(requireContext()));
        softposInfo.addPreference(new SignaturePreference(requireContext()));
        softposInfo.addPreference(new DeviceInfoPreference(requireContext()));
    }

}
