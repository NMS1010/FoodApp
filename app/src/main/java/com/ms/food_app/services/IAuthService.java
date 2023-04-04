package com.ms.food_app.services;

import com.google.gson.JsonObject;
import com.ms.food_app.models.User;
import com.ms.food_app.models.requests.LoginRequest;
import com.ms.food_app.models.requests.RegisterRequest;
import com.ms.food_app.models.response.AuthResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IAuthService {
    @POST("auth/authenticate")
    Call<AuthResponse> login(@Body JsonObject params);

    @POST("auth/register")
    Call<AuthResponse> signup(@Body JsonObject params);
    @POST("auth/refresh")
    Call<AuthResponse> refreshToken(@Body JsonObject params);
}
