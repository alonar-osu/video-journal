package com.alonar.android.videojournal.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * For checking permissions at runtime
 */
public class PermissionChecker extends AppCompatActivity {

    public PermissionChecker() { }

    /**
     * Checks if given permission was granted on Android M or above
      * @param permission the String of the permission to check
     * @return true if granted, false if not granted or if SDK version is below Android M
     */
    public static boolean checkPermission(String permission, Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

}
