package com.alcineo.bonappetit.ui.settings.preference;

import android.content.Context;

import androidx.preference.Preference;

import com.alcineo.softpos.payment.api.ProductionVersionAPI;
import com.google.common.io.BaseEncoding;

public class ProductVersionPreference extends Preference {

    public ProductVersionPreference(Context context) {
        super(context);

        setKey("Product_version");
        setTitle("Product version");

        final String formattedProductVersion = getFormattedProductVersion();

        setSummary("Product Version: ".concat(formattedProductVersion));
    }

    public final String getFormattedProductVersion() {
        final byte[] productionVersion = ProductionVersionAPI.getProductionVersion();
        return BaseEncoding.base16().encode(productionVersion);
    }

}
