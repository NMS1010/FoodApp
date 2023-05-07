package com.ms.food_app.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityProductStatusBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.requests.UpdateProductRequest;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductStatus extends AppCompatActivity {
    private ActivityProductStatusBinding binding;
    private Product product;
    private int count;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductStatusBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        progress = LoadingUtil.setLoading(this);
        setEvents();
        loadProduct();
    }
    private void setEvents(){
        binding.backBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, AdminMain.class));
        });
        binding.plus.setOnClickListener(view -> {
            count += 1;
            binding.count.setText(String.valueOf(count));
        });
        binding.minus.setOnClickListener(view -> {
            count -= 1;
            if(count < 0){
                count = 0;
            }
            binding.count.setText(String.valueOf(count));
        });
        binding.updateProduct.setOnClickListener(view -> {
            progress.show();
            UpdateProductRequest updateProductRequest = new UpdateProductRequest();
            updateProductRequest.setId(product.getId());
            updateProductRequest.setPrice(Double.parseDouble(binding.priceProduct.getText().toString()));
            updateProductRequest.setPromotionalPrice(Double.parseDouble(binding.promotionalPriceProduct.getText().toString()));
            updateProductRequest.setQuantity(count);

            String req = new Gson().toJson(updateProductRequest);
            JsonParser parser = new JsonParser();
            BaseAPIService.createService(IProductService.class).updateProduct((JsonObject) parser.parse(req)).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if(response.isSuccessful() && response.body() != null){
                        ToastUtil.showToast(getApplicationContext(), "Update successfully");
                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progress.dismiss();
                }
            });
        });
        binding.count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0){
                    count = Integer.parseInt(String.valueOf(charSequence));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void loadProduct(){
        Intent intent = getIntent();
        String p = intent.getStringExtra("product");
        if(p != null && !p.equals("")){
            product = new Gson().fromJson(p, Product.class);
        }
        if(product != null){
            count = product.getQuantity();
            binding.count.setText(String.valueOf(product.getQuantity()));
            binding.priceProduct.setText(String.valueOf(product.getPrice()));
            binding.promotionalPriceProduct.setText(String.valueOf(product.getPromotionalPrice()));
        }
    }
}