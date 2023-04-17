package com.ms.food_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.food_app.activities.OrderDetail;
import com.ms.food_app.databinding.OrderItemBinding;
import com.ms.food_app.models.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolders>{
    private Context context;
    private List<Order> orderList;
    public OrderAdapter(Context context, List<Order> orders){
        this.context = context;
        this.orderList = orders;
    }
    @NonNull
    @Override
    public OrderViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderItemBinding binding = OrderItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderAdapter.OrderViewHolders(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolders holder, int position) {
        Order order = orderList.get(position);
        holder.binding.nameFood.setText(order.getCreatedAt().toLocaleString());
        holder.binding.foodPrice.setText(order.getAmountFromUser() + " VND");
        holder.binding.itemCount.setText(order.getOrderItems().size() + " items");
        holder.binding.statusReviewTV.setText(order.getStatus());
        if(order.getOrderItems().size() > 0)
            Glide.with(context)
                    .load(order.getOrderItems().get(0).getProduct().getImages().get(0))
                    .into(holder.binding.imageFood);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, OrderDetail.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
        holder.binding.btnReview.setOnClickListener(view -> {
            Intent intent = new Intent(context, OrderDetail.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrders(List<Order> orders){
        this.orderList = orders;
        notifyDataSetChanged();
    }

    public class OrderViewHolders extends RecyclerView.ViewHolder {
        private OrderItemBinding binding;
        public OrderViewHolders(@NonNull OrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
