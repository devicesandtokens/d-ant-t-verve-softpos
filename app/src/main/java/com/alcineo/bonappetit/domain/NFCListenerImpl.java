package com.alcineo.bonappetit.domain;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.util.Log;

import com.alcineo.bonappetit.domain.utils.ApduUtils;
import com.alcineo.softpos.payment.api.interfaces.NFCListener;
import com.alcineo.softpos.payment.model.CardStatus;
import com.alcineo.utils.common.StringUtils;

import java.io.IOException;

/**
 * Demo Implementation for NFC Read/Write
 */
public class NFCListenerImpl implements NFCListener {

    private static final String TAG = NFCListenerImpl.class.getSimpleName();

    private IsoDep lastTagRead;

    public NFCListenerImpl() {
    }

    @Override
    public void activateNFC() {

    }

    @Override
    public void deactivateNFC() {
        try {
            if (lastTagRead != null) {
                lastTagRead.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "activateNFC: ", e);
        }
    }

    @Override
    public CardStatus getCardStatus() {
        if (lastTagRead != null && lastTagRead.isConnected()) {
            return CardStatus.PRESENT_ON_FIELD;
        }

        return CardStatus.ABSENT_OFF_FIELD;
    }

    @Override
    public byte[] transceiveApdu(byte[] capdu) throws Exception {
        final byte[] rapdu = lastTagRead.transceive(capdu);
        Log.i("C-APDU", StringUtils.convertBytesToHex(capdu));
        Log.i("R-APDU", StringUtils.convertBytesToHex(rapdu));

//        if (ApduUtils.isChangeApplicationPriorityCommand(capdu)) {
//            ApduUtils.changeKernelApplicationPriority(rapdu);
//        }
        return rapdu;
    }

    @Override
    public boolean resetNFCField() {
        deactivateNFC();
        activateNFC();
        return true;
    }

    @Override
    public void setTimeout(int timeout) {
        lastTagRead.setTimeout(timeout);
    }

    @Override
    public void onNfcTagDiscovered(Tag tag) {
        lastTagRead = IsoDep.get(tag);

        try {
            lastTagRead.connect();
        } catch (IOException e) {
            Log.e(TAG, "onNfcTagRead: ", e);
        }
        lastTagRead.setTimeout(3000);
    }

}
