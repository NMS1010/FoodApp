package com.ms.food_app.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
            DecodedJWT decodedJWT = JWT.decode(authResponse.getAccessToken());
            if(isJWTExpired(decodedJWT) ){
                if(!request.url().url().getPath().contains("refresh")){
                    authResponse = refreshToken();
                    if(authResponse != null)
                        SharedPrefManager.getInstance(ContextUtil.context).saveAuthToken(authResponse);
                    else
                        SharedPrefManager.getInstance(ContextUtil.context).logout();
                }
            }
            if(!request.url().url().getPath().contains("refresh") && authResponse != null)
                request = request.newBuilder().addHeader("Authorization", "Bearer " + authResponse.getAccessToken()).build();
            else
                request = request.newBuilder().removeHeader("Authorization").build();
        }
        return chain.proceed(request);
    }
    boolean isJWTExpired(DecodedJWT decodedJWT) {
        Date expiresAt = decodedJWT.getExpiresAt();
        return expiresAt.before(new Date());
    }
    private AuthResponse refreshToken() throws IOException {
        AuthResponse authResponse = SharedPrefManager.getInstance(ContextUtil.context).getAuthToken();
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.setTokenRefresh(authResponse.getRefreshToken());
        String req = new Gson().toJson(refreshRequest);
        JsonParser parser = new JsonParser();
        retrofit2.Response<AuthResponse> resp = BaseAPIService
                .createService(IAuthService.class)
                .refreshToken((JsonObject) parser.parse(req))
                .execute();
        if(resp != null && resp.isSuccessful()){
            authResponse = resp.body();
            return authResponse;
        }
        return null;
    }
}
