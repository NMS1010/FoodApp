package com.ms.food_app.activities;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.activities.admin.AdminMain;
import com.ms.food_app.databinding.ActivitySigninBinding;
import com.ms.food_app.models.User;
import com.ms.food_app.models.requests.LoginRequest;
import com.ms.food_app.models.requests.RegisterRequest;
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
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        progress = LoadingUtil.setLoading(this);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            Intent intent = new Intent(this, Main.class);
            if(SharedPrefManager.getInstance(this).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN")))
                intent = new Intent(Signin.this, AdminMain.class);
            startActivity(intent);
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
    ActivityResultLauncher<Intent> googleSignInActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    handleGoogleResp(task);
                }
            });
    private void googleSignIn(){
        gsc.signOut();
        Intent signInIntent = gsc.getSignInIntent();

        googleSignInActivityResultLauncher.launch(signInIntent);
    }
    private void handleGoogleResp(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account != null){
                LoginRequest loginRequest  =new LoginRequest(account.getEmail(), account.getId());
                String req = new Gson().toJson(loginRequest);
                JsonParser parser  =new JsonParser();
                progress.show();
                BaseAPIService.createService(IAuthService.class).login((JsonObject) parser.parse(req)).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if(response.isSuccessful() && response.body() != null){
                            AuthResponse authResponse = response.body();
                            if (response.body().getAccessToken() != null && !response.body().getAccessToken().equals("")) {
                                SharedPrefManager.getInstance(getApplicationContext()).saveAuthToken(authResponse);
                                saveCurrentUser(authResponse.getUserId());
                            }else{
                                googleSignUp(account, parser);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        progress.dismiss();
                    }
                });
            }
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private void googleSignUp(GoogleSignInAccount account, JsonParser parser){
        RegisterRequest registerRequest  =new RegisterRequest(
                account.getFamilyName(),
                account.getGivenName(),
                account.getEmail(),
                account.getId()
        );
        String req = new Gson().toJson(registerRequest);

        BaseAPIService.createService(IAuthService.class).signup((JsonObject) parser.parse(req)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(response.body() == null){
                    ToastUtil.showToast(binding.getRoot(),"Failed to register account with this information", false);
                    return;
                }
                AuthResponse authResponse = response.body();
                if (authResponse.getAccessToken() != null && !authResponse.getAccessToken().equals("")) {
                    SharedPrefManager.getInstance(getApplicationContext()).saveAuthToken(authResponse);
                    saveCurrentUser(authResponse.getUserId());
                }else{
                    ToastUtil.showToast(binding.getRoot(),"Failed to register account with this information", false);
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.d("error", t.getMessage());
                progress.dismiss();
            }
        });
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
        binding.signinBtnGoogleLogin.setOnClickListener(view -> {
            googleSignIn();
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
                    ToastUtil.showToast(binding.getRoot(),"Email or password is incorrect", false);
                    progress.dismiss();
                    return;
                }
                if (!response.body().getAccessToken().equals("")) {
                    AuthResponse authResponse = response.body();

                    SharedPrefManager.getInstance(getApplicationContext()).saveAuthToken(authResponse);
                    saveCurrentUser(authResponse.getUserId());

                }else{
                    ToastUtil.showToast(binding.getRoot(),"Email or password is incorrect", false);
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
                    ToastUtil.showToast(binding.getRoot(),"Cannot login", false);
                    return;
                }
                User currentUser = response.body();
                SharedPrefManager.getInstance(getApplicationContext()).saveUser(currentUser);
                if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {
                    ToastUtil.showToast(binding.getRoot(),"Logging successfully", true);
                    Intent intent = new Intent(Signin.this, Main.class);
                    if(currentUser.getRoles().stream().anyMatch(x -> x.contains("ADMIN")))
                        intent = new Intent(Signin.this, AdminMain.class);
                    startActivity(intent);
                    progress.dismiss();
                }else{
                    ToastUtil.showToast(binding.getRoot(),"Cannot login", false);
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