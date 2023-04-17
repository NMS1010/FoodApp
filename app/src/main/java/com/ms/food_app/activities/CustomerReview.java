package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityCustomerReviewBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.ToastUtil;

public class CustomerReview extends AppCompatActivity {
    private ActivityCustomerReviewBinding binding;
    private ProgressDialog progress;
    private Product product;
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
        }else{
            ToastUtil.showToast(this,"Cannot load review");
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
}