package com.ms.food_app.services;

import com.ms.food_app.models.Delivery;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IDeliveryService {
    @GET("deliverise")
    Call<List<Delivery>> getAllDelivery();
}
