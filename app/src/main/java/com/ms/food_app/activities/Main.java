package com.ms.food_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.navigation.NavigationBarView;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityMainBinding;
import com.ms.food_app.fragments.Home;
import com.ms.food_app.fragments.MyOrder;
import com.ms.food_app.fragments.Profile;
import com.ms.food_app.utils.ContextUtil;
import com.ms.food_app.utils.ToastUtil;

public class Main extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        ContextUtil.context = getApplicationContext();
        ToastUtil.showToast(binding.getRoot(),"Logging successfully", true);
        initialFragment();
        setEvents();
    }
    private void setEvents(){
        binding.bottomNavigation.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private void initialFragment(){

        Intent intent = getIntent();
        String s = intent.getStringExtra("Check");
        if(s == null) {
            loadFragment(new Home());
            return;
        }
        switch (s){
            case "Profile":
                loadFragment(new Profile());
                binding.bottomNavigation.setSelectedItemId(R.id.profile_page);
                break;
            case "Order":
                loadFragment(new MyOrder());
                binding.bottomNavigation.setSelectedItemId(R.id.order_page);
                break;
            default:
                loadFragment(new Home());
                binding.bottomNavigation.setSelectedItemId(R.id.home_page);
                break;
        }
    }
    @SuppressLint("NonConstantResourceId")
    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.home_page:
                        fragment = new Home();
                        loadFragment(fragment);
                        return true;
                    case R.id.order_page:
                        fragment = new MyOrder();
                        loadFragment(fragment);
                        return true;
                    case R.id.profile_page:
                        fragment = new Profile();
                        loadFragment(fragment);
                        return true;
                }

                return false;
            };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}