package com.alcineo.bonappetit.ui.transaction;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;

import com.alcineo.softpos.payment.api.interfaces.NFCListener;
import com.alcineo.softpos.payment.model.transaction.TransactionParameters;
import org.jetbrains.annotations.NotNull;

public class TransactionViewModelFactory implements Factory {

    private final NFCListener nfcListener;
    private final Context context;
    private final TransactionParameters transactionParameters;

    public TransactionViewModelFactory(Context context, NFCListener nfcListener, TransactionParameters transactionParameters) {
        this.context = context;
        this.nfcListener = nfcListener;
        this.transactionParameters = transactionParameters;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new TransactionViewModel(context, nfcListener, transactionParameters);
    }
}
