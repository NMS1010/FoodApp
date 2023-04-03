package com.ms.food_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ms.food_app.R;
import com.ms.food_app.activities.AddressList;
import com.ms.food_app.activities.ChangePassword;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.databinding.FragmentSettingBinding;
import com.ms.food_app.utils.SharedPrefManager;

public class Setting extends Fragment {
    private FragmentSettingBinding binding;
    public Setting() {
        // Required empty public constructor
    }

    public static Setting newInstance(String param1, String param2) {
        return new Setting();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        setEvents();
        return binding.getRoot();
    }
    private void setEvents(){
        binding.addressBtnSetting.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), AddressList.class));
        });
        binding.changepassBtnSetting.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), ChangePassword.class));
        });
        binding.logoutBtnSetting.setOnClickListener(view -> {
            SharedPrefManager.getInstance(getContext()).logout();
            startActivity(new Intent(getActivity(), Signin.class));
        });
    }
}