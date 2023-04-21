package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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
import com.ms.food_app.models.Save;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ICartService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.services.ISaveService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetail extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private ProgressDialog progress;
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        progress = LoadingUtil.setLoading(this);
        String param = intent.getStringExtra("product");
        if(param != null && !param.isEmpty()){
            product = new Gson().fromJson(param, Product.class);
            loadProduct();
            setEvents();
        }else{
            ToastUtil.showToast(this,"Cannot load product");
            finish();
        }
    }
    private void loadProduct(){

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            SharedPrefManager.getInstance(this).logout();
            this.startActivity(new Intent(this, IntroScreen.class));
            return;
        }
        progress.show();
        User user = SharedPrefManager.getInstance(this).getUser();
        BaseAPIService.createService(ISaveService.class).getSaveProductByUserId(user.getId()).enqueue(new Callback<Save>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {
                if(response.isSuccessful() && response.body() != null){
                    binding.fav.setBackgroundColor(R.color.White);
                    for (Product p: response.body().getProducts()) {
                        if(p.getId() == product.getId()){
                            binding.fav.setSelected(true);
                            break;
                        }
                    }
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<Save> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
        binding.nameDetail.setText(product.getName());
        binding.descripDetail.loadDataWithBaseURL(null, product.getDescription(), "text/html", "UTF-8", null);
        binding.priceDetail.setText(product.getPrice() + " VND");
        Glide.with(getApplicationContext())
                .load(product.getImages().get(0))
                .into(binding.imageDetail);
    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent(this, Main.class);
            finish();
            startActivity(intent);
        });
        binding.review.setOnClickListener(view -> {
            Intent intent = new Intent(this, CustomerReview.class);
            intent.putExtra("product", new Gson().toJson(product));
            startActivity(intent);
        });
        binding.cart.setOnClickListener(view -> {
            startActivity(new Intent(this, Cart.class));
        });
        binding.fav.setOnClickListener(view -> {
            User user = SharedPrefManager.getInstance(this).getUser();
            progress.show();
            BaseAPIService.createService(ISaveService.class).saveProduct(user.getId(), product.getId()).enqueue(new Callback<Save>() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onResponse(Call<Save> call, Response<Save> response) {
                    if(response.isSuccessful() && response.body() != null){
                        if(!binding.fav.isSelected())
                            ToastUtil.showToast(getApplicationContext(), "Succeed in adding product to your save list");
                        binding.fav.setSelected(true);
                    }else{
                        ToastUtil.showToast(getApplicationContext(), "Failed to add product to your save list");
                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<Save> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progress.dismiss();
                }
            });
        });
        binding.addToCart.setOnClickListener(view -> {
            progress.show();
            User currUser = SharedPrefManager.getInstance(this).getUser();
            CartItem cartItem = new CartItem();
            cartItem.setCount(1);
            cartItem.setCartId(currUser.getCartId());
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
                        progress.dismiss();
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