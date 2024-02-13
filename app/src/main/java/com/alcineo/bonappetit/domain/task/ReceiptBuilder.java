package com.alcineo.bonappetit.domain.task;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.alcineo.softpos.payment.model.transaction.TransactionEndStatus;
import com.alcineo.softpos.payment.model.transaction.TransactionMode;
import com.alcineo.utils.common.StringUtils;
import com.alcineo.utils.tlv.TlvException;
import com.alcineo.utils.tlv.TlvItem;
import com.alcineo.utils.tlv.TlvTag;
import com.neovisionaries.i18n.CurrencyCode;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.NotNull;

public class ReceiptBuilder implements Callable<LiveData<String>> {

    private static final String TAG = ReceiptBuilder.class.getSimpleName();

    private static final int MERCHANT_NAME_AND_LOCATION_EMV_TAG    = 0x9F4E;
    private static final int TRANSACTION_DATE_EMV_TAG              = 0x9A;
    private static final int TRANSACTION_TIME_EMV_TAG              = 0x9F21;
    private static final int APPLICATION_IDENTIFIER_EMV_TAG        = 0x84;
    private static final int APPLICATION_LABEL_EMV_TAG             = 0x50;
    private static final int TRANSACTION_CURRENCY_EXPONENT_EMV_TAG = 0x5F36;
    private static final int AMOUNT_AUTHORISED_EMV_TAG             = 0x9F02;
    private static final int TRANSACTION_CURRENCY_CODE_EMV_TAG     = 0x5F2A;

    private final TlvItem dataRecord;
    private final TransactionEndStatus endStatus;

    public ReceiptBuilder(TlvItem dataRecord, TransactionEndStatus transactionEndStatus) {
        this.dataRecord = dataRecord;
        this.endStatus = transactionEndStatus;
    }

    @Override
    public LiveData<String> call() throws Exception {
        final StringBuilder receipt = new StringBuilder();
        final String posEntryMode = extractPosEntryMode();
        final String merchantDataHexValue = searchValueByEmvTag(MERCHANT_NAME_AND_LOCATION_EMV_TAG, "4d45524348414e545f44415441");
        final String merchantData = new String(StringUtils.convertHexToBytes(merchantDataHexValue));
        final String dateString = extractTransactionDateTime();
        final String aid = searchValueByEmvTag(APPLICATION_IDENTIFIER_EMV_TAG, "00000");
        final String applicationLabelHexValue = searchValueByEmvTag(APPLICATION_LABEL_EMV_TAG, "554E4E414D4544");
        final String applicationLabel = new String(StringUtils.convertHexToBytes(applicationLabelHexValue));
        final String currencyExponent = searchValueByEmvTag(TRANSACTION_CURRENCY_EXPONENT_EMV_TAG, "0");
        final String rawAmount = searchValueByEmvTag(AMOUNT_AUTHORISED_EMV_TAG, "0");
        final BigDecimal amountAuthorised = new BigDecimal(rawAmount).movePointLeft(Integer.parseInt(currencyExponent));
        final String currency = searchValueByEmvTag(TRANSACTION_CURRENCY_CODE_EMV_TAG, "555344");
        final CurrencyCode currencyCode = CurrencyCode.getByCode((Integer.parseInt(currency)));

        receipt.append(endStatus.toString()).append("\n");
        receipt.append(posEntryMode);
        receipt.append(System.lineSeparator());
        receipt.append(merchantData);
        receipt.append(System.lineSeparator());
        receipt.append("Date: ").append(dateString);
        receipt.append(System.lineSeparator());
        receipt.append("AID: ").append(aid);
        receipt.append(System.lineSeparator());
        receipt.append("Application label: ").append(applicationLabel);
        receipt.append(System.lineSeparator());
        receipt.append("Amount authorised: ").append(amountAuthorised).append(" ").append(currencyCode);
        receipt.append(System.lineSeparator());

        return new MutableLiveData<>(receipt.toString());
    }

    private String searchValueByEmvTag(int emvTag, String defaultValue) {
        byte[] emvTagHexFormat = StringUtils.convertHexToBytes(Integer.toHexString(emvTag));

        for (TlvItem tlvItem : dataRecord.getChildren()) {
            try {
                if (tlvItem.getTag().equals(TlvTag.fromBytes(emvTagHexFormat, 0))) {
                    return StringUtils.convertBytesToHex(tlvItem.getValue());
                }
            } catch (TlvException tlvException) {
                Log.e(TAG, "getValueFromTag: ", tlvException);
            }
        }

        return defaultValue;
    }

    private String extractPosEntryMode() {
        final byte[] tagPosEntryMode = new byte[]{(byte) 0x9F, (byte) 0x36};
        byte[] posEntryModeValue = new byte[0];

        for (TlvItem tag : dataRecord.getChildren()) {
            try {
                if (tag.getTag().equals(TlvTag.fromBytes(tagPosEntryMode, 0))) {
                    posEntryModeValue = tag.getValue();
                }
            } catch (TlvException tlvException) {
                return TransactionMode.MODE_CONTACTLESS.getMode();
            }
        }

        if (posEntryModeValue != null && posEntryModeValue.length != 1) {
            return TransactionMode.MODE_CONTACTLESS.getMode();
        }

        if (posEntryModeValue != null) {
            switch (posEntryModeValue[0]) {
                case (byte) 0x05:
                    // Chip transaction
                    return TransactionMode.MODE_EMV_CHIP.getMode();
                case (byte) 0x42:
                    return TransactionMode.MODE_MAGNETIC_STRIPE.getMode();
                case (byte) 0x91:
                    return TransactionMode.MODE_MSD.getMode();
                // Contactless transaction
                case (byte) 0x07:
                default:
                    return TransactionMode.MODE_CONTACTLESS.getMode();
            }
        }

        return TransactionMode.MODE_CONTACTLESS.getMode();
    }

    @NotNull
    private String extractTransactionDateTime() {

        try {
            Date date;
            String dateFromTerminal = searchValueByEmvTag( TRANSACTION_DATE_EMV_TAG, "000000000000");
            String timestampFromTerminal = searchValueByEmvTag( TRANSACTION_TIME_EMV_TAG, "000000");

            if (!dateFromTerminal.isEmpty() && !timestampFromTerminal.isEmpty()) {
                date = new SimpleDateFormat("yyMMddhhmmss", Locale.getDefault()).parse(dateFromTerminal + timestampFromTerminal);
            } else if (!dateFromTerminal.isEmpty()) {
                date = new SimpleDateFormat("yyMMdd", Locale.getDefault()).parse(dateFromTerminal);
            } else if (!timestampFromTerminal.isEmpty()) {
                date = new SimpleDateFormat("hhmmss", Locale.getDefault()).parse(timestampFromTerminal);
            } else {
                throw new ParseException(dateFromTerminal, 0);
            }
            SimpleDateFormat sdf= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");;
            return sdf.format(date);

        } catch (ParseException e) {
            Log.e(TAG, "getReceipt: ", e);
        }
        return "";
    }
}
