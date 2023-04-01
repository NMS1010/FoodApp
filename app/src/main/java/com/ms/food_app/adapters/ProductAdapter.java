package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.food_app.R;
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
        View productItem = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(productItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Glide.with(context).load(product.getStrMealThumb()).into(holder.productItem_ImgV);
        holder.productItemName_Tv.setText(product.getStrMeal());
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

        private ImageView productItem_ImgV;
        private ImageView productItemSave_ImgV;
        private ImageView productItemCart_ImgV;
        private TextView productItemName_Tv;
        private TextView productItemPrice_Tv;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productItem_ImgV = itemView.findViewById(R.id.productItem_ImgV);
            productItemSave_ImgV = itemView.findViewById(R.id.productItemSave_ImgV);
            productItemCart_ImgV = itemView.findViewById(R.id.productItemCart_ImgV);
            productItemName_Tv = itemView.findViewById(R.id.addressItemName_Tv);
            productItemPrice_Tv = itemView.findViewById(R.id.productItemPrice_Tv);
        }
    }
}
