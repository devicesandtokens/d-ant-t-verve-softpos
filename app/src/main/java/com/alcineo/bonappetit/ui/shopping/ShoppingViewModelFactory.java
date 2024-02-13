package com.alcineo.bonappetit.ui.shopping;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;
import org.jetbrains.annotations.NotNull;

public class ShoppingViewModelFactory implements Factory {

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        return (T) new ShoppingViewModel();
    }
}
