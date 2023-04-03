package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.databinding.ActivityChangePasswordBinding;
import com.ms.food_app.fragments.Home;
import com.ms.food_app.fragments.Profile;

public class ChangePassword extends AppCompatActivity {
    ActivityChangePasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setEvents();
    }
    private void setEvents(){
        binding.backBtnChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.putExtra("Check", "Profile");
            startActivity(intent);
        });
    }
}