package com.ms.food_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.food_app.databinding.OrderDetailItemBinding;
import com.ms.food_app.databinding.OrderItemBinding;
import com.ms.food_app.models.Order;
import com.ms.food_app.models.OrderItem;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolders>{
    private Context context;
    private List<OrderItem> orderItems;
    public OrderDetailAdapter(Context context, List<OrderItem> orderItems){
        this.context = context;
        this.orderItems = orderItems;
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
        holder.binding.orderItemNameTvOrderItem.setText(orderItem.getProduct().getName() + " VND");
        holder.binding.orderItemQuantityTvOrderItem.setText(String.valueOf(orderItem.getCount()));
        holder.binding.orderItemUnitPriceTvOrderItem.setText(String.valueOf(orderItem.getProduct().getPrice()));
        holder.binding.orderItemTotalPriceTvOrderItem.setText(orderItem.getCount() * orderItem.getProduct().getPrice() + " VND");
        Glide.with(context)
                .load(orderItem.getProduct().getImages().get(0))
                .into(holder.binding.orderItemImgVOrderItem);
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public void updateOrderDetails(List<OrderItem> orderItems){
        this.orderItems = orderItems;
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
