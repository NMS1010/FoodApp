package com.ms.food_app.activities;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
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

import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.adapters.CartAdapter;
import com.ms.food_app.databinding.ActivityCartBinding;
import com.ms.food_app.models.CartItem;
import com.ms.food_app.models.Category;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ICartService;
import com.ms.food_app.services.ICategoryService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private com.ms.food_app.models.Cart cart;
    private ProgressDialog progress;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, IntroScreen.class));
            return;
        }
        user = SharedPrefManager.getInstance(this).getUser();
        progress = LoadingUtil.setLoading(this);
        progress.show();
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
            if(user.getAddresses() == null || user.getAddresses().size() == 0) {
                ToastUtil.showToast(binding.getRoot(), "Please set your address", false);
                return;
            }
            Intent intent = new Intent(this, Checkout.class);
            String req = new Gson().toJson(cart);
            intent.putExtra("cart", req);
            startActivity(intent);
        });
        binding.MainButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, Main.class);
            intent.putExtra("Check","Home");
            startActivity(intent);
        });
    }
    private void setAdapter(){
        Consumer<Integer> emptyCart = (Integer i) -> binding.noItem.setVisibility(i);
        if(cart == null)
            cart = new com.ms.food_app.models.Cart(new ArrayList<>());
        Consumer<Double> updateTotalPrice = totalPrice -> {
            binding.totalPrice.setText(totalPrice + " VND");
        };
        adapter = new CartAdapter(this, cart.getCartItems(), updateTotalPrice, emptyCart);
        binding.cartRV.setAdapter(adapter);
        binding.cartRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
    private void loadCart(){
        if(!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, IntroScreen.class));
            return;
        }
        User currentUser = SharedPrefManager.getInstance(this).getUser();
        BaseAPIService.createService(ICartService.class)
                .getCartByUserId(currentUser.getId())
                .enqueue(new Callback<com.ms.food_app.models.Cart>() {
            @Override
            public void onResponse(Call<com.ms.food_app.models.Cart> call, Response<com.ms.food_app.models.Cart> response) {
                if(response.isSuccessful() && response.body() != null) {
                    cart = response.body();
                    if(cart.getCartItems().size() == 0){
                        binding.noItem.setVisibility(VISIBLE);
                    }
                    else{
                        binding.noItem.setVisibility(View.GONE);
                    }
                    setAdapter();
                    double totalPrice = 0;
                    for (CartItem ci: cart.getCartItems()) {
                        totalPrice += ci.getCount() * ci.getProduct().getPrice();
                    }
                    binding.totalPrice.setText(totalPrice + " VND");
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<com.ms.food_app.models.Cart> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
}