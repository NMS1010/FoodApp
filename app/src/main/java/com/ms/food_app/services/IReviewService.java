package com.ms.food_app.services;

import com.google.gson.JsonObject;
import com.ms.food_app.models.Review;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IReviewService {

    @GET("review/product/{id}")
    Call<List<Review>> getReviewByProduct(@Path("id") long id);
    @GET("review/user/{id}")
    Call<List<Review>> getReviewByUser(@Path("id") long id);

    @POST("review")
    Call<Review> createReview(@Body JsonObject review);
}
