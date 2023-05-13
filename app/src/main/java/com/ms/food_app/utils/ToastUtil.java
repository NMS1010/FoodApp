package com.ms.food_app.utils;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.ms.food_app.R;

public class ToastUtil {
    public static void showToast(View view, String message, boolean isSuccess) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("DISMISS", v -> snackbar.dismiss());
        int color = ContextUtil.context.getResources().getColor(R.color.colorPrimary);
        if(isSuccess)
            color = ContextUtil.context.getResources().getColor(R.color.fbutton_color_turquoise);
        snackbar.setBackgroundTint(color);
        snackbar.show();
    }
}
