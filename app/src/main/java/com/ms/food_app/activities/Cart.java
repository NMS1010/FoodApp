package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityCartBinding;

public class Cart extends AppCompatActivity {
    private ActivityCartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}