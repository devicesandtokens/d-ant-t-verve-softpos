package com.alcineo.bonappetit.domain.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteArraysUtils {
    
    private static final String TAG = ByteArraysUtils.class.getSimpleName();

    public static String byteArrayToHexString(final byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b & 0xff));
        }
        return sb.toString();
    }

    public static boolean byteArrayFindAndReplaceIsFound(byte[] data, int offset, byte[] toFind) {
        int explored;

        explored = 0;
        while (explored < toFind.length) {
            if (data[offset] != toFind[explored]) {
                return false;
            }
            ++offset;
            ++explored;
        }
        return true;
    }

    public static boolean byteArrayFindAndReplaceReplace(byte[] data, int i, byte[] replaceWith) {
        int replaced;

        replaced = 0;
        while (replaced < replaceWith.length) {
            data[i] = replaceWith[replaced];
            ++i;
            ++replaced;
            if (i > data.length) {
                return false;
            }
        }
        return true;
    }

    public static boolean byteArrayFindAndReplace(byte[] data, byte[] toFind, byte[] replaceWith) {
        int i;

        i = 0;
        while (i < data.length) {
            if (byteArrayFindAndReplaceIsFound(data, i, toFind)) {
                return byteArrayFindAndReplaceReplace(data, i, replaceWith);
            }
            ++i;
        }
        return false;
    }

    public static byte[] concatenateByteArray(byte[] array1, byte[] array2) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        if (array1 == null) {
            return array2;
        }
        if (array2 == null) {
            return array1;
        }
        try {
            outputStream.write(array1);
            outputStream.write(array2);
        } catch (IOException e) {
            Log.e(TAG, "concatenateByteArray: ", e);
        }
        return outputStream.toByteArray();
    }

}
