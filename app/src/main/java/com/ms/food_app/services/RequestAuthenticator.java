package com.ms.food_app.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.models.requests.RefreshRequest;
import com.ms.food_app.models.response.AuthResponse;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class RequestAuthenticator implements Authenticator {
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        AuthResponse authResponse = SharedPrefManager.getInstance(ContextUtil.context).getAuthToken();
        if (response.code() == 403){
            authResponse = refreshToken();
        }
        return response
                .request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + authResponse.getAccessToken())
                .build();
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
        }
        return authResponse;
    }
}
