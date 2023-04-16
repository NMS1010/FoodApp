package com.ms.food_app.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.food_app.databinding.CartItemBinding;
import com.ms.food_app.databinding.CheckoutItemBinding;
import com.ms.food_app.models.CartItem;
import com.ms.food_app.utils.LoadingUtil;

import java.util.List;
import java.util.function.Consumer;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>{
    private Context context;
    private List<CartItem> cartItems;
    private ProgressDialog progress;
    public CheckoutAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        progress = LoadingUtil.setLoading(context);
    }
    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CheckoutItemBinding binding = CheckoutItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CheckoutViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        Glide.with(context).load(cartItem.getProduct().getImages().get(0)).into(holder.binding.imageFood);
        holder.binding.nameFood.setText(cartItem.getProduct().getName());
        holder.binding.priceFood.setText(cartItem.getProduct().getPrice() + " VND");
        holder.binding.count.setText(String.valueOf(cartItem.getCount()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
    public class CheckoutViewHolder extends RecyclerView.ViewHolder {
        private CheckoutItemBinding binding;

        public CheckoutViewHolder(@NonNull CheckoutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
