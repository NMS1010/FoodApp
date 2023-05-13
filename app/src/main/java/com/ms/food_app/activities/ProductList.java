package com.ms.food_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.adapters.ProductAdapter;
import com.ms.food_app.databinding.ActivityProductListBinding;
import com.ms.food_app.models.Category;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.response.ProductResponse;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.LoadingUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductList extends AppCompatActivity {
    private final int PAGE_SIZE = 5;
    private int PAGE_INDEX = 0;
    private int TOTAL_PAGE = -1;
    private boolean isLoading = false;
    private List<Product> currProducts;
    private ActivityProductListBinding binding;
    private ProductAdapter adapter;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        currProducts = new ArrayList<>();
        progress = LoadingUtil.setLoading(this);

        String cate = getIntent().getStringExtra("category");
        if(cate != null && !cate.equals("")){
            fetchByCategory(new Gson().fromJson(cate, Category.class));
        }else{
            fetchPagingData();
        }
        adapter = new ProductAdapter(this, currProducts);
        binding.productRV.setAdapter(adapter);
        binding.productRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        setEvent();
    }
    private void setEvent(){
        binding.idNestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!isLoading && scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                binding.productLoading.setVisibility(View.VISIBLE);
                fetchPagingData();
            }
        });
        binding.backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
        });
    }
    private void fetchPagingData(){
        if(TOTAL_PAGE != -1 && PAGE_INDEX > TOTAL_PAGE - 1){
            binding.productLoading.setVisibility(View.GONE);
            return;
        }
        isLoading = true;
        BaseAPIService.createService(IProductService.class).getAllProducts(PAGE_INDEX, PAGE_SIZE).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ProductResponse resp = response.body();
                    TOTAL_PAGE = resp.getTotalPage();
                    currProducts.addAll(resp.getProducts());
                    adapter.updateProducts(currProducts);
                    PAGE_INDEX ++;
                    binding.productLoading.setVisibility(View.GONE);
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
    private void fetchByCategory(Category category){
        binding.title.setText(category.getName());
        progress.show();
        BaseAPIService.createService(IProductService.class).getAllProducts(category.getId()).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    adapter.updateProducts(response.body().getProducts());
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
    }

}