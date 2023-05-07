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
import android.app.ProgressDialog;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ms.food_app.R;
import com.ms.food_app.activities.admin.AdminMain;
import com.ms.food_app.databinding.ActivityUpdateProfileBinding;
import com.ms.food_app.databinding.FragmentProfileBinding;
import com.ms.food_app.fragments.Home;
import com.ms.food_app.fragments.Profile;
import com.ms.food_app.models.User;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IUserService;
import com.ms.food_app.utils.DatePickerUtil;
import com.ms.food_app.utils.LoadingUtil;
import com.ms.food_app.utils.RealPathUtil;
import com.ms.food_app.utils.SharedPrefManager;
import com.ms.food_app.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ActivityUpdateProfileBinding binding;
    public static final  int MY_REQUEST_CODE = 100;
    public static  final String TAG = UpdateProfile.class.getName();
    private Uri mUri;
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;
    private ArrayAdapter<CharSequence> adapter;
    private ProgressDialog progress;
    private User user;
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
        progress = LoadingUtil.setLoading(this);
        adapter = ArrayAdapter.createFromResource(this, R.array.genderItem, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.genderSpinUpdateProfile.setAdapter(adapter);
        setEvents();
        final Calendar c = Calendar.getInstance();
        this.lastSelectedYear = c.get(Calendar.YEAR);
        this.lastSelectedMonth = c.get(Calendar.MONTH);
        this.lastSelectedDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        try {
            loadUser();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadUser() throws ParseException {
        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(this, IntroScreen.class));
        }
        user = SharedPrefManager.getInstance(this).getUser();
        binding.firstNameTvUpdateProfile.setText(user.getFirstname());
        binding.lastNameTvUpdateProfile.setText(user.getLastname());
        binding.emailTvUpdateProfile.setText(user.getEmail());
        binding.emailTvUpdateProfile.setEnabled(false);
        binding.phoneTvUpdateProfile.setText(user.getPhone());
        if(user.getBirthday() != null){
            binding.birthdayTvUpdateProfile.setText(user.getBirthday());
        }
        if(!Objects.equals(user.getGender(), "")) {
            int spinnerPosition = adapter.getPosition(user.getGender());
            binding.genderSpinUpdateProfile.setSelection(spinnerPosition);
        }
        Glide.with(this)
                .load(user.getAvatar())
                .into(binding.avatarImgVProfile);
    }
    private Boolean isValidated() {
        if (binding.emailTvUpdateProfile.getText().toString().trim().isEmpty()) {
            binding.emailTvUpdateProfile.setError("Enter your email");
            return false;
        }
        if (binding.firstNameTvUpdateProfile.getText().toString().trim().isEmpty()) {
            binding.firstNameTvUpdateProfile.setError("Enter your first name");
            return false;
        }
        if (binding.lastNameTvUpdateProfile.getText().toString().trim().isEmpty()) {
            binding.lastNameTvUpdateProfile.setError("Enter your last name");
            return false;
        }
        if (binding.phoneTvUpdateProfile.getText().toString().trim().isEmpty()) {
            binding.phoneTvUpdateProfile.setError("Enter your phone");
            return false;
        }
        return true;
    }
    private void updateUser(){

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            SharedPrefManager.getInstance(this).logout();
            startActivity(new Intent(this, IntroScreen.class));
        }
        user.setEmail(binding.emailTvUpdateProfile.getText().toString());
        user.setPhone(binding.phoneTvUpdateProfile.getText().toString());
        user.setFirstname(binding.firstNameTvUpdateProfile.getText().toString());
        user.setLastname(binding.lastNameTvUpdateProfile.getText().toString());
        user.setGender(binding.genderSpinUpdateProfile.getSelectedItem().toString());
        user.setBirthday(binding.birthdayTvUpdateProfile.getText().toString());
        MultipartBody.Part avatar = null;
        if(mUri != null){
            String IMAGE_PATH = RealPathUtil.getRealPath(this, mUri);
            File file = new File(IMAGE_PATH);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            avatar = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        }
        progress.show();
        String request = new Gson().toJson(user);
        JsonParser parser = new JsonParser();
        if(avatar == null){
            BaseAPIService
                    .createService(IUserService.class)
                    .updateProfile((JsonObject)parser.parse(request))
                    .enqueue(handleUpdate());
        }else{
            BaseAPIService
                    .createService(IUserService.class)
                    .updateProfile((JsonObject)parser.parse(request), avatar)
                    .enqueue(handleUpdate());
        }
    }
    private Callback<User> handleUpdate(){
        return new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null){
                    User user = response.body();
                    SharedPrefManager.getInstance(getApplicationContext()).saveUser(user);
                    Intent intent = new Intent(getApplicationContext(), Main.class);
                    if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
                        if(SharedPrefManager.getInstance(getApplicationContext()).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN"))){
                            intent = new Intent(getApplicationContext(), AdminMain.class);
                        }

                    }
                    intent.putExtra("Check", "Profile");
                    startActivity(intent);
                }else{
                    ToastUtil.showToast(getApplicationContext(), "Failed to update profile");
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        };
    }
    private void setEvents(){
        binding.backBtnUpdateProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            if(SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()){
                if(SharedPrefManager.getInstance(getApplicationContext()).getUser().getRoles().stream().anyMatch(x -> x.contains("ADMIN"))){
                    intent = new Intent(getApplicationContext(), AdminMain.class);
                }

            }
            intent.putExtra("Check", "Profile");
            startActivity(intent);
        });
        binding.layoutImage.setOnClickListener(view -> {
            CheckPermission();
        });
        binding.selectdateUpdateProfile.setOnClickListener(view -> {
            DatePickerUtil mDatePickerDialogFragment = new DatePickerUtil();
            mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
        });
        binding.updateBtnUpdateProfile.setOnClickListener(view -> {
            if(isValidated() ){
                updateUser();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, i);
        mCalendar.set(Calendar.MONTH, i1);
        mCalendar.set(Calendar.DAY_OF_MONTH, i2);
        lastSelectedYear = mCalendar.get(Calendar.YEAR);
        lastSelectedMonth = mCalendar.get(Calendar.MONTH);
        lastSelectedDayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        String selectedDate = lastSelectedYear + "-" + lastSelectedMonth + "-" + lastSelectedDayOfMonth;
        binding.birthdayTvUpdateProfile.setText(selectedDate);
    }
}