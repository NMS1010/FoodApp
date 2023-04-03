package com.ms.food_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.R;
import com.ms.food_app.adapters.CartAdapter;
import com.ms.food_app.databinding.ActivityCartBinding;
import com.ms.food_app.models.CartItem;
import com.ms.food_app.models.Category;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ICartService;
import com.ms.food_app.services.ICategoryService;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private com.ms.food_app.models.Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        setAdapter();
        loadCart();
        setEvents();
    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent(this, Main.class);
            intent.putExtra("Check","Home");
            startActivity(intent);
        });
        binding.Checkout.setOnClickListener(view -> {
            startActivity(new Intent(this, Checkout.class));
        });
    }
    private void setAdapter(){
        if(cart == null)
            cart = new com.ms.food_app.models.Cart(new ArrayList<>());
        adapter = new CartAdapter(this, cart.getCartItems());
        binding.cartRV.setAdapter(adapter);
        binding.cartRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
    private void loadCart(){
        if(!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, Signin.class));
            return;
        }
        User currentUser = SharedPrefManager.getInstance(this).getUser();
        BaseAPIService.createService(ICartService.class).getCartByUserId(currentUser.getId()).enqueue(new Callback<com.ms.food_app.models.Cart>() {
            @Override
            public void onResponse(Call<com.ms.food_app.models.Cart> call, Response<com.ms.food_app.models.Cart> response) {
                if(response.isSuccessful() && response.body() != null) {
                    cart = response.body();
                    adapter.updateCart(cart.getCartItems());
                    double totalPrice = 0;
                    for (CartItem ci: cart.getCartItems()) {
                        totalPrice += ci.getCount() * ci.getProduct().getPrice();
                    }
                    binding.totalPrice.setText(totalPrice + " VND");
                }
            }

            @Override
            public void onFailure(Call<com.ms.food_app.models.Cart> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
}