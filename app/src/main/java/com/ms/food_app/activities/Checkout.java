package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityCheckoutBinding;

public class Checkout extends AppCompatActivity {
    private ActivityCheckoutBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        setEvents();
    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, Cart.class));
        });
        binding.BtnOrder.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, SuccessNotify.class));
        });
    }
}