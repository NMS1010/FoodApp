package com.ms.food_app.services;

import com.ms.food_app.models.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface IProductService {
    @GET("/appfoods/lastproduct.php")
    Call<ArrayList<Product>> getAllProducts();
}
