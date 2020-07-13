package com.example.android.videojournal.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PermissionChecker extends AppCompatActivity {

    private static final String TAG = PermissionChecker.class.getSimpleName();
    Context context;
    Activity activity;

    public PermissionChecker(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public static boolean checkPermission(String permission, Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED) {
                return true;
            } else return false;
        }
        return true;
    }





}
