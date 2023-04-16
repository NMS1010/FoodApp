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

import com.ms.food_app.R;
import com.ms.food_app.adapters.SaveListAdapter;
import com.ms.food_app.databinding.ActivitySaveListBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.Save;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ISaveService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveList extends AppCompatActivity {
    private ActivitySaveListBinding binding;
    private SaveListAdapter adapter;
    private List<Product> products;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySaveListBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress  = LoadingUtil.setLoading(this);
        setEvents();
        setAdapter();
        loadData();
    }
    private void setAdapter(){
        if(products == null)
            products  =new ArrayList<>();
        adapter = new SaveListAdapter(this, products);

        binding.favoriteMenuRv.setAdapter(adapter);
        binding.favoriteMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }
    private void loadData(){
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            SharedPrefManager.getInstance(this).logout();
            this.startActivity(new Intent(this, IntroScreen.class));
            return;
        }
        User user = SharedPrefManager.getInstance(this).getUser();
        progress.show();
        BaseAPIService.createService(ISaveService.class).getSaveProductByUserId(user.getId()).enqueue(new Callback<Save>() {
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {
                if(response.isSuccessful() && response.body() != null){
                    Save save = response.body();
                    products = save.getProducts();
                    adapter.updateSave(products);
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Save> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
    }
    private void setEvents(){
        binding.back.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.putExtra("Check", "Profile");
            startActivity(intent);
        });
        binding.cart.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Cart.class));
        });
    }
}