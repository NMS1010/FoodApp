package com.ms.food_app.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ms.food_app.R;
import com.ms.food_app.activities.Signin;
import com.ms.food_app.activities.UpdateProfile;
import com.ms.food_app.databinding.FragmentProfileBinding;
import com.ms.food_app.models.User;
import com.ms.food_app.utils.SharedPrefManager;

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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        loadProfile();
        setEvents();
        return binding.getRoot();
    }
    private void setEvents(){
        binding.updateBtnUpdateProfile.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), UpdateProfile.class));
        });
    }
    private void loadProfile(){
        if(!SharedPrefManager.getInstance(getContext()).isLoggedIn()){
            startActivity(new Intent(getActivity(), Signin.class));
        }
        User user = SharedPrefManager.getInstance(getContext()).getUser();
        binding.firstNameTvProfile.setText(user.getFirstname());
        binding.lastNameTvProfile.setText(user.getLastname());
        binding.emailTvProfile.setText(user.getEmail());
        binding.phoneTvProfile.setText(user.getPhone());
        binding.birthdayTvProfile.setText(user.getBirthday());
        binding.genderTvProfile.setText(user.getGender());
        Glide.with(this)
                .load(user.getAvatar())
                .into(binding.avatarImgVProfile);
    }
}