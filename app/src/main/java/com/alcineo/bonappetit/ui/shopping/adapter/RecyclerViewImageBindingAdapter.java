package com.alcineo.bonappetit.ui.shopping.adapter;

import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

public class RecyclerViewImageBindingAdapter {

    //code for loading image
    @BindingAdapter("android:imageUrl")
    public static void loadImage(View view, int imageId) {
        AppCompatImageView imageView = (AppCompatImageView) view;
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), imageId));
    }

}
