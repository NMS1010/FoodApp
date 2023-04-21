package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.activities.IntroScreen;
import com.ms.food_app.activities.ProductDetail;
import com.ms.food_app.databinding.SaveItemBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.Save;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ISaveService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveListAdapter extends RecyclerView.Adapter<SaveListAdapter.SaveListViewHolder> {
    private Context context;
    private List<Product> products;
    private ProgressDialog progress;

    public SaveListAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        progress = LoadingUtil.setLoading(context);
    }
    @NonNull
    @Override
    public SaveListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SaveItemBinding binding = SaveItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SaveListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SaveListViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context).load(product.getImages().get(0)).into(holder.binding.imageFood);
        holder.binding.nameFood.setText(product.getName());
        holder.binding.priceFood.setText(String.valueOf(product.getPrice()));
        holder.binding.rateReview.setText(String.valueOf(product.getRating()));
        holder.binding.countReview.setText(product.getSold() + " orders");
        holder.binding.favoriteFood.setSelected(true);
        holder.binding.favoriteFood.setOnClickListener(view -> {
            removeSaveItem(holder.binding.favoriteFood,product, position);
        });
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("product", new Gson().toJson(product));
            context.startActivity(intent);
        });
    }
    private void removeSaveItem(View view, Product product, int position){

        if(!SharedPrefManager.getInstance(context).isLoggedIn()){
            SharedPrefManager.getInstance(context).logout();
            context.startActivity(new Intent(context, IntroScreen.class));
            return;
        }
        User user = SharedPrefManager.getInstance(context).getUser();
        progress.show();

        BaseAPIService.createService(ISaveService.class).unSaveProduct(user.getId(), product.getId()).enqueue(new Callback<Save>() {
            @Override
            public void onResponse(Call<Save> call, Response<Save> response) {
                if(response.isSuccessful() && response.body() != null){
                    view.setSelected(false);
                    products.remove(position);
                    notifyItemRemoved(position);
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
    @SuppressLint("NotifyDataSetChanged")
    public void updateSave(List<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class SaveListViewHolder extends RecyclerView.ViewHolder {
        private SaveItemBinding binding;

        public SaveListViewHolder(@NonNull SaveItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
