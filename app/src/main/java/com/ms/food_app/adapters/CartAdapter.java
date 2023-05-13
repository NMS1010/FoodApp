package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.R;
import com.ms.food_app.databinding.CartItemBinding;
import com.ms.food_app.models.Cart;
import com.ms.food_app.models.CartItem;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.ICartService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private Consumer<Double> updateTotalPrice;
    private ProgressDialog progress;
    private Consumer<Integer> emptyCart;
    public CartAdapter(Context context, List<CartItem> cartItems,  Consumer<Double> updateTotalPrice, Consumer<Integer> emptyCart) {
        this.context = context;
        this.cartItems = cartItems;
        this.updateTotalPrice = updateTotalPrice;
        this.emptyCart = emptyCart;
        progress = LoadingUtil.setLoading(context);
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
        if(cartItem.getProduct().getQuantity() == 0){
            removeCartItem(cartItem, position);
            return;
        }
        Glide.with(context).load(cartItem.getProduct().getImages().get(0)).into(holder.binding.imageFood);
        holder.binding.nameFood.setText(cartItem.getProduct().getName());
        holder.binding.priceFood.setText(cartItem.getProduct().getPrice() + " VND");
        holder.binding.count.setText(String.valueOf(cartItem.getCount()));
        holder.binding.rateReview.setText(String.valueOf(cartItem.getProduct().getRating()));
        holder.binding.countSold.setText(cartItem.getProduct().getSold() + " orders");
        holder.binding.delete.setOnClickListener(view -> {
            removeCartItem(cartItem, position);
        });
        holder.binding.plus.setOnClickListener(view -> {
            increaseCartItem(cartItem, position, cartItem.getCount());
        });
        holder.binding.minus.setOnClickListener(view -> {
            int amount = cartItem.getCount() - 1;
            if(amount <= 0){
                removeCartItem(cartItem, position);
            }
            else{
                decreaseCartItem(cartItem, position);
            }
        });
    }
    private void increaseCartItem(CartItem cartItem, int position, int count){
        progress.show();
        User currUser = SharedPrefManager.getInstance(context).getUser();
        cartItem.setCount(1);
        cartItem.setCartId(currUser.getCartId());
        String request = new Gson().toJson(cartItem);
        JsonParser parser = new JsonParser();
        BaseAPIService
                .createService(ICartService.class)
                .addProductToCart((JsonObject) parser.parse(request))
                .enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if(response.isSuccessful() && response.body() != null){
                    cartItem.setCount(count + 1);
                    notifyItemChanged(position);
                    double totalPrice = 0;
                    for (CartItem ci: response.body().getCartItems()) {
                        totalPrice += (ci.getProduct().getPrice() * ci.getCount());
                    }
                    updateTotalPrice.accept(totalPrice);
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
    }
    private void decreaseCartItem(CartItem cartItem, int position){
        progress.show();
        BaseAPIService.createService(ICartService.class).removeOneProduct(cartItem.getId()).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if(response.isSuccessful() && response.body() != null){
                    cartItem.setCount(cartItem.getCount() - 1);
                    notifyItemChanged(position);
                    double totalPrice = 0;
                    for (CartItem ci: response.body().getCartItems()) {
                        totalPrice += (ci.getProduct().getPrice() * ci.getCount());
                    }
                    updateTotalPrice.accept(totalPrice);
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
    }
    private void removeCartItem(CartItem cartItem, int position){
        progress.show();
        BaseAPIService.createService(ICartService.class).removeAllProduct(cartItem.getId()).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if(response.isSuccessful() && response.body() != null){
                    cartItems.remove(cartItem);
                    notifyItemRemoved(position);
                    if(cartItems.size() == 0){
                        emptyCart.accept(View.VISIBLE);
                    }
                    double totalPrice = 0;
                    for (CartItem ci: response.body().getCartItems()) {
                        totalPrice += (ci.getProduct().getPrice() * ci.getCount());
                    }
                    updateTotalPrice.accept(totalPrice);
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
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
