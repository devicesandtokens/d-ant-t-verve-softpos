package com.alcineo.bonappetit.ui.settings.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.preference.Preference;

import com.google.common.io.BaseEncoding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.content.pm.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SignaturePreference extends Preference {

    public SignaturePreference(Context context) {
        super(context);

        setKey("Signature");
        setTitle("Apk Signature");

        final List<String> Signature = getSignatures(context.getPackageManager(), context.getPackageName());

        setSummary(Signature.get(0).toUpperCase(Locale.ROOT));
    }
    private static List<String> getSignatures(@NonNull PackageManager pm, @NonNull String packageName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                if (packageInfo == null
                        || packageInfo.signingInfo == null) {
                    return null;
                }
                if(packageInfo.signingInfo.hasMultipleSigners()){
                    return signatureDigest(packageInfo.signingInfo.getApkContentsSigners());
                }
                else{
                    return signatureDigest(packageInfo.signingInfo.getSigningCertificateHistory());
                }
            }
            else {
                @SuppressLint("PackageManagerGetSignatures")
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                if (packageInfo == null
                        || packageInfo.signatures == null
                        || packageInfo.signatures.length == 0
                        || packageInfo.signatures[0] == null) {
                    return null;
                }
                return signatureDigest(packageInfo.signatures);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    private static String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    private static List<String> signatureDigest(Signature[] sigList) {
        List<String> signaturesList= new ArrayList<>();
        for (Signature signature: sigList) {
            if(signature!=null) {
                signaturesList.add(signatureDigest(signature));
            }
        }
        return signaturesList;
    }

}

