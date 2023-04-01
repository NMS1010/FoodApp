package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityProductDetailBinding;

public class ProductDetail extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}