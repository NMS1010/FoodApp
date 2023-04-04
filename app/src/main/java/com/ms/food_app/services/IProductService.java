package com.ms.food_app.services;

import com.ms.food_app.models.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IProductService {
    @GET("products")
    Call<ArrayList<Product>> getAllProducts();

    @GET("products")
    Call<Product> getProductById(@Query("id") long id);
}
