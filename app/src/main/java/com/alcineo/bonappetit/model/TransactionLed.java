package com.alcineo.bonappetit.model;

import android.graphics.Color;
import lombok.Data;

/**
 * Object representing a transaction led
 */
public class TransactionLed {

    public static final int[] MODE1_ON = new int[]{Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN};
    public static final int[] MODE2_ON = new int[]{Color.BLUE, Color.YELLOW, Color.GREEN, Color.RED};

    private int backgroundColor;

    public int getBackgroundColor() {

        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
