package com.ms.food_app.activities;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityFeedbackBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.Review;
import com.ms.food_app.models.User;
import com.ms.food_app.models.UserReviewDto;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IReviewService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feedback extends AppCompatActivity {
    private ActivityFeedbackBinding binding;
    private long orderId;
    private Product product;
    private Review rv;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        product = new Gson().fromJson(intent.getStringExtra("product"), Product.class);
        orderId = intent.getLongExtra("orderId", -1);
        if(product == null || orderId == -1){
            Log.d("Error", "Cannot feedback, please try again");
            Intent i = new Intent(this, OrderDetail.class);
            i.putExtra("orderId", orderId);
            startActivity(i);
            return;
        }
        String r = intent.getStringExtra("review");
        if(r != null && r != ""){
            rv = new Gson().fromJson(r, Review.class);
        }
        progress  = LoadingUtil.setLoading(this);
        loadData();
        setEvent();

    }
    private void loadData(){
        binding.nameFood.setText(product.getName());
        Glide.with(this)
                .load(product.getImages().get(0))
                .into(binding.imageFood);
        binding.orderId.setText("#" + orderId);
        binding.priceFood.setText(product.getPrice() + " VND");
        if(rv != null){
            binding.inputReview.setText(rv.getContent());
            binding.ratingBar.setRating(rv.getRating());
        }
    }
    private void setEvent(){
        binding.backBtn.setOnClickListener(view -> {
            Intent i = new Intent(this, OrderDetail.class);
            i.putExtra("orderId", orderId);
            if(rv != null)
                i = new Intent(this, MyReview.class);
            startActivity(i);
        });
        binding.Submit.setOnClickListener(view -> {
            String content = binding.inputReview.getText().toString().trim();
            if(content == ""){
                binding.inputReview.setError("Please input your review");
                return;
            }
            if(!SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                startActivity(new Intent(getApplicationContext(), IntroScreen.class));
                return;
            }
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            Review review  = new Review();
            if (rv != null ){
                review.setId(rv.getId());
            }
            review.setContent(content);
            review.setOrderId(orderId);
            review.setProductId(product.getId());
            review.setRating(Math.round(binding.ratingBar.getRating()));
            UserReviewDto u = new UserReviewDto();
            u.setId(user.getId());
            u.setFirstname(user.getFirstname());
            u.setLastname(user.getLastname());
            u.setAvatar(user.getAvatar());
            review.setUserReviewDto(u);
            review.setApproved(true);
            String req = new Gson().toJson(review);
            JsonParser parser = new JsonParser();
            progress.show();
            BaseAPIService.createService(IReviewService.class).createReview((JsonObject) parser.parse(req)).enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    if(response.isSuccessful() && response.body() != null){
                        ToastUtil.showToast(binding.getRoot(), "Submit feedback successfully", true);
                        Intent intent = new Intent(getApplicationContext(), OrderDetail.class);
                        if(rv != null)
                            intent = new Intent(getApplicationContext(), MyReview.class);
                        intent.putExtra("orderId", orderId);
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<Review> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progress.dismiss();
                }
            });
        });
    }
}