package com.ms.food_app.fragments.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ms.food_app.R;
import com.ms.food_app.activities.IntroScreen;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.adapters.CategoryAdapter;
import com.ms.food_app.adapters.ProductAdapter;
import com.ms.food_app.adapters.SliderAdapter;
import com.ms.food_app.databinding.FragmentHomeBinding;
import com.ms.food_app.databinding.FragmentProductsBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.response.ProductResponse;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Products extends Fragment {
    private FragmentProductsBinding binding;
    private ArrayList<Product> products;
    private ProductAdapter productAdapter;
    private ProgressDialog progress;
    private final int PAGE_SIZE = 5;
    private int PAGE_INDEX = 0;
    private int TOTAL_PAGE = -1;
    private boolean isLoading = false;
    public Products() {
        // Required empty public constructor
    }

    public static Products newInstance(String param1, String param2) {
        Products fragment = new Products();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        ContextUtil.context = getActivity();
        if(!SharedPrefManager.getInstance(getActivity()).isLoggedIn()){
            startActivity(new Intent(getActivity(), IntroScreen.class));
        }
        progress = LoadingUtil.setLoading(getActivity());
        setEvents();
        setAdapter();
        loadProducts();
        return binding.getRoot();
    }
    private void setEvents(){
        binding.idNestedSV.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!isLoading && scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                binding.productLoading.setVisibility(View.VISIBLE);
                loadProducts();
            }
        });
    }
    private void setAdapter(){
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(getActivity(), products);

        binding.productRV.setAdapter(productAdapter);
        binding.productRV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }
    private void loadProducts(){
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
                    products.addAll(resp.getProducts());
                    productAdapter.updateProducts(products);
                    PAGE_INDEX ++;
                    binding.productLoading.setVisibility(View.GONE);
                    isLoading = false;
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
}