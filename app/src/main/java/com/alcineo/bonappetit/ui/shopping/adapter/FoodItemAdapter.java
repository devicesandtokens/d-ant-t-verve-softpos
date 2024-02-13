package com.alcineo.bonappetit.ui.shopping.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.alcineo.bonappetit.R;
import com.alcineo.bonappetit.databinding.AdapterFooditemBinding;
import com.alcineo.bonappetit.model.FoodItem;
import com.alcineo.bonappetit.ui.shopping.adapter.FoodItemAdapter.FoodItemViewHolder;
import com.neovisionaries.i18n.CurrencyCode;

import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemViewHolder> {

    public MutableLiveData<BigDecimal> transactionAmount;
    public CurrencyCode transactionCurrency;

    private List<FoodItem> foodItems = new ArrayList<>();

    public FoodItemAdapter(MutableLiveData<BigDecimal> transactionAmount, CurrencyCode transactionCurrency ) {
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;

        this.foodItems = Arrays.asList(
                new FoodItem(R.drawable.item_salad,
                        0,
                        BigDecimal.valueOf(3).setScale(transactionCurrency.getMinorUnit(), RoundingMode.FLOOR),
                        "Salad"),
                new FoodItem(R.drawable.item_juice,
                        0,
                        BigDecimal.valueOf(1.52).setScale(transactionCurrency.getMinorUnit(), RoundingMode.FLOOR),
                        "Juice"),
                new FoodItem(R.drawable.item_cupcake,
                        0,
                        BigDecimal.valueOf(2.309).setScale(transactionCurrency.getMinorUnit(), RoundingMode.FLOOR),
                        "Cupcake"),
                new FoodItem(R.drawable.item_fruits,
                        0,
                        BigDecimal.valueOf(2.70).setScale(transactionCurrency.getMinorUnit(), RoundingMode.FLOOR),
                        "Fruits"));
    }

    @NonNull
    @NotNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new FoodItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fooditem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FoodItemAdapter.FoodItemViewHolder holder, int position) {
        holder.getAdapterFooditemBinding().setFoodItem(foodItems.get(position));

        holder.getAdapterFooditemBinding().getRoot().findViewById(R.id.plusButton)
                .setOnClickListener(view -> {
                    updateItemsQuantity(view, position);
                    updateTotalAmount();
                });

        holder.getAdapterFooditemBinding().getRoot().findViewById(R.id.minusButton)
                .setOnClickListener(view -> {
                    updateItemsQuantity(view, position);
                    updateTotalAmount();
                });

        holder.adapterFooditemBinding.plusButton.setOnLongClickListener(view -> {
            updateItemQuantity(position, 100);
            updateTotalAmount();
            return true;
        });

        holder.adapterFooditemBinding.minusButton.setOnLongClickListener(view -> {
            updateItemQuantity(position, 0);
            updateTotalAmount();
            return true;
        });

        holder.getAdapterFooditemBinding().executePendingBindings();
    }

    private void updateItemsQuantity(View view, int position) {
        final int initialQuantity = foodItems.get(position).getQuantity();

        if (view.getId() == R.id.plusButton) {
            updateItemQuantity(position, initialQuantity + 1);
        } else if (view.getId() == R.id.minusButton) {
            if (initialQuantity > 0) {
                updateItemQuantity(position, initialQuantity - 1);
            }
        }
    }

    private void updateItemQuantity(int position, int i) {
        foodItems.get(position).setQuantity(i);
        notifyItemChanged(position);
    }

    private void updateTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(0).setScale(this.transactionCurrency.getMinorUnit());
        for (FoodItem foodItem : foodItems) {
            totalAmount = totalAmount.add(foodItem.getPrice().multiply(BigDecimal.valueOf(foodItem.getQuantity())));
        }
        transactionAmount.postValue(totalAmount);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    class FoodItemViewHolder extends RecyclerView.ViewHolder {

        @Getter
        public final AdapterFooditemBinding adapterFooditemBinding;

        public FoodItemViewHolder(View view) {
            super(view);
            adapterFooditemBinding = AdapterFooditemBinding.bind(view);
            adapterFooditemBinding.setFoodItemAdapter(FoodItemAdapter.this);
        }

    }

}
