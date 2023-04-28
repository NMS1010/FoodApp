package com.ms.food_app.utils;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.models.User;
import com.ms.food_app.models.response.AuthResponse;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "retrofitregisterlogin";
    private static final String KEY_USER = "keyuser";
    private static final String KEY_AUTH_TOKEN = "keyauthtoken";
    private static SharedPrefManager instance;
    private static Context ctx;

    public SharedPrefManager(Context context) {
        ctx = context;
    }
    public static synchronized  SharedPrefManager getInstance(Context context){
        if(instance == null){
            instance = new SharedPrefManager(context);
        }
        return instance;
    }
    //this method will store the user data in shared preferences
    public void saveUser (User user) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String currentUser = gson.toJson(user);
        editor.putString(KEY_USER, currentUser);
        editor.apply();
    }
    public void saveAuthToken(AuthResponse authResponse){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String currentUser = gson.toJson(authResponse);
        editor.putString(KEY_AUTH_TOKEN, currentUser);
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER,null) != null &&
                sharedPreferences.getString(KEY_AUTH_TOKEN, null) != null &&
                getUser() != null &&
                getAuthToken() != null &&
                getAuthToken().getAccessToken() != null &&
                getAuthToken().getRefreshToken() != null;
    }
    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String currentUser = sharedPreferences.getString(KEY_USER,"");
        return gson.fromJson(currentUser, User.class);
    }
    public AuthResponse getAuthToken() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences (SHARED_PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String authToken = sharedPreferences.getString(KEY_AUTH_TOKEN,"");
        return gson.fromJson(authToken, AuthResponse.class);
    }
    public void logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(ctx, Signin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

}