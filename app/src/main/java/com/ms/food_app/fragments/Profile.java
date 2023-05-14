package com.ms.food_app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ms.food_app.R;
import com.ms.food_app.activities.AddressList;
import com.ms.food_app.activities.ChangePassword;
import com.ms.food_app.activities.IntroScreen;
import com.ms.food_app.activities.MyReview;
import com.ms.food_app.activities.SaveList;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.activities.UpdateProfile;
import com.ms.food_app.activities.admin.ProductStatus;
import com.ms.food_app.databinding.FragmentProfileBinding;
import com.ms.food_app.models.User;
import com.ms.food_app.utils.SharedPrefManager;

import java.util.Objects;

public class Profile extends Fragment {

    private FragmentProfileBinding binding;
    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        return new Profile();
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.changePasswordLayout.setVisibility(View.GONE);
        loadProfile();
        setEvents();
        return binding.getRoot();
    }
    private void setEvents(){
        binding.updateProfile.setOnClickListener(view -> {

            startActivity(new Intent(getActivity(), UpdateProfile.class));
        });
        binding.Address.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), AddressList.class));
        });
        binding.Favorite.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), SaveList.class));
        });
        binding.logout.setOnClickListener(view -> {
            SharedPrefManager.getInstance(getContext()).logout();
            startActivity(new Intent(getActivity(), IntroScreen.class));
        });
        binding.changePasswordLayout.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), ChangePassword.class));
        });
        binding.myReview.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), MyReview.class));
        });
    }
    private void loadProfile(){
        if(!SharedPrefManager.getInstance(getContext()).isLoggedIn()){
            SharedPrefManager.getInstance(getContext()).logout();
            startActivity(new Intent(getActivity(), IntroScreen.class));
        }
        if(SharedPrefManager.getInstance(getActivity()).isLoggedIn()){
            if(SharedPrefManager.getInstance(getActivity()).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN"))){
                binding.Favorite.setVisibility(View.GONE);
                binding.Address.setVisibility(View.GONE);
                binding.myReview.setVisibility(View.GONE);
            }

        }
        User user = SharedPrefManager.getInstance(getContext()).getUser();
        binding.nameUser.setText(user.getFirstname() + " " + user.getLastname());
        binding.emailUser.setText(user.getEmail());
        Context ctx = getActivity();
        if(ctx != null)
            Glide.with(ctx)
                .load(user.getAvatar())
                .into(binding.imageUser);
    }
}