package com.alcineo.bonappetit.ui.receipt;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.alcineo.bonappetit.model.TransactionFullDataDto;
import com.alcineo.bonappetit.domain.task.ReceiptBuilder;
import com.alcineo.softpos.payment.model.transaction.TransactionEndStatus;
import com.alcineo.softpos.payment.model.transaction.TransactionResult;
import com.alcineo.utils.tlv.TlvItem;
import java.util.Arrays;
import java.util.List;

public class ReceiptViewModel extends ViewModel {

    private static final String TAG = ReceiptViewModel.class.getSimpleName();

    private static final byte[] DATA_RECORD_TAG    = new byte[] {(byte)0xFF, (byte)0x81, (byte)0x05};
    private static final byte[] CYPHERED_DATA_TAG  = new byte[] {(byte)0x74};
    private static final byte[] SIGNED_DATA_TAG    = new byte[] {(byte)0x75};
    private static final byte[] ATTTESATION_ID_TAG = new byte[] {(byte)0x76};

    public LiveData<String> receipt;

    public ReceiptViewModel(TransactionFullDataDto transactionFullDataDto) {

        final TransactionResult transactionResult = transactionFullDataDto.getTransactionResult();
        final List<TlvItem> transactionOutcomeTlvItems = transactionResult.getTransactionOutcomeTlvItems();

        if (transactionResult.getTransactionEndStatus() == TransactionEndStatus.APPROVED ||
                transactionResult.getTransactionEndStatus() == TransactionEndStatus.DECLINED) {

            TlvItem dataRecord = null;
            for (TlvItem item : transactionOutcomeTlvItems) {
                Log.i(TAG, "ReceiptViewModel: tlvitem = " + item);
                if (Arrays.equals(item.getTag().getBytes(), DATA_RECORD_TAG)) {
                    dataRecord = item;
                }
            }

            if (dataRecord != null) {
                generateReceipt(dataRecord, transactionResult.getTransactionEndStatus());
            }
            else {
                // No receipt possible because FF8105 not present at end of transaction
                // This case is possible only if all tags are cipher
                receipt = new MutableLiveData<String>(transactionResult.getTransactionEndStatus().name() + "\n Without receipt FF8105 not present \n(maybe all tags are cipher)");
            }

        } else {
            receipt = new MutableLiveData<String>(transactionResult.getTransactionEndStatus().name());
        }
    }

    private void generateReceipt(TlvItem dataRecord, TransactionEndStatus transactionEndStatus) {

        try {

            receipt = new ReceiptBuilder(dataRecord, transactionEndStatus).call();

        } catch (Exception e) {
            Log.e(TAG, "generateReceipt: ", e);
        }

    }

}
