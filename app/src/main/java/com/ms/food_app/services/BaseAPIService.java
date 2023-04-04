package com.ms.food_app.services;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseAPIService {
    private static final String BASE_URL = "http://10.0.2.2:8081/api/v1/";
    private static final OkHttpClient httpClient
            = new OkHttpClient
            .Builder()
            .authenticator(new RequestAuthenticator())
            .addInterceptor(new RequestInterceptor())
            .build();
    private static final Retrofit.Builder builder
            = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create());

    private static final Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {

        return retrofit.create(serviceClass);
    }
}
