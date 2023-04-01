package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {
    ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setEvents();
    }

    private void setEvents(){
        binding.btnChangePasswordProfile.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ChangePassword.class));
        });
        binding.btnUpdateProfile.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), UpdateProfile.class));
        });
    }
}