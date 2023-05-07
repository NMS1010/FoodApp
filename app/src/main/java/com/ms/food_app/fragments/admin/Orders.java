package com.ms.food_app.fragments.admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ms.food_app.R;
import com.ms.food_app.activities.IntroScreen;
import com.ms.food_app.adapters.OrderAdapter;
import com.ms.food_app.databinding.FragmentMyOrderBinding;
import com.ms.food_app.models.Order;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IOrderService;
import com.ms.food_app.utils.Constants;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Orders extends Fragment {

    private FragmentMyOrderBinding binding;
    private ProgressDialog progress;
    private List<Order> orderList;
    private OrderAdapter orderAdapter;
    public Orders() {
        // Required empty public constructor
    }

    public static Orders newInstance(String param1, String param2) {
        Orders fragment = new Orders();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(!SharedPrefManager.getInstance(getActivity()).isLoggedIn()){
            startActivity(new Intent(getActivity(), IntroScreen.class));
        }
        binding = FragmentMyOrderBinding.inflate(inflater, container, false);
        progress = LoadingUtil.setLoading(getActivity());
        binding.pendingBtnOrderList.setTextColor(getResources().getColor(R.color.blue2));
        setAdapter();
        loadOrderByStatus(Constants.NOT_PROCESSED);
        setEvents();
        return binding.getRoot();
    }
    private void setEvents(){
        binding.pendingBtnOrderList.setOnClickListener(view -> {
            resetBackground();
            setChosenBackground((Button)view);
            loadOrderByStatus(Constants.NOT_PROCESSED);
        });
        binding.processingBtnOrderList.setOnClickListener(view -> {
            resetBackground();
            setChosenBackground((Button)view);
            loadOrderByStatus(Constants.PROCESSING);
        });
        binding.shippingBtnOrderList.setOnClickListener(view -> {
            resetBackground();
            setChosenBackground((Button)view);
            loadOrderByStatus(Constants.SHIPPED);
        });
        binding.completedBtnOrderList.setOnClickListener(view -> {
            resetBackground();
            setChosenBackground((Button)view);
            loadOrderByStatus(Constants.DELIVERED);
        });
        binding.canceledBtnOrderList.setOnClickListener(view -> {
            resetBackground();
            setChosenBackground((Button)view);
            loadOrderByStatus(Constants.CANCELLED);
        });
    }
    private void resetBackground(){
        binding.pendingBtnOrderList.setTextColor(this.getResources().getColor(R.color.white));

        binding.canceledBtnOrderList.setTextColor(this.getResources().getColor(R.color.white));

        binding.completedBtnOrderList.setTextColor(this.getResources().getColor(R.color.white));

        binding.processingBtnOrderList.setTextColor(this.getResources().getColor(R.color.white));

        binding.shippingBtnOrderList.setTextColor(this.getResources().getColor(R.color.white));
    }
    private void setChosenBackground(Button button){
//        button.setBackgroundColor(R.color.white);
        button.setTextColor(this.getResources().getColor(R.color.blue2));
    }
    private void loadOrderByStatus(String status){
        progress.show();
        BaseAPIService.createService(IOrderService.class).getAllOrderByStatus(status).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if(response.isSuccessful() && response.body() != null){
                    orderList = response.body();
                    orderAdapter.updateOrders(orderList);
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });
    }
    private void setAdapter(){
        if(orderList == null)
            orderList  =new ArrayList<>();
        orderAdapter = new OrderAdapter(getActivity(), orderList);
        binding.listOrderRv.setAdapter(orderAdapter);
        binding.listOrderRv.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }
}