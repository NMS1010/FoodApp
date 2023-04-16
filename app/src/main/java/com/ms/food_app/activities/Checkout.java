package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.adapters.CheckoutAdapter;
import com.ms.food_app.adapters.DeliveryAdapter;
import com.ms.food_app.databinding.ActivityCheckoutBinding;
import com.ms.food_app.models.Address;
import com.ms.food_app.models.CartItem;
import com.ms.food_app.models.Delivery;
import com.ms.food_app.models.Order;
import com.ms.food_app.models.OrderItem;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IDeliveryService;
import com.ms.food_app.services.IOrderService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Checkout extends AppCompatActivity {
    private ActivityCheckoutBinding binding;
    private com.ms.food_app.models.Cart cart;
    private CheckoutAdapter adapter;
    private DeliveryAdapter deliveryAdapter;
    private List<Delivery> deliveryList;
    private ProgressDialog progressDialog;
    private double totalPrice = 0;
    private Delivery chosenDelivery;
    private Address selectedAddress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog = LoadingUtil.setLoading(this);
        setContentView(binding.getRoot());
        loadData();
        setAdapters();
        setEvents();
        progressDialog = LoadingUtil.setLoading(this);
        progressDialog.show();
        loadDelivery();
    }

    private void loadData() {
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, IntroScreen.class));
            return;
        }
        Intent intent = getIntent();
        cart = new Gson().fromJson(intent.getStringExtra("cart"), com.ms.food_app.models.Cart.class);
        for (CartItem ci: cart.getCartItems()) {
            totalPrice += ci.getCount() * ci.getProduct().getPrice();
        }

        User user = SharedPrefManager.getInstance(this).getUser();
        List<Address> addresses = user.getAddresses();
        for (Address x : addresses) {
            if (x.getStatus()) {
                selectedAddress = x;
                break;
            }
        }
        if (selectedAddress == null) {
            ToastUtil.showToast(this, "Please set your address");
            startActivity(new Intent(this, Cart.class));
            return;
        }
        binding.name.setText(selectedAddress.getUsername());
        binding.address.setText(selectedAddress.getStreet() + ", " + selectedAddress.getWard() + ", " + selectedAddress.getDistrict() + ", " + selectedAddress.getProvince());

    }

    private void setAdapters() {
        if (cart == null)
            cart = new com.ms.food_app.models.Cart(new ArrayList<>());

        adapter = new CheckoutAdapter(this, cart.getCartItems());
        binding.orderList.setAdapter(adapter);
        binding.orderList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        if (deliveryList == null)
            deliveryList = new ArrayList<>();
        Consumer<Double> updateShippingPrice = shippingPrice -> {
            binding.shipping.setText(shippingPrice + " VND");
            binding.total.setText(totalPrice + shippingPrice + " VND");
        };
        binding.subPrice.setText(totalPrice + " VND");
        Consumer<Delivery> chooseDelivery = delivery -> {
            chosenDelivery = delivery;
        };
        deliveryAdapter = new DeliveryAdapter(this, deliveryList, updateShippingPrice, chooseDelivery);
        binding.deliveryList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.deliveryList.setAdapter(deliveryAdapter);
    }

    private void loadDelivery() {
        BaseAPIService.createService(IDeliveryService.class).getAllDelivery().enqueue(new Callback<List<Delivery>>() {
            @Override
            public void onResponse(Call<List<Delivery>> call, Response<List<Delivery>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deliveryList = response.body();
                    deliveryAdapter.updateDeliveries(response.body());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Delivery>> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void setEvents() {
        binding.back.setOnClickListener(view -> {
            finish();
            startActivity(new Intent(this, Cart.class));
        });
        binding.BtnOrder.setOnClickListener(view -> {
            if(!SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
                finish();
                startActivity(new Intent(this, IntroScreen.class));
                return;
            }
            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
            Order order = new Order();
            order.setAddress(binding.address.getText().toString());
            order.setDelivery(chosenDelivery);
            order.setUser(user);
            order.setAmountFromUser(totalPrice + chosenDelivery.getPrice());
            order.setPhone(selectedAddress.getPhone());
            order.setStatus("0");
            List<OrderItem> orderItems = new ArrayList<>();
            cart.getCartItems().forEach(ci -> {
                OrderItem oi = new OrderItem();
                oi.setCount(ci.getCount());
                oi.setProduct(ci.getProduct());
                orderItems.add(oi);
            });
            order.setOrderItems(orderItems);
            String req = new Gson().toJson(order);
            JsonParser parser = new JsonParser();
            progressDialog.show();
            BaseAPIService
                    .createService(IOrderService.class)
                    .createOrder((JsonObject) parser.parse(req))
                    .enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if(response.body() != null && response.isSuccessful()){
                        ToastUtil.showToast(getApplicationContext(), "Success in ordering this products");
                        finish();
                        startActivity(new Intent(getApplicationContext(), SuccessNotify.class));
                    }
                    else{
                        ToastUtil.showToast(getApplicationContext(), "Failed to ordering this products");
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progressDialog.dismiss();
                }
            });
        });
    }
}