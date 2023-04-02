package com.ms.food_app.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.ms.food_app.R;
import com.ms.food_app.databinding.ActivityUpdateProfileBinding;
import com.ms.food_app.databinding.FragmentProfileBinding;
import com.ms.food_app.fragments.Home;
import com.ms.food_app.fragments.Profile;
import com.ms.food_app.models.User;
import com.ms.food_app.utils.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class UpdateProfile extends AppCompatActivity {

    ActivityUpdateProfileBinding binding;
    public static final  int MY_REQUEST_CODE = 100;
    public static  final String TAG = UpdateProfile.class.getName();
    private Uri mUri;
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;
    private ArrayAdapter<CharSequence> adapter;
    public static  String[] storge_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public  static String [] getStorge_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };
    public  static  String [] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = getStorge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else  {
            requestPermissions(permissions(), MY_REQUEST_CODE);
        }
    }
    @Override
    public  void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            binding.avatarImgVProfile.setImageBitmap(bitmap);

                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = ArrayAdapter.createFromResource(this, R.array.genderItem, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.genderSpinUpdateProfile.setAdapter(adapter);
        setEvents();
        loadUser();
        final Calendar c = Calendar.getInstance();
        this.lastSelectedYear = c.get(Calendar.YEAR);
        this.lastSelectedMonth = c.get(Calendar.MONTH);
        this.lastSelectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
    }
    private void loadUser(){
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, Signin.class));
        }
        User user = SharedPrefManager.getInstance(this).getUser();
        binding.firstNameTvUpdateProfile.setText(user.getFirstname());
        binding.lastNameTvUpdateProfile.setText(user.getLastname());
        binding.emailTvUpdateProfile.setText(user.getEmail());
        binding.phoneTvUpdateProfile.setText(user.getPhone());
        binding.birthdayTvUpdateProfile.setText(user.getBirthday());
        if(!Objects.equals(user.getGender(), "")) {
            int spinnerPosition = adapter.getPosition(user.getGender());
            binding.genderSpinUpdateProfile.setSelection(spinnerPosition);
        }
        Glide.with(this)
                .load(user.getAvatar())
                .into(binding.avatarImgVProfile);
    }
    private void selectDate(){
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
            binding.birthdayTvUpdateProfile.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

            lastSelectedYear = year;
            lastSelectedMonth = monthOfYear;
            lastSelectedDayOfMonth = dayOfMonth;
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                dateSetListener, lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth);
        datePickerDialog.show();
    }
    private void setEvents(){
        binding.btnBackUpdateProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            intent.putExtra("Check", "Profile");
            startActivity(intent);
        });
        binding.layoutImage.setOnClickListener(view -> {
            CheckPermission();
        });
        binding.selectdateUpdateProfile.setOnClickListener(view -> {
            selectDate();
        });
    }

}