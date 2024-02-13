package com.alcineo.bonappetit.model;

import androidx.annotation.IdRes;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoodItem {

    @IdRes
    private int        imgRes;
    private int        quantity;
    private BigDecimal price;
    private String     name;

}
