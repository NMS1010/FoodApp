package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ms.food_app.adapters.AddressAdapter;
import com.ms.food_app.databinding.ActivityAddressListBinding;
import com.ms.food_app.models.Address;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IUserService;
import com.ms.food_app.utils.SharedPrefManager;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressList extends AppCompatActivity {
    private AddressAdapter adapter;
    private List<Address> addressList;
    private ActivityAddressListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressListBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        setAdapter();
        setEvents();
        loadAddress();
    }
    private void setEvents(){
        binding.backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, Main.class);
            intent.putExtra("Check", "Profile");
            startActivity(intent);
        });
        binding.addNewBtnAddress.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddressDetail.class);
            intent.putExtra("New", "Add");
            finish();
            startActivity(intent);
        });
    }
    private void setAdapter(){
        if(addressList == null)
            addressList = new ArrayList<>();
        adapter = new AddressAdapter(this, addressList);
        binding.addressListRVAddress.setAdapter(adapter);
        binding.addressListRVAddress.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void loadAddress(){
        if(!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, Signin.class));
            return;
        }
        User currentUser = SharedPrefManager.getInstance(this).getUser();
        BaseAPIService.createService(IUserService.class).getAddressByUserId(currentUser.getId()).enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    addressList = response.body();
                    adapter.updateAddress(addressList);
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                Log.d("error", t.getMessage());
            }
        });
    }
}