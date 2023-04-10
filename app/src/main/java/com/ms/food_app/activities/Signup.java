package com.ms.food_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivitySignupBinding;
import com.ms.food_app.models.requests.LoginRequest;
import com.ms.food_app.models.requests.RegisterRequest;
import com.ms.food_app.models.response.AuthResponse;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IAuthService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress = LoadingUtil.setLoading(this);
        setEvents();
    }
    private void setEvents(){
        binding.loginBtnSignup.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, Signin.class));
        });
        binding.signUpBtnSignup.setOnClickListener(view -> {
            if(!isValidated())
                return;
            signup();
        });
    }
    private void signup(){
        String email = binding.emailEdSignup.getText().toString();
        String firstName = binding.firstNameEdSignup.getText().toString();
        String lastName = binding.lastNameEdSignup.getText().toString();
        String password = binding.passwordEdSignup.getText().toString();
        RegisterRequest req = new RegisterRequest(firstName,lastName,email, password);
        String request = new Gson().toJson(req);
        JsonParser parser = new JsonParser();
        progress.show();
        BaseAPIService.createService(IAuthService.class).signup((JsonObject)parser.parse(request)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if(response.body() == null){
                    showToast("Failed to register account with this information");
                    return;
                }
                AuthResponse authResponse = response.body();
                if (!authResponse.getAccessToken().equals("")) {
                    showToast("Registering is successfully");
                    finish();
                    Intent intent = new Intent(Signup.this, Signin.class);
                    startActivity(intent);
                }else{
                    showToast("Failed to register account with this information");
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Log.d("error", t.getMessage());
                progress.dismiss();
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private Boolean isValidated() {
        if (binding.emailEdSignup.getText().toString().trim().isEmpty()) {
            binding.emailEdSignup.setError("Enter your email");
            return false;
        }
        if (binding.firstNameEdSignup.getText().toString().trim().isEmpty()) {
            binding.firstNameEdSignup.setError("Enter your first name");
            return false;
        }
        if (binding.lastNameEdSignup.getText().toString().trim().isEmpty()) {
            binding.lastNameEdSignup.setError("Enter your last name");
            return false;
        }
        if (binding.passwordEdSignup.getText().toString().trim().isEmpty()) {
            binding.passwordEdSignup.setError("Enter your password");
            return false;
        }
        if (binding.confirmPasswordEdSignup.getText().toString().trim().isEmpty()) {
            binding.confirmPasswordEdSignup.setError("Enter your confirm password");
            return false;
        }
        if (!binding.passwordEdSignup.getText().toString().trim().equals(binding.confirmPasswordEdSignup.getText().toString().trim())) {
            binding.confirmPasswordEdSignup.setError("Password doesn't match");
            return false;
        }
        return true;
    }
}