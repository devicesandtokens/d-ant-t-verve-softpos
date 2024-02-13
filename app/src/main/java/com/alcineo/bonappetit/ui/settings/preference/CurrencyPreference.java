package com.alcineo.bonappetit.ui.settings.preference;

import android.content.Context;

import androidx.preference.ListPreference;

import com.neovisionaries.i18n.CurrencyCode;

import java.util.Arrays;
import java.util.List;

public class CurrencyPreference extends ListPreference {

    public CurrencyPreference(Context context) {
        super(context);

        setKey("transactionCurrency");
        setTitle("Choose transactionCurrency");

        final List<CurrencyCode> CurrencyEntries = Arrays.asList(CurrencyCode.USD, CurrencyCode.EUR, CurrencyCode.CAD, CurrencyCode.CHF, CurrencyCode.RON, CurrencyCode.BGN, CurrencyCode.JPY, CurrencyCode.TND, CurrencyCode.NGN);
        final String[] currencyCode = getCurrencyCodeToDisplay(CurrencyEntries);

        setEntries(currencyCode);
        setEntryValues(currencyCode);
        setSummaryProvider(SimpleSummaryProvider.getInstance());
    }

    public String[] getCurrencyCodeToDisplay(List<CurrencyCode> currencyCodes) {
        return currencyCodes.stream().map(currencyCode -> currencyCode.getCurrency().getCurrencyCode()).toArray(String[]::new);
    }


}
