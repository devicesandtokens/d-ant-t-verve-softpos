package com.alcineo.bonappetit.ui.settings;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.alcineo.bonappetit.ui.settings.preference.CurrencyPreference;
import com.neovisionaries.i18n.CurrencyCode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CurrencyPreferenceTest {

    @Mock
    CurrencyPreference currencyPreference;

    @Before
    public void setUp() {
        when(currencyPreference.getCurrencyCodeToDisplay(any())).thenCallRealMethod();
    }

    @Test
    public void testShouldReturnEmpty() {
        List<CurrencyCode> currencyCodes = new ArrayList<>();
        final String[] currencyCodeToDisplay = currencyPreference.getCurrencyCodeToDisplay(currencyCodes);
        assertEquals(0, currencyCodeToDisplay.length);
    }

    @Test
    public void testShouldReturnOneItem() {
        List<CurrencyCode> currencyCodes = Collections.singletonList(CurrencyCode.EUR);
        final String[] currencyCodeToDisplay = currencyPreference.getCurrencyCodeToDisplay(currencyCodes);
        assertEquals(1, currencyCodeToDisplay.length);
    }

    @Test
    public void testShouldReturnCurrencyCodeNameInCodeFormat() {
        List<CurrencyCode> currencyCodes = Collections.singletonList(CurrencyCode.EUR);
        final String[] currencyCodeToDisplay = currencyPreference.getCurrencyCodeToDisplay(currencyCodes);
        assertEquals("EUR", currencyCodeToDisplay[0]);
    }

}
