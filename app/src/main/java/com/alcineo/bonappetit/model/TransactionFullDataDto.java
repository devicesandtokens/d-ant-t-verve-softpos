package com.alcineo.bonappetit.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.alcineo.softpos.payment.model.transaction.TransactionParameters;
import com.alcineo.softpos.payment.model.transaction.TransactionResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * This object contains all the data after transaction ending
 */
@Data
@RequiredArgsConstructor
public class TransactionFullDataDto implements Parcelable {

    private final TransactionParameters transaction;
    private final TransactionResult     transactionResult;


    protected TransactionFullDataDto(Parcel in) {
        transaction = (TransactionParameters) in.readValue(TransactionParameters.class.getClassLoader());
        transactionResult = (TransactionResult) in.readValue(TransactionResult.class.getClassLoader());
    }

    // ==== Parcelable ====
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(transaction);
        dest.writeValue(transactionResult);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TransactionFullDataDto> CREATOR = new Parcelable.Creator<TransactionFullDataDto>() {
        @Override
        public TransactionFullDataDto createFromParcel(Parcel in) {
            return new TransactionFullDataDto(in);
        }

        @Override
        public TransactionFullDataDto[] newArray(int size) {
            return new TransactionFullDataDto[size];
        }
    };
}
