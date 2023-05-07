package com.ms.food_app.activities;

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
import com.ms.food_app.activities.admin.AdminMain;
import com.ms.food_app.activities.admin.OrderStatus;
import com.ms.food_app.adapters.OrderDetailAdapter;
import com.ms.food_app.databinding.ActivityOrderDetailBinding;
import com.ms.food_app.models.Order;
import com.ms.food_app.models.OrderItem;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IOrderService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetail extends AppCompatActivity {
    private ActivityOrderDetailBinding binding;
    private OrderDetailAdapter adapter;
    private List<OrderItem> orderItemList;
    private ProgressDialog progressDialog;
    private long orderId;
    private Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        orderId = intent.getLongExtra("orderId", -1);
        progressDialog = LoadingUtil.setLoading(this);
        setEvents();
        setAdapter();
        loadData();
    }
    private void loadData(){
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            SharedPrefManager.getInstance(this).logout();
            startActivity(new Intent(this, IntroScreen.class));
            return;
        }
            if(SharedPrefManager.getInstance(this).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN")))
                binding.statusUpdate.setVisibility(View.VISIBLE);
            else
                binding.statusUpdate.setVisibility(View.GONE);
        if(orderId == -1){
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.putExtra("Check", "Order");
            startActivity(intent);
            return;
        }
        progressDialog.show();
        BaseAPIService.createService(IOrderService.class).getOrderById(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if(response.isSuccessful() && response.body() != null){
                    order = response.body();
                    orderItemList = response.body().getOrderItems();
                    adapter.updateOrderDetails(orderItemList, order);
                    binding.orderId.setText("#" + order.getId());
                    binding.fullName.setText(order.getUser().getFirstname() + " " + order.getUser().getLastname());
                    binding.orderDate.setText(order.getCreatedAt().toLocaleString());
                    binding.status.setText(order.getStatus());
                    binding.address.setText(order.getAddress());
                    binding.totalPrice.setText(String.valueOf(order.getAmountFromUser()));
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progressDialog.dismiss();
            }
        });

    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
                if(SharedPrefManager.getInstance(this).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN")))
                    intent = new Intent(this, AdminMain.class);
                startActivity(intent);
            }
            intent.putExtra("Check", "Order");
            startActivity(intent);
        });
        binding.statusUpdate.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), OrderStatus.class);
            intent.putExtra("order", new Gson().toJson(order));
            startActivity(intent);
        });
    }
    private void setAdapter(){
        if(orderItemList == null)
            orderItemList = new ArrayList<>();
        adapter= new OrderDetailAdapter(this, orderItemList);
        binding.orderItemRv.setAdapter(adapter);
        binding.orderItemRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
}