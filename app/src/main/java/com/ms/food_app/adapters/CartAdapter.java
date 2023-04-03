package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.food_app.R;
import com.ms.food_app.databinding.CartItemBinding;
import com.ms.food_app.models.Cart;
import com.ms.food_app.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }
    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartItemBinding binding = CartItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartAdapter.CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Glide.with(context).load(cartItem.getProduct().getImages().get(0)).into(holder.binding.imageFood);
        holder.binding.nameFood.setText(cartItem.getProduct().getName());
        holder.binding.priceFood.setText(String.valueOf(cartItem.getProduct().getPrice()));
        holder.binding.count.setText(String.valueOf(cartItem.getCount()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCart(List<CartItem> cartItems){
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private CartItemBinding binding;

        public CartViewHolder(@NonNull CartItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
