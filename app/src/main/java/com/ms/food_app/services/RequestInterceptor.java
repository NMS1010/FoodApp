package com.ms.food_app.services;

import android.content.Intent;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.activities.IntroScreen;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.models.User;
import com.ms.food_app.models.requests.RefreshRequest;
import com.ms.food_app.models.response.AuthResponse;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if(SharedPrefManager.getInstance(ContextUtil.context).isLoggedIn()) {
            AuthResponse authResponse = SharedPrefManager.getInstance(ContextUtil.context).getAuthToken();
            if(authResponse == null || authResponse.getAccessToken() == null || authResponse.getRefreshToken() == null){
                logout();
            }
            else {
                DecodedJWT decodedJWT = JWT.decode(authResponse.getAccessToken());
                if (isJWTExpired(decodedJWT)) {
                    if (!request.url().url().getPath().contains("refresh")) {
                        authResponse = refreshToken();
                        if (authResponse != null)
                            SharedPrefManager.getInstance(ContextUtil.context).saveAuthToken(authResponse);
                        else {
                            logout();
                        }
                    }
                }
                if (!request.url().url().getPath().contains("refresh") && authResponse != null)
                    request = request.newBuilder().addHeader("Authorization", "Bearer " + authResponse.getAccessToken()).build();
                else
                    request = request.newBuilder().removeHeader("Authorization").build();
            }
        }
        return chain.proceed(request);
    }
    void logout(){
        SharedPrefManager.getInstance(ContextUtil.context).logout();
        Intent intent =new Intent(ContextUtil.context, IntroScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextUtil.context.startActivity(intent);
    }
    boolean isJWTExpired(DecodedJWT decodedJWT) {
        Date expiresAt = decodedJWT.getExpiresAt();
        return expiresAt.before(new Date());
    }
    private AuthResponse refreshToken() throws IOException {
        AuthResponse authResponse = SharedPrefManager.getInstance(ContextUtil.context).getAuthToken();
        User user = SharedPrefManager.getInstance(ContextUtil.context).getUser();
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setTokenRefresh(authResponse.getRefreshToken());
        refreshRequest.setUserId(user.getId());
        String req = new Gson().toJson(refreshRequest);
        JsonParser parser = new JsonParser();
        retrofit2.Response<AuthResponse> resp = BaseAPIService
                .createService(IAuthService.class)
                .refreshToken((JsonObject) parser.parse(req))
                .execute();
        if(resp.body() != null && resp.isSuccessful()){
            authResponse = resp.body();
            return authResponse;
        }
        return null;
    }
}
