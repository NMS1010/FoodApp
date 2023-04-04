package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityProductDetailBinding;
import com.ms.food_app.models.CartItem;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ICartService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetail extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private long productId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        String param = intent.getStringExtra("productId");
        if(param != null && !param.isEmpty()){
            productId = Integer.parseInt(param);
        }
        loadProduct();
        setEvents();
    }
    private void loadProduct(){
        BaseAPIService
                .createService(IProductService.class)
                .getProductById(productId)
                .enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful() && response.body() != null){
                    Product product = response.body();
                    binding.nameDetail.setText(product.getName());
                    binding.descripDetail.setText(product.getDescription());
                    binding.priceDetail.setText(product.getPrice() + " VND");
                    Glide.with(getApplicationContext())
                            .load(product.getImages().get(0))
                            .into(binding.imageDetail);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent(this, Main.class);
            finish();
            startActivity(intent);
        });
        binding.review.setOnClickListener(view -> {
            startActivity(new Intent(this, CustomerReview.class));
        });
        binding.cart.setOnClickListener(view -> {
            startActivity(new Intent(this, Cart.class));
        });
        binding.addToCart.setOnClickListener(view -> {
            User currUser = SharedPrefManager.getInstance(this).getUser();
            CartItem cartItem = new CartItem();
            cartItem.setCount(1);
            cartItem.setCartId(currUser.getCartId());
            Product product = new Product();
            product.setId(productId);
            cartItem.setProduct(product);
            String request = new Gson().toJson(cartItem);
            JsonParser parser = new JsonParser();
            BaseAPIService
                    .createService(ICartService.class)
                    .addProductToCart((JsonObject) parser.parse(request))
                    .enqueue(new Callback<com.ms.food_app.models.Cart>() {
                @Override
                public void onResponse(Call<com.ms.food_app.models.Cart> call, Response<com.ms.food_app.models.Cart> response) {
                    if(response.isSuccessful() && response.body() != null){
                        ToastUtil.showToast(getApplicationContext(), "Add product to your cart successfully");
                    }
                }

                @Override
                public void onFailure(Call<com.ms.food_app.models.Cart> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
        });
    }
}