package com.ms.food_app.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityOrderStatusBinding;
import com.ms.food_app.models.Order;

public class OrderStatus extends AppCompatActivity {
    private ActivityOrderStatusBinding binding;
    private Order order;
    private RadioButton radioButton;
    private String chosenStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderStatusBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        setEvent();
    }
    private void setEvent(){
        binding.saveButton.setOnClickListener(view -> {

        });
        binding.backButton.setOnClickListener(view -> {

        });
        binding.statusRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            radioButton = findViewById(i);
            chosenStatus = radioButton.getText().toString();
        });
    }
}