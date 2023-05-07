package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.activities.admin.AdminMain;
import com.ms.food_app.databinding.ActivityChangePasswordBinding;
import com.ms.food_app.fragments.Home;
import com.ms.food_app.fragments.Profile;
import com.ms.food_app.utils.SharedPrefManager;

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
            if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
                if(SharedPrefManager.getInstance(getApplicationContext()).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN"))){
                    intent = new Intent(getApplicationContext(), AdminMain.class);
                }
            }
            intent.putExtra("Check", "Profile");
            startActivity(intent);
        });
    }
}