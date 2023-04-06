package com.ms.food_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ms.food_app.R;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.utils.SharedPrefManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyOrder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyOrder extends Fragment {


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
            startActivity(new Intent(getActivity(), Signin.class));
        }
        return inflater.inflate(R.layout.fragment_my_order, container, false);
    }
}