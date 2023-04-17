package com.ms.food_app.services;

import com.ms.food_app.models.Product;
import com.ms.food_app.models.response.ProductResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IProductService {
    @GET("products")
    Call<ProductResponse> getAllProducts();

    @GET("products")
    Call<ProductResponse> getAllProducts(@Query("categoryId") Long categoryId);
    @GET("products")
    Call<ProductResponse> getAllProducts(@Query("search") String search);
    @GET("product")
    Call<Product> getProductById(@Query("id") long id);
}
