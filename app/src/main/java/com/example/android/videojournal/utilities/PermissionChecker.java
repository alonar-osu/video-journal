package com.example.android.videojournal.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * For checking permissions at runtime
 */
public class PermissionChecker extends AppCompatActivity {

    private static final String TAG = PermissionChecker.class.getSimpleName();

    Context mContext;
    Activity mActivity;

    public PermissionChecker(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    /**
     * Checks if given permission was granted on Android M or above
      * @param permission the String of the permission to check
     * @return true if granted, false if not granted or if SDK version is below Android M
     */
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
