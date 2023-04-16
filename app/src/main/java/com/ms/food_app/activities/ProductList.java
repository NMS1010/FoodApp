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

import com.ms.food_app.R;
import com.ms.food_app.adapters.ProductAdapter;
import com.ms.food_app.databinding.ActivityProductListBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.LoadingUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductList extends AppCompatActivity {
    private int count = 0;
    private final int PAGE_SIZE = 5;
    private int PAGE_INDEX = 0;
    private ArrayList<Product> allProducts;
    private ArrayList<Product> currProducts;
    private ActivityProductListBinding binding;
    private ProductAdapter adapter;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        allProducts  =new ArrayList<>();
        currProducts = new ArrayList<>();
        progress = LoadingUtil.setLoading(this);
        fetchData();
        setData(PAGE_INDEX, PAGE_SIZE);
        adapter = new ProductAdapter(this, currProducts);
        binding.productRV.setAdapter(adapter);
        binding.productRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        setEvent();
    }
    private void setEvent(){
        binding.idNestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            count = 0;
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                count++;
                binding.productLoading.setVisibility(View.VISIBLE);
                if (count < 20) {
                    setData(PAGE_INDEX, PAGE_SIZE);
                    binding.productLoading.setVisibility(View.GONE);
                }
            }
        });
        binding.backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
        });
    }
    private void fetchData(){
        progress.show();
        BaseAPIService.createService(IProductService.class).getAllProducts().enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    allProducts = response.body();
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Log.d("error", t.getMessage());
                progress.dismiss();
            }
        });
    }
    private void setData(int begin, int amount){
        if(begin + amount > allProducts.size())
            return;
        for(int i=begin; i< begin + amount; i++){
            currProducts.add(allProducts.get(i));
        }
        adapter.updateProducts(currProducts);
        PAGE_INDEX ++;
    }
}