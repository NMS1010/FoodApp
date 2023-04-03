package com.ms.food_app.services;

import com.ms.food_app.models.Address;
import com.ms.food_app.models.Cart;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface IUserService {
    @GET("users/addresses/{id}")
    Call<List<Address>> getAddressByUserId(@Path("id") long id);
    @PUT("users/addresses/{id}")
    Call<List<Address>> deleteAddressById(@Path("id") long id);
}
