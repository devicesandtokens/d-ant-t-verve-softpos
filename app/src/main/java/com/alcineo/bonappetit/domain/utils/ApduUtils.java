package com.alcineo.bonappetit.domain.utils;

import android.util.Log;

import com.alcineo.bonappetit.model.TlvRegistry;
import com.alcineo.utils.tlv.TlvException;
import com.alcineo.utils.tlv.TlvItem;
import com.alcineo.utils.tlv.TlvParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApduUtils {

    private static final String TAG = ApduUtils.class.getSimpleName();

    public static List<byte[]> transceiveApduParseApplicationsGetTLVTagList(byte[] data, TlvRegistry tag, boolean tagRequired) throws TlvException {
        List<TlvItem> parsed;
        List<byte[]> retValues;

        retValues = new ArrayList<byte[]>();
        parsed = TlvParser.decode(data, 0);
        for (int i = 0; i < parsed.size(); i++) {
            if (Arrays.equals(parsed.get(i).getTag().getBytes(), tag.getValue())) {
                retValues.add(parsed.get(i).getValue());
            }
        }
        if (retValues.size() == 0) {
            if (tagRequired) {
                Log.d(TAG, "transceiveApduParseApplicationsGetTLVTagList: Tag " + ByteArraysUtils.byteArrayToHexString(tag.getValue()) + " not found in data " + ByteArraysUtils.byteArrayToHexString(data));
                throw new TlvException("Tag " + Arrays.toString(tag.getValue()) + " not found.");
            }
            Log.d(TAG, "transceiveApduParseApplicationsGetTLVTagList: tag " + ByteArraysUtils.byteArrayToHexString(tag.getValue()) + " not found but not required then null returned.");
            return null;
        }
        return retValues;
    }

    public static byte[] transceiveApduParseApplicationsGetTLVTag(byte[] data, TlvRegistry tag, boolean tagRequired) throws TlvException {
        List<TlvItem> parsed;

        parsed = TlvParser.decode(data, 0);
        for (int i = 0; i < parsed.size(); i++) {
            if (Arrays.equals(parsed.get(i).getTag().getBytes(), tag.getValue())) {
                Log.d(TAG, "transceiveApduParseApplicationsGetTLVTag: tag found, returned");
                return parsed.get(i).getValue();
            }
        }
        if (tagRequired) {
            Log.d(TAG, "transceiveApduParseApplicationsGetTLVTag: Tag " + ByteArraysUtils.byteArrayToHexString(tag.getValue()) + " not found in data " + ByteArraysUtils.byteArrayToHexString(data));
            throw new TlvException("Tag " + Arrays.toString(tag.getValue()) + " not found.");
        }
        Log.d(TAG, "transceiveApduParseApplicationsGetTLVTag: tag " + ByteArraysUtils.byteArrayToHexString(tag.getValue()) + " not found but not required then null returned.");
        return null;
    }

    public static byte[] getTlvFromByte(TlvRegistry tag, byte[] value) {
        if (value == null) {
            return new byte[]{};
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(tag.getValue());
            outputStream.write(value.length);
            outputStream.write(value);
        } catch (IOException e) {
            Log.e(TAG, "getTlvFromByte: ", e);
        }
        return outputStream.toByteArray();
    }

    public static void setApplicationPriorityEditApp(byte[] app, int priority) throws TlvException {
        byte[] parsed;
        byte[] newPriority;

        parsed = transceiveApduParseApplicationsGetTLVTag(app, TlvRegistry.TAG_APPLICATION_PRIORITY, true);
        parsed = getTlvFromByte(TlvRegistry.TAG_APPLICATION_PRIORITY, parsed);
        newPriority = getTlvFromByte(TlvRegistry.TAG_APPLICATION_PRIORITY, new byte[]{(byte) priority});
        ByteArraysUtils.byteArrayFindAndReplace(app, parsed, newPriority);
    }

    public static void setApplicationPriorityRebuild(byte[] data, byte[] parsed, List<byte[]> appsBytes) {
        byte[] allApps;

        allApps = null;
        for (int i = 0; i < appsBytes.size(); i++) {
            allApps = ByteArraysUtils.concatenateByteArray(allApps, getTlvFromByte(TlvRegistry.TAG_APPLICATION_TEMPLATE, appsBytes.get(i)));
        }
        ByteArraysUtils.byteArrayFindAndReplace(data, parsed, allApps);
    }

    public static void setApplicationPriority(byte[] data, byte[] aid, int priority) throws TlvException {
        byte[] parsed;
        List<byte[]> appsBytes;

        parsed = transceiveApduParseApplicationsGetTLVTag(data, TlvRegistry.TAG_FILE_CONTROL_INFORMATION_6F, true);
        parsed = transceiveApduParseApplicationsGetTLVTag(parsed, TlvRegistry.TAG_FILE_CONTROL_INFORMATION_A5, true);
        parsed = transceiveApduParseApplicationsGetTLVTag(parsed, TlvRegistry.TAG_FILE_CONTROL_INFORMATION_BF0C, true);
        appsBytes = transceiveApduParseApplicationsGetTLVTagList(parsed, TlvRegistry.TAG_APPLICATION_TEMPLATE, true);
        for (int i = 0; i < appsBytes.size(); i++) {
            byte[] tmpApp;

            tmpApp = transceiveApduParseApplicationsGetTLVTag(appsBytes.get(i), TlvRegistry.TAG_APPLICATION_ID, true);
            if (Arrays.equals(tmpApp, aid)) {
                tmpApp = appsBytes.get(i);
                setApplicationPriorityEditApp(tmpApp, priority);
                appsBytes.set(i, tmpApp);
            }
        }
        setApplicationPriorityRebuild(data, parsed, appsBytes);
    }

    public static boolean isChangeApplicationPriorityCommand(byte[] capdu) {
        final byte[] SELECT_PPSE = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00};
        return Arrays.equals(SELECT_PPSE, capdu);
    }

    public static void changeKernelApplicationPriority(byte[] rapdu) throws TlvException {
        Log.d(TAG, "transceiveApdu: guenuine RAPDU: " + ByteArraysUtils.byteArrayToHexString(rapdu));
        final byte[] applicationTagValue = {(byte) 0xA0, 0x00, 0x00, 0x00, 0x04, 0x10, 0x10};
        final int newPriorityValue = 0x02;
        setApplicationPriority(rapdu, applicationTagValue, newPriorityValue);
        Log.d(TAG, "transceiveApdu: modified RAPDU: " + ByteArraysUtils.byteArrayToHexString(rapdu));
    }
}
