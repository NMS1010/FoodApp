package com.ms.food_app.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.activities.OrderDetail;
import com.ms.food_app.databinding.ActivityOrderStatusBinding;
import com.ms.food_app.models.Order;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IOrderService;
import com.ms.food_app.utils.Constants;
import com.ms.food_app.utils.LoadingUtil;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
    private ActivityOrderStatusBinding binding;
    private Order order;
    private RadioButton radioButton;
    private String chosenStatus;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderStatusBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress = LoadingUtil.setLoading(this);
        setEvent();
        loadData();
    }
    private void loadData(){
        String o = getIntent().getStringExtra("order");
        if(o != null && !o.equals("")){
            order  = new Gson().fromJson(o, Order.class);
        }
        if(order != null ){
            chosenStatus = order.getStatus();
            binding.orderTextId.setText("Order #" + order.getId());
            int radioButtonCount = binding.statusRadioGroup.getChildCount();
            if(Objects.equals(chosenStatus, Constants.DELIVERED) ||
                    Objects.equals(chosenStatus, Constants.CANCELLED) ){
                for(int i = 0; i < binding.statusRadioGroup.getChildCount(); i++){
                    View radioButtonView = binding.statusRadioGroup.getChildAt(i);
                    if(radioButtonView instanceof RadioButton) {
                        RadioButton radioButton = (RadioButton) radioButtonView;
                        radioButton.setClickable(false);
                        String radioButtonValue = radioButton.getText().toString();
                        if(radioButtonValue.equals(order.getStatus())) {
                            radioButton.setTextColor(this.getResources().getColor(R.color.fbutton_color_green_sea));
                        }else{
                            radioButton.setTextColor(this.getResources().getColor(R.color.grey));
                        }
                    }
                }
            }
            for(int i = 0; i < radioButtonCount; i++) {
                View radioButtonView = binding.statusRadioGroup.getChildAt(i);


                if(radioButtonView instanceof RadioButton) {
                    RadioButton radioButton = (RadioButton) radioButtonView;
                    String radioButtonValue = radioButton.getText().toString();

                    if(radioButtonValue.equals(order.getStatus())) {
                        radioButton.setChecked(true);
                        break;
                    }
                }
            }
        }
    }
    private void setEvent(){
        binding.saveButton.setOnClickListener(view -> {
            progress.show();
            BaseAPIService.createService(IOrderService.class).updateStatus(order.getId(), chosenStatus).enqueue(new Callback<List<Order>>() {
                @Override
                public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Intent intent = new Intent(getApplicationContext(), OrderDetail.class);
                        intent.putExtra("orderId", order.getId());
                        startActivity(intent);
                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<List<Order>> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progress.dismiss();
                }
            });
        });
        binding.backButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), OrderDetail.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });
        binding.statusRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            radioButton = findViewById(i);
            chosenStatus = radioButton.getText().toString();
        });
    }
}