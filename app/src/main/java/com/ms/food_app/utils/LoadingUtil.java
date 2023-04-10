package com.ms.food_app.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadingUtil {
    public static ProgressDialog setLoading(Context context){
        ProgressDialog progress = new ProgressDialog(context);
//        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        return progress;
    }
}
