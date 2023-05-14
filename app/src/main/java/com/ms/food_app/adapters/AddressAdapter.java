package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ms.food_app.R;
import com.ms.food_app.activities.AddressDetail;
import com.ms.food_app.databinding.AddressItemBinding;
import com.ms.food_app.models.Address;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IUserService;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder>{
    private Context context;
    private List<Address> addresses;
    private ProgressDialog progress;

    public AddressAdapter(Context context, List<Address> addresses) {
        this.context = context;
        this.addresses = addresses;
        progress = LoadingUtil.setLoading(context);
    }
    @NonNull
    @Override
    public AddressAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AddressItemBinding binding = AddressItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AddressAdapter.AddressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.AddressViewHolder holder, int position) {
        Address addressItem = addresses.get(position);
        holder.binding.addressItemFullNameTvAddress.setText(addressItem.getUsername());
        holder.binding.addressItemPhoneTvAddress.setText(addressItem.getPhone());
        holder.binding.addressItemStreetTvAddress.setText(addressItem.getStreet());
        String txt = addressItem.getWard() + " " + addressItem.getDistrict() + " " + addressItem.getProvince();
        holder.binding.addressItemWDPTvAddress.setText(txt);
        holder.binding.addressItemDeleteBtnAddress.setOnClickListener(view -> {
            if(addressItem.getStatus()){
                ToastUtil.showToast(((Activity) context).findViewById(R.id.listAddress),"Cannot remove default address", false);
                return;
            }
            progress.show();
            BaseAPIService.createService(IUserService.class).deleteAddressById(addressItem.getId()).enqueue(new Callback<List<Address>>() {
                @Override
                public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        addresses.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        ToastUtil.showToast(((Activity) context).findViewById(R.id.listAddress),"Remove successfully", true);
                    }
                    progress.dismiss();
                }

                @Override
                public void onFailure(Call<List<Address>> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    progress.dismiss();
                }
            });
        });
        holder.binding.addressItemEditBtnAddress.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddressDetail.class);
            intent.putExtra("New", (new Gson()).toJson(addressItem));
            context.startActivity(intent);
        });
    }
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateAddress(List<Address> addresses){
        this.addresses = addresses;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        private AddressItemBinding binding;

        public AddressViewHolder(@NonNull AddressItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
