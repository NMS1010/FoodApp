package com.ms.food_app.services;

import com.google.gson.JsonObject;
import com.ms.food_app.models.Address;
import com.ms.food_app.models.Cart;
import com.ms.food_app.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IUserService {
    @GET("users/addresses/{id}")
    Call<List<Address>> getAddressByUserId(@Path("id") long id);
    @PUT("users/addresses/{id}")
    Call<List<Address>> deleteAddressById(@Path("id") long id);
    @POST("users/addresses")
    Call<List<Address>> uploadAddresses(@Body JsonObject params);
    @Multipart
    @POST("users")
    Call<User> updateProfile(@Query("model") JsonObject params, @Part MultipartBody.Part file);

    @POST("users")
    Call<User> updateProfile(@Query("model") JsonObject params);
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);
}
