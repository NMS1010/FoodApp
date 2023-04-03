package com.ms.food_app.services;

import com.ms.food_app.models.Cart;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ICartService {
    @GET("cart/user/{id}")
    Call<Cart> getCartByUserId(@Path("id") long id);
}
