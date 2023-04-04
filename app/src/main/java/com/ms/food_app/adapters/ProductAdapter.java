package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.food_app.R;
import com.ms.food_app.activities.ProductDetail;
import com.ms.food_app.databinding.ProductItemHorizontalBinding;
import com.ms.food_app.models.Product;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
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
            intent.putExtra("productId", product.getId());
            context.startActivity(intent);
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateProducts(ArrayList<Product> products){
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
