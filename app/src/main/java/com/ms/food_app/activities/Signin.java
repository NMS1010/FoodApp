package com.ms.food_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.databinding.ActivitySigninBinding;
import com.ms.food_app.models.User;
import com.ms.food_app.models.requests.LoginRequest;
import com.ms.food_app.models.response.AuthResponse;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IAuthService;
import com.ms.food_app.services.IUserService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signin extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress = LoadingUtil.setLoading(this);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, Main.class));
        }
        setEvents();
    }
    private Boolean isValidated() {
        if (binding.emailEtLogin.getText().toString().trim().isEmpty()) {
            binding.emailEtLogin.setError("Enter your username");
            return false;
        } else if (binding.passwordEtLogin.getText().toString().trim().isEmpty()) {
            binding.passwordEtLogin.setError("Enter your password");
            return false;
        }else {
            return true;
        }
    }
    private void setEvents(){
        binding.signinBtnLogin.setOnClickListener(view -> {
            if(!isValidated())
                return;
            login();
        });
        binding.signupBtnLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, Signup.class));
        });
    }
    private void login() {
        String email = binding.emailEtLogin.getText().toString();
        String password = binding.passwordEtLogin.getText().toString();
        LoginRequest req = new LoginRequest(email, password);
        String request = new Gson().toJson(req);


        JsonParser parser = new JsonParser();
        progress.show();
        BaseAPIService.createService(IAuthService.class).login((JsonObject) parser.parse(request)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {

                if(response.body() == null){
                    ToastUtil.showToast(getApplicationContext(),"Email or password is incorrect");
                    progress.dismiss();
                    return;
                }
                if (!response.body().getAccessToken().equals("")) {
                    AuthResponse authResponse = response.body();

                    SharedPrefManager.getInstance(getApplicationContext()).saveAuthToken(authResponse);
                    saveCurrentUser(authResponse.getUserId());

                }else{
                    ToastUtil.showToast(getApplicationContext(),"Email or password is incorrect");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Log.d("error", t.getMessage());
                progress.dismiss();
            }
        });
    }
    private void saveCurrentUser(long userId){
        BaseAPIService.createService(IUserService.class).getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if(response.body() == null){
                    ToastUtil.showToast(getApplicationContext(),"Cannot login");
                    return;
                }
                User currentUser = response.body();
                SharedPrefManager.getInstance(getApplicationContext()).saveUser(currentUser);
                if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
                    ToastUtil.showToast(getApplicationContext(),"Logging successfully");
                    finish();
                    Intent intent = new Intent(Signin.this, Main.class);
                    startActivity(intent);
                    progress.dismiss();
                }else{
                    ToastUtil.showToast(getApplicationContext(),"Cannot login");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Log.d("error", t.getMessage());
                progress.dismiss();
            }
        });
    }
}