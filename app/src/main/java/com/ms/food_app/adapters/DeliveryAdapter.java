package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ms.food_app.databinding.CategoryItemBinding;
import com.ms.food_app.databinding.DeliveryItemBinding;
import com.ms.food_app.models.Category;
import com.ms.food_app.models.Delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolders> {
    private List<Delivery> deliveryList;
    int selectedPosition = 0;
    private Context context;
    Consumer<Double> updateShippingPrice;
    Consumer<Delivery> chooseDelivery;

    public DeliveryAdapter(Context context, List<Delivery> deliveryList, Consumer<Double> updateShippingPrice, Consumer<Delivery> chooseDelivery) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.updateShippingPrice = updateShippingPrice;
        this.chooseDelivery = chooseDelivery;
    }

    @NonNull
    @Override
    public DeliveryViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeliveryItemBinding binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DeliveryAdapter.DeliveryViewHolders(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolders holder, int position) {
        Delivery delivery = deliveryList.get(position);
        holder.binding.deliveryItem.setText(delivery.getName() + " | " + delivery.getPrice());
        holder.binding.deliveryItem.setChecked(position == selectedPosition);
        if (holder.binding.deliveryItem.isChecked()) {
            updateShippingPrice.accept(delivery.getPrice());
            chooseDelivery.accept(delivery);
        }
        holder.binding.deliveryItem.setOnClickListener(view -> {
            selectedPosition = holder.getAdapterPosition();
            notifyItemRangeChanged(0, deliveryList.size());
            updateShippingPrice.accept(delivery.getPrice());
            chooseDelivery.accept(delivery);
        });
    }

    public void updateDeliveries(List<Delivery> deliveryList) {
        this.deliveryList = deliveryList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public class DeliveryViewHolders extends RecyclerView.ViewHolder {
        private DeliveryItemBinding binding;

        public DeliveryViewHolders(@NonNull DeliveryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
