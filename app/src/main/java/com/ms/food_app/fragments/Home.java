package com.ms.food_app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ms.food_app.R;
import com.ms.food_app.activities.Cart;
import com.ms.food_app.activities.Search;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.adapters.CategoryAdapter;
import com.ms.food_app.adapters.ProductAdapter;
import com.ms.food_app.adapters.SliderAdapter;
import com.ms.food_app.databinding.FragmentHomeBinding;
import com.ms.food_app.models.Category;
import com.ms.food_app.models.Product;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ICategoryService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {
    private FragmentHomeBinding binding;
    private ArrayList<Category> categories;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Product> products;
    private ProductAdapter productAdapter;
    private SliderAdapter sliderAdapter;
    private List<Integer> images;
    private ProgressDialog progress;
    public Home() {
        // Required empty public constructor
    }
    public static Home newInstance(String param1, String param2) {
        return new Home();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        ContextUtil.context = getActivity();
        // Inflate the layout for this fragment
        if(!SharedPrefManager.getInstance(getActivity()).isLoggedIn()){
            startActivity(new Intent(getActivity(), Signin.class));
        }
        progress = LoadingUtil.setLoading(getActivity());
        progress.show();
        setAdapter();
        setEvents();
        LoadCategories();
        LoadProducts();
        return binding.getRoot();
    }
    private void setAdapter(){
        categories = new ArrayList<>();
        products = new ArrayList<>();
        images = new ArrayList<Integer>(){
            {
                add(R.drawable.img1);
                add(R.drawable.img2);
                add(R.drawable.img3);
            }
        };

        categoryAdapter = new CategoryAdapter(getActivity(), categories);
        productAdapter = new ProductAdapter(getActivity(), products);
        sliderAdapter = new SliderAdapter(getActivity(), images);

        binding.imageSlider.setSliderAdapter(sliderAdapter);
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.WHITE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setScrollTimeInSec(4);
        binding.imageSlider.setAutoCycle(true);
        binding.imageSlider.startAutoCycle();

        binding.categoryRV.setAdapter(categoryAdapter);
        binding.categoryRV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        binding.productRV.setAdapter(productAdapter);
        binding.productRV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }
    private void setEvents(){
        binding.cart.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), Cart.class));
        });
        binding.searchView.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), Search.class));
        });
    }
    private void LoadCategories(){
        BaseAPIService.createService(ICategoryService.class).getAllCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {
                if(response.isSuccessful() && response.body() != null) {
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
                if(response.isSuccessful() && response.body() != null) {
                    productAdapter.updateProducts(response.body());
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
}