package com.ms.food_app.services;

import android.content.Context;

import com.ms.food_app.activities.Main;
import com.ms.food_app.models.response.AuthResponse;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        AuthResponse authResponse = SharedPrefManager.getInstance(ContextUtil.context).getAuthToken();
        request = request.newBuilder().addHeader("Authorization", "Bearer " + authResponse.getAccessToken()).build();
        return chain.proceed(request);
    }
}
