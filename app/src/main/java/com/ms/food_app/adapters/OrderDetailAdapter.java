package com.ms.food_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ms.food_app.activities.Feedback;
import com.ms.food_app.databinding.OrderDetailItemBinding;
import com.ms.food_app.databinding.OrderItemBinding;
import com.ms.food_app.models.Order;
import com.ms.food_app.models.OrderItem;
import com.ms.food_app.utils.Constants;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.List;
import java.util.Objects;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolders>{
    private Context context;
    private List<OrderItem> orderItems;
    private Order order;
    public OrderDetailAdapter(Context context, List<OrderItem> orderItems){
        this.context = context;
        this.orderItems = orderItems;
        order = null;
    }
    @NonNull
    @Override
    public OrderDetailViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderDetailItemBinding binding = OrderDetailItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderDetailViewHolders(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolders holder, int position) {
        OrderItem orderItem = orderItems.get(position);
        holder.binding.orderItemNameTvOrderItem.setText(orderItem.getProduct().getName());
        holder.binding.orderItemQuantityTvOrderItem.setText(String.valueOf(orderItem.getCount()));
        holder.binding.orderItemUnitPriceTvOrderItem.setText(orderItem.getProduct().getPrice() + " VND");
        holder.binding.orderItemTotalPriceTvOrderItem.setText(orderItem.getCount() * orderItem.getProduct().getPrice() + " VND");
        Glide.with(context)
                .load(orderItem.getProduct().getImages().get(0))
                .into(holder.binding.orderItemImgVOrderItem);
        if(order != null && !Objects.equals(order.getStatus(), Constants.DELIVERED) || orderItem.getRating()
        || SharedPrefManager.getInstance(context).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN"))){
            holder.binding.reviewBtn.setVisibility(View.GONE);

        }else{
            holder.binding.reviewBtn.setVisibility(View.VISIBLE);
        }

        holder.binding.reviewBtn.setOnClickListener(view -> {
            Intent intent = new Intent(context, Feedback.class);
            intent.putExtra("orderId", order.getId());
            intent.putExtra("product", new Gson().toJson(orderItem.getProduct()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void updateOrderDetails(List<OrderItem> orderItems, Order order){
        this.orderItems = orderItems;
        this.order = order;
        notifyDataSetChanged();

    }

    public class OrderDetailViewHolders extends RecyclerView.ViewHolder {
        private OrderDetailItemBinding binding;
        public OrderDetailViewHolders(@NonNull OrderDetailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
