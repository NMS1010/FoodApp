package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.adapters.CustomerReviewAdapter;
import com.ms.food_app.databinding.ActivityCustomerReviewBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.Review;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IReviewService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerReview extends AppCompatActivity {
    private ActivityCustomerReviewBinding binding;
    private ProgressDialog progress;
    private Product product;

    private CustomerReviewAdapter adapter;
    private List<Review> reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerReviewBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        progress = LoadingUtil.setLoading(this);
        String param = intent.getStringExtra("product");
        if(param != null && !param.isEmpty()){
            product = new Gson().fromJson(param, Product.class);
            setEvents();
            setAdapter();
            loadReview();
        }else{
            ToastUtil.showToast(binding.getRoot(),"Cannot load review", false);
            finish();
        }
    }

    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProductDetail.class);
            intent.putExtra("product", new Gson().toJson(product));
            startActivity(intent);
        });
    }
    private void setAdapter(){
        if(reviews == null)
            reviews  =new ArrayList<>();
        adapter = new CustomerReviewAdapter(this, reviews);
        binding.CustomerReviewRV.setAdapter(adapter);
        binding.CustomerReviewRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
    private void loadReview(){
        progress.show();
        binding.reviewRate.setText(String.valueOf(product.getRating()));
        BaseAPIService.createService(IReviewService.class).getReviewByProduct(product.getId()).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if(response.isSuccessful() && response.body() != null){
                    reviews = response.body();
                    binding.counterReviewOrder.setText(reviews.size() + " ratings");
                    adapter.updateReview(reviews);
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
    }
}