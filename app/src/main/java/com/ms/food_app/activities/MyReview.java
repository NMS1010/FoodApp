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

import com.ms.food_app.R;
import com.ms.food_app.adapters.CustomerReviewAdapter;
import com.ms.food_app.adapters.MyReviewAdapter;
import com.ms.food_app.databinding.ActivityMyReviewBinding;
import com.ms.food_app.models.Review;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IReviewService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReview extends AppCompatActivity {
    private ActivityMyReviewBinding binding;
    private MyReviewAdapter adapter;
    private List<Review> reviews;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReviewBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress = LoadingUtil.setLoading(this);
        setEvents();
        setAdapter();
        loadReview();
    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.putExtra("Check", "Profile");
            startActivity(intent);
        });
    }
    private void setAdapter(){
        if(reviews == null)
            reviews  =new ArrayList<>();
        adapter = new MyReviewAdapter(this, reviews);
        binding.myReviewRV.setAdapter(adapter);
        binding.myReviewRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
    private void loadReview(){
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            SharedPrefManager.getInstance(this).logout();
            startActivity(new Intent(this, IntroScreen.class));
            return;
        }
        User user = SharedPrefManager.getInstance(this).getUser();
        progress.show();
        BaseAPIService.createService(IReviewService.class).getReviewByUser(user.getId()).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if(response.isSuccessful() && response.body() != null){
                    reviews = response.body();
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