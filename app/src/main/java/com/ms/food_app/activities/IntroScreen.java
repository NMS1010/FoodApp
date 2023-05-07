package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.R;
import com.ms.food_app.activities.admin.AdminMain;
import com.ms.food_app.databinding.ActivityIntroScreenBinding;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.SharedPrefManager;

public class IntroScreen extends AppCompatActivity {
    private ActivityIntroScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroScreenBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        setEvents();
        ContextUtil.context = getApplicationContext();
        if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
            finish();
            Intent intent = new Intent(this, Main.class);
            if(SharedPrefManager.getInstance(this).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN")))
                intent = new Intent(this, AdminMain.class);
            startActivity(intent);
        }
    }
    private void setEvents(){
        binding.SignIn.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, Signin.class));
        });
        binding.SignUp.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, Signup.class));
        });
    }
}