package com.ms.food_app.utils;

import android.content.Context;

public class ToastUtil {
    public static void showToast(Context context, String message) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
