package com.ms.food_app.services;

import com.google.gson.JsonObject;
import com.ms.food_app.models.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IOrderService {
    @POST("order")
    Call<Order> createOrder(@Body JsonObject order);

    @GET("order/{id}")
    Call<Order> getOrderById(@Path("id") long id);

    @GET("order/user")
    Call<List<Order>> getOrderByStatus(@Query("userId") long userId, @Query("status") String status);

    @GET("order/user/{userId}")
    Call<List<Order>> getOrderByUser(@Path("userId") long userId);

}
