package com.example.android.videojournal;

import android.app.Activity;
import android.os.Bundle;

// Based on sample code for Camera2 API
// https://android.googlesource.com/platform/development/+/abededd/samples/browseable/Camera2Video?autodive=0%2F

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