package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.R;
import com.ms.food_app.adapters.CategoryAdapter;
import com.ms.food_app.adapters.ProductAdapter;
import com.ms.food_app.adapters.SliderAdapter;
import com.ms.food_app.databinding.ActivityHomeBinding;
import com.ms.food_app.models.Category;
import com.ms.food_app.models.Product;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ICategoryService;
import com.ms.food_app.services.IProductService;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private ArrayList<Category> categories;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Product> products;
    private ProductAdapter productAdapter;
    private SliderAdapter sliderAdapter;
    private List<Integer> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categories = new ArrayList<>();
        products = new ArrayList<>();
        images = new ArrayList<Integer>(){
            {
                add(R.drawable.img1);
                add(R.drawable.img2);
                add(R.drawable.img3);
            }
        };

        categoryAdapter = new CategoryAdapter(this, categories);
        productAdapter = new ProductAdapter(this, products);
        sliderAdapter = new SliderAdapter(this, images);

        binding.imageSlider.setSliderAdapter(sliderAdapter);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(4); //set scroll delay in seconds :
        binding.imageSlider.setAutoCycle(true);
        binding.imageSlider.startAutoCycle();

        binding.categoryRV.setAdapter(categoryAdapter);
        binding.categoryRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        binding.productRV.setAdapter(productAdapter);
        binding.productRV.setLayoutManager(new GridLayoutManager(this, 2));

        LoadCategories();
        LoadProducts();
    }

    private void LoadCategories(){
        BaseAPIService.createService(ICategoryService.class).getAllCategories().enqueue(new Callback<ArrayList<Category>>() {
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
    private void LoadProducts(){
        BaseAPIService.createService(IProductService.class).getAllProducts().enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    productAdapter.updateProducts(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
}