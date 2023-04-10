package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityAddressDetailBinding;
import com.ms.food_app.models.Address;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IUserService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressDetail extends AppCompatActivity {
    private ActivityAddressDetailBinding binding;
    private ProgressDialog progress ;
    private Address addressItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressDetailBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress = LoadingUtil.setLoading(this);
        setEvents();
        loadData();
    }

    private void setEvents(){
        binding.backBtnAddressDetail.setOnClickListener(view -> {
            startActivity(new Intent(this, AddressList.class));
        });
        binding.actionBtnAddressDetail.setOnClickListener(view -> {
            if(!SharedPrefManager.getInstance(this).isLoggedIn()) {
                SharedPrefManager.getInstance(this).logout();
                startActivity(new Intent(this, IntroScreen.class));
            }
            User currUser = SharedPrefManager.getInstance(this).getUser();
            Address address = new Address();
            address.setUserId(currUser.getId());
            if(addressItem != null)
                address.setId(addressItem.getId());
            address.setUsername(binding.addressDetailNameEt.getText().toString());
            address.setDistrict(binding.addressDetailDistrictEt.getText().toString());
            address.setPhone(binding.addressDetailPhoneEt.getText().toString());
            address.setProvince(binding.addressDetailProvinceEt.getText().toString());
            address.setStreet(binding.addressDetailStreetEt.getText().toString());
            address.setStatus(binding.defaultSwitchAddressDetail.isChecked());
            address.setWard(binding.addressDetailWardEt.getText().toString());
            String request = new Gson().toJson(address);
            JsonParser parser  = new JsonParser();
            progress.show();
            BaseAPIService.createService(IUserService.class).uploadAddresses((JsonObject) parser.parse(request)).enqueue(new Callback<List<Address>>() {
                @Override
                public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        finish();
                        startActivity(new Intent(getApplicationContext(), AddressList.class));
                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<List<Address>> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progress.dismiss();
                }
            });
        });
    }
    private void loadData(){
        Intent intent = getIntent();
        String res = intent.getStringExtra("New");
        if(!Objects.equals(res, "Add")){
            binding.actionBtnAddressDetail.setText("Update");
            addressItem = (new Gson()).fromJson(res, Address.class);
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