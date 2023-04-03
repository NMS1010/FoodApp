package com.ms.food_app.services;

import com.ms.food_app.models.Category;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ICategoryService {
    @GET("categorise")
    Call<ArrayList<Category>> getAllCategories();
}
