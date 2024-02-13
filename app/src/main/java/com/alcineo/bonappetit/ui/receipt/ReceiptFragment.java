package com.alcineo.bonappetit.ui.receipt;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.alcineo.bonappetit.R;
import com.alcineo.bonappetit.databinding.FragmentReceiptBinding;
import com.alcineo.bonappetit.model.TransactionFullDataDto;
import com.alcineo.softpos.payment.model.transaction.TransactionEndStatus;
import com.alcineo.softpos.payment.model.transaction.TransactionResult;

public class ReceiptFragment extends Fragment {

    private ReceiptViewModel       mReceiptViewModel;
    private FragmentReceiptBinding mFragmentReceiptBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        mFragmentReceiptBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_receipt, container, false);

        final TransactionFullDataDto transactionFullDataDto = ReceiptFragmentArgs.fromBundle(requireArguments())
                .getTransactionFullDataDto();

        mReceiptViewModel =
                new ViewModelProvider(this,
                                      new ReceiptViewModelFactory(transactionFullDataDto))
                        .get(ReceiptViewModel.class);

        mFragmentReceiptBinding.setReceipt(mReceiptViewModel);
        mFragmentReceiptBinding.setLifecycleOwner(getViewLifecycleOwner());

        setupUI(transactionFullDataDto);

        setupObserver();

        return mFragmentReceiptBinding.getRoot();
    }

    private void setupObserver() {

        mFragmentReceiptBinding.receiptLeaveButton.setOnClickListener(v -> navigateToShoppingFragment());

    }

    private void navigateToShoppingFragment() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    private void setupUI(TransactionFullDataDto transactionFullDataDto) {
        int vectorDrawable;

        TransactionResult transactionResult = transactionFullDataDto.getTransactionResult();
        TransactionEndStatus transactionEndStatus = transactionResult.getTransactionEndStatus();

        if (transactionEndStatus == TransactionEndStatus.APPROVED) {
            vectorDrawable = R.drawable.vector_done;
        } else if (transactionEndStatus == TransactionEndStatus.ONLINE) {
            vectorDrawable = 0;
        } else {
            vectorDrawable = R.drawable.vector_error;
        }

        AppCompatImageView receiptAnimatedVector = mFragmentReceiptBinding.receiptAnimatedVector;
        receiptAnimatedVector
                .setImageDrawable(ResourcesCompat.getDrawable(getResources(), vectorDrawable, getContext().getTheme()));
        AnimatedVectorDrawable receiptAnimatedVectorDrawable = (AnimatedVectorDrawable) receiptAnimatedVector
                .getDrawable();
        receiptAnimatedVectorDrawable.start();
    }

}
