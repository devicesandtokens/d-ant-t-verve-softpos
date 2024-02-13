package com.alcineo.bonappetit.ui.shopping;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.neovisionaries.i18n.CurrencyCode;

import java.math.BigDecimal;

/**
 * Used for transaction action handle data from softpos and set value in MutableLiveData who will be queried by xml
 * view
 */
public class ShoppingViewModel extends ViewModel {

    public MutableLiveData<BigDecimal>   transactionAmount;
    public MutableLiveData<CurrencyCode> transactionCurrencyCode;

    public ShoppingViewModel() {
        this.transactionAmount = new MutableLiveData<>(new BigDecimal(0));
        this.transactionCurrencyCode = new MutableLiveData<>();
    }

    public void updateCurrency(CurrencyCode currencyCode) {
        this.transactionCurrencyCode.postValue(currencyCode);
    }

}
