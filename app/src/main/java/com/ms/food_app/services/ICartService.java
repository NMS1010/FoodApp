package com.ms.food_app.services;

import com.google.gson.JsonObject;
import com.ms.food_app.models.Cart;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ICartService {
    @GET("cart/user/{id}")
    Call<Cart> getCartByUserId(@Path("id") long id);
    @POST("cart")
    Call<Cart> addProductToCart(@Body JsonObject params);
    @PUT("cart/deleteOne")
    Call<Cart> removeOneProduct(@Query("cartItemId") long cartItemId);
    @DELETE("cart/deleteAll")
    Call<Cart> removeAllProduct(@Query("cartItemId") long cartItemId);
}
