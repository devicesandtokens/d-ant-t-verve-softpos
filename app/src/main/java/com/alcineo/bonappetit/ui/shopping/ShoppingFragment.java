package com.alcineo.bonappetit.ui.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.alcineo.bonappetit.R;
import com.alcineo.bonappetit.databinding.FragmentShoppingBinding;
import com.alcineo.bonappetit.domain.SoftposInitialization;
import com.alcineo.bonappetit.ui.MainActivity;
import com.alcineo.bonappetit.ui.settings.SettingsFragment;
import com.alcineo.bonappetit.ui.shopping.ShoppingFragmentDirections.ActionShoppingToTransaction;
import com.alcineo.bonappetit.ui.shopping.adapter.FoodItemAdapter;
import com.alcineo.bonappetit.domain.utils.SharedPreferencesHelper;

import java.math.BigDecimal;
import java.util.Observable;

public class ShoppingFragment extends Fragment {

    private static final String TAG = ShoppingFragment.class.getSimpleName();

    private ShoppingViewModel       mShoppingViewModel;
    private FoodItemAdapter         foodItemAdapter;
    private FragmentShoppingBinding mShoppingFragmentBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mShoppingViewModel = new ViewModelProvider(this, new ShoppingViewModelFactory()).get(ShoppingViewModel.class);
        mShoppingViewModel.transactionAmount.setValue(new BigDecimal(0));

        mShoppingFragmentBinding = FragmentShoppingBinding.inflate(inflater, container, false);

        mShoppingFragmentBinding.setShopping(mShoppingViewModel);
        mShoppingFragmentBinding.setLifecycleOwner(getViewLifecycleOwner());

        RecyclerView recyclerView = mShoppingFragmentBinding.shoppingItemsRecyclerview;
        foodItemAdapter = new FoodItemAdapter(mShoppingViewModel.transactionAmount, SharedPreferencesHelper.getCurrencyCodeFromPrefs(requireContext()));
        recyclerView.setAdapter(foodItemAdapter);

        mShoppingFragmentBinding.shoppingChargeButton.setOnClickListener(v -> {
            final ActionShoppingToTransaction actionShoppingToTransactionFragment = ShoppingFragmentDirections.actionShoppingToTransaction(mShoppingViewModel.transactionAmount.getValue(),
                    mShoppingViewModel.transactionCurrencyCode.getValue());
            Navigation.findNavController(requireView()).navigate(actionShoppingToTransactionFragment);
        });

        if (((SoftposInitialization) ((MainActivity) requireActivity()).softposInitializationObservable).isDeviceReady()) {
            mShoppingFragmentBinding.shoppingChargeButton.setEnabled(true);
            mShoppingFragmentBinding.shoppingChargeButtonIndicator.setVisibility(View.INVISIBLE);
        } else {
            ((MainActivity) requireActivity()).softposInitializationObservable.addObserver((Observable o, Object arg) -> {
                if (o instanceof SoftposInitialization) {
                    requireActivity().runOnUiThread(() -> {
                        mShoppingFragmentBinding.shoppingChargeButton.setEnabled((Boolean) arg);
                        mShoppingFragmentBinding.shoppingChargeButtonIndicator.setVisibility(View.INVISIBLE);
                    });
                }
            });
        }

        SharedPreferencesHelper.getCurrencyCodeFromPrefs(requireContext(), currencyCode -> {
            mShoppingViewModel.updateCurrency(currencyCode);
            foodItemAdapter.transactionCurrency = currencyCode;
        });

        return mShoppingFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = requireView().findViewById(R.id.shopping_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_nav_host_fragment, new SettingsFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack("settings_fragment")
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

}
