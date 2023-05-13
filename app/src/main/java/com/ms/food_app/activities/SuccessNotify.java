package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivitySuccessNotifyBinding;
import com.ms.food_app.utils.ToastUtil;

public class SuccessNotify extends AppCompatActivity {
    private ActivitySuccessNotifyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySuccessNotifyBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        ToastUtil.showToast(binding.getRoot(), "Success in ordering this products", true);
        setEvents();
    }
    private void setEvents(){
        binding.done.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, Main.class));
        });
    }
}