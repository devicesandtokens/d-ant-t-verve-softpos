package com.alcineo.bonappetit.domain.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.neovisionaries.i18n.CurrencyCode;

import java.lang.ref.WeakReference;

/**
 * Class used to fetch/save data to SharedPreferences
 */
public class SharedPreferencesHelper {

    public static void getCurrencyCodeFromPrefs(Context context, OnResultListener<CurrencyCode> onResultListener) {
        new Thread(() -> {
            String currencyString = (String) getFromPrefs(context, "transactionCurrency", "NGN");
            final CurrencyCode currencyCode = CurrencyCode.valueOf(currencyString);
            onResultListener.onResult(currencyCode);
        }).start();
    }

    public static CurrencyCode getCurrencyCodeFromPrefs(Context context) {
            String currencyString = (String) getFromPrefs(context, "transactionCurrency", "NGN");
            return CurrencyCode.valueOf(currencyString);
    }

    private static Object getFromPrefs(Context context, String key, Object defaultValue) {
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        if (contextWeakReference.get() != null) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get());
            try {
                if (defaultValue instanceof String) {
                    return sharedPrefs.getString(key, defaultValue.toString());
                } else if (defaultValue instanceof Integer) {
                    return sharedPrefs.getInt(key, (Integer) defaultValue);
                } else if (defaultValue instanceof Boolean) {
                    return sharedPrefs.getBoolean(key, (Boolean) defaultValue);
                } else if (defaultValue instanceof Long) {
                    return sharedPrefs.getLong(key, (Long) defaultValue);
                } else if (defaultValue instanceof Float) {
                    return sharedPrefs.getFloat(key, (Float) defaultValue);
                } else if (defaultValue instanceof Double) {
                    return Double.longBitsToDouble(sharedPrefs.getLong(key, Double.doubleToLongBits((double) defaultValue)));
                }
            } catch (Exception e) {
                Log.e("Execption", e.getMessage());
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public interface  OnResultListener<T> {
        void onResult(T t);
    }

}
