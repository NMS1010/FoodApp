package com.ms.food_app.fragments;

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

import com.ms.food_app.R;
import com.ms.food_app.activities.IntroScreen;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.adapters.OrderAdapter;
import com.ms.food_app.databinding.FragmentHomeBinding;
import com.ms.food_app.databinding.FragmentMyOrderBinding;
import com.ms.food_app.databinding.FragmentProfileBinding;
import com.ms.food_app.models.Order;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IOrderService;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrder extends Fragment {

    private FragmentMyOrderBinding binding;
    private ProgressDialog progress;
    private List<Order> orderList;
    private OrderAdapter orderAdapter;
    public MyOrder() {
        // Required empty public constructor
    }

    public static MyOrder newInstance(String param1, String param2) {
        MyOrder fragment = new MyOrder();
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
        setAdapter();
        loadOrders();
        return binding.getRoot();
    }

    private void setAdapter(){
        if(orderList == null)
            orderList  =new ArrayList<>();
        orderAdapter = new OrderAdapter(getActivity(), orderList);
        binding.listOrderRv.setAdapter(orderAdapter);
        binding.listOrderRv.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }
    private void loadOrders(){
        User user = SharedPrefManager.getInstance(getActivity()).getUser();
        progress.show();
        BaseAPIService
                .createService(IOrderService.class)
                .getOrderByUser(user.getId())
                .enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if(response.isSuccessful() && response.body() != null){
                    orderAdapter.updateOrders(response.body());
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
}