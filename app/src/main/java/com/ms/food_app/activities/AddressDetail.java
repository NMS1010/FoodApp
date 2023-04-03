package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityAddressDetailBinding;
import com.ms.food_app.models.Address;

import java.util.Objects;

public class AddressDetail extends AppCompatActivity {
    private ActivityAddressDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressDetailBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        setEvents();
        loadData();
    }

    private void setEvents(){
        binding.backBtnAddressDetail.setOnClickListener(view -> {
            startActivity(new Intent(this, AddressList.class));
        });
    }
    private void loadData(){
        Intent intent = getIntent();
        String res = intent.getStringExtra("New");
        if(!Objects.equals(res, "Add")){
            binding.actionBtnAddressDetail.setText("Update");
            Address addressItem = (new Gson()).fromJson(res, Address.class);
            binding.addressDetailPhoneEt.setText(addressItem.getPhone());
            binding.addressDetailDistrictEt.setText(addressItem.getDistrict());
            binding.addressDetailNameEt.setText(addressItem.getUsername());
            binding.addressDetailProvinceEt.setText(addressItem.getProvince());
            binding.addressDetailStreetEt.setText(addressItem.getStreet());
            binding.addressDetailWardEt.setText(addressItem.getWard());
            binding.defaultSwitchAddressDetail.setChecked(addressItem.getStatus());
        }
    }
}