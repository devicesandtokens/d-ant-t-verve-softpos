package com.alcineo.bonappetit.ui.transaction;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

public class TransactionViewModelTest {

    @Rule
    public final TestRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TransactionViewModel transactionViewModel;

    @Before
    public void setUp() throws Exception {
        transactionViewModel = new TransactionViewModel(null, null);
    }

    @Test
    public void testShouldDisplayEmptyTextForCLEARSCREENReceived() {
        transactionViewModel.onDisplayText("** CLEAR SCREEN **");
        assertEquals("", transactionViewModel.transactionMessage.getValue());
    }

    @Test
    public void testShouldDisplayTextReceived() {
        transactionViewModel.onDisplayText("Test message to display");
        assertEquals("Test message to display", transactionViewModel.transactionMessage.getValue());
    }


}
