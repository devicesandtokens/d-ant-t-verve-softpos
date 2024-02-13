package com.alcineo.bonappetit.ui;

import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.alcineo.administrative.Kernel;
import com.alcineo.bonappetit.R;
import com.alcineo.bonappetit.android.LocationHelper;
import com.alcineo.bonappetit.domain.SoftposInitialization;
import com.alcineo.bonappetit.domain.NFCListenerImpl;
import com.alcineo.softpos.payment.api.KernelsAdministrationAPI;
import com.alcineo.softpos.payment.api.interfaces.NFCListener;
import com.alcineo.softpos.payment.error.ExecutorException;

import java.util.Observable;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter      nfcAdapter;
    public  NFCListener     nfcListener;
    public  Observable      softposInitializationObservable;
    public  LocationHelper  locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupNfc();
        setupLocation();



        softposInitializationObservable = new SoftposInitialization(getApplicationContext());


    }

    private void setupNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcListener = new NFCListenerImpl();
    }

    private void setupLocation() {
        locationHelper = new LocationHelper(this);
        locationHelper.startListeningUserLocation();
    }


    @Override
    protected void onResume() {
        super.onResume();
        final int NFC_FLAGS = NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS;
        nfcAdapter.enableReaderMode(this, nfcListener::onNfcTagDiscovered, NFC_FLAGS, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableReaderMode(this);
    }

}
