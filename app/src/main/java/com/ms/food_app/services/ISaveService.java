package com.ms.food_app.services;

import com.ms.food_app.models.Save;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ISaveService {
    @GET("users/follow-product")
    Call<Save> getSaveProductByUserId(@Query("userId") long userId);

    @POST("users/follow-product")
    Call<Save> saveProduct(@Query("userId") long userId, @Query("productId") long productId);

    @PUT("users/follow-product")
    Call<Save> unSaveProduct(@Query("userId") long userId, @Query("productId") long productId);
}
