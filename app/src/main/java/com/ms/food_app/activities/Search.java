package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.R;
import com.ms.food_app.adapters.ProductAdapter;
import com.ms.food_app.databinding.ActivitySearchBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.response.ProductResponse;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {
    private ActivitySearchBinding binding;
    private ProductAdapter adapter;
    private List<Product> products;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress  = LoadingUtil.setLoading(this);
        setAdapter();
        setEvents();
    }
    private void setAdapter(){
        products = new ArrayList<>();
        adapter = new ProductAdapter(this, products);
        binding.nFoodSeachRv.setAdapter(adapter);
        binding.nFoodSeachRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            startActivity(new Intent(this, Main.class));
        });
        binding.btnSearch.setOnClickListener(view -> {
            String txt = binding.edtSearch.getText().toString();
            if(txt.trim().equals("")){
                ToastUtil.showToast(binding.getRoot(), "Please type anything....", false);
            }else{
                progress.show();
                BaseAPIService.createService(IProductService.class).getAllProducts(txt).enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        if(response.isSuccessful() && response.body() != null){
                            products = response.body().getProducts();
                            if(products.size() == 0)
                                binding.noItem.setVisibility(View.VISIBLE);
                            else
                                binding.noItem.setVisibility(View.GONE);
                            adapter.updateProducts(products);
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
        });
    }
}