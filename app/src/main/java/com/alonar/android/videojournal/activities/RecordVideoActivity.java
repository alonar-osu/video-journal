package com.alonar.android.videojournal.activities;

import android.app.Activity;
import android.os.Bundle;

import com.alonar.android.videojournal.R;
import com.alonar.android.videojournal.fragments.RecordVideoFragment;

/**
 * Camera activity for recording videos using camera
 * Based on sample code for Camera2 API
 * https://android.googlesource.com/platform/development/+/abededd/samples/browseable/Camera2Video?autodive=0%2F
 */

public class RecordVideoActivity extends Activity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, RecordVideoFragment.newInstance())
                    .commit();
        }
    }
}