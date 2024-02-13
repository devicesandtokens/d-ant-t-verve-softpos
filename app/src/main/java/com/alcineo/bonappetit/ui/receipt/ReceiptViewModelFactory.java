package com.alcineo.bonappetit.ui.receipt;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;
import com.alcineo.bonappetit.model.TransactionFullDataDto;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class for ReceiptViewModel
 * <p>
 * Required class as we need a transactionFullDataDto object filled with data
 */
public class ReceiptViewModelFactory implements Factory {

    private final TransactionFullDataDto TransactionFullDataDto;

    public ReceiptViewModelFactory(@NonNull TransactionFullDataDto transactionFullDataDto) {
        this.TransactionFullDataDto = transactionFullDataDto;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new ReceiptViewModel(TransactionFullDataDto);
    }
}
