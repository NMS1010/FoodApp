package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.activities.ProductDetail;
import com.ms.food_app.databinding.ProductItemHorizontalBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.Save;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ISaveService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> products;
    private ProgressDialog progress;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        progress = LoadingUtil.setLoading(context);
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductItemHorizontalBinding binding = ProductItemHorizontalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context).load(product.getImages().get(0)).into(holder.binding.imageFood);
        holder.binding.nameFood.setText(product.getName());
        holder.binding.priceFood.setText(String.valueOf(product.getPrice()));
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("product", new Gson().toJson(product));
            context.startActivity(intent);
        });
        holder.binding.countSold.setText(product.getSold() + " orders");
        holder.binding.rateReview.setText(String.valueOf(product.getRating()));
        holder.binding.favoriteFood.setOnClickListener(view -> {
            User user = SharedPrefManager.getInstance(context).getUser();
            progress.show();
            BaseAPIService.createService(ISaveService.class).saveProduct(user.getId(), product.getId()).enqueue(new Callback<Save>() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onResponse(Call<Save> call, Response<Save> response) {
                    if(response.isSuccessful() && response.body() != null){
                        ToastUtil.showToast(context, "Succeed in adding product to your save list");
                    }else{
                        ToastUtil.showToast(context, "Failed to add product to your save list");
                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<Save> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progress.dismiss();
                }
            });
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateProducts(List<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private ProductItemHorizontalBinding binding;

        public ProductViewHolder(@NonNull ProductItemHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
