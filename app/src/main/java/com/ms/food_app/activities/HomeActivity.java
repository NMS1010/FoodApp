package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.R;
import com.ms.food_app.adapters.CategoryAdapter;
import com.ms.food_app.databinding.ActivityHomeBinding;
import com.ms.food_app.models.Category;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.CategoryService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private ArrayList<Category> categories;
    private CategoryAdapter categoryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categories);
        binding.categoryRV.setAdapter(categoryAdapter);
        binding.categoryRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        LoadCategories();
    }

    private void LoadCategories(){
        BaseAPIService.createService(CategoryService.class).getAllCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    categoryAdapter.updateCategories(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Category>> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
}