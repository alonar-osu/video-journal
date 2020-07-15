package com.example.android.videojournal;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.android.videojournal.data.VideoDatabase;
import com.example.android.videojournal.data.VideoEntry;
import com.example.android.videojournal.utilities.NotificationUtils;
import com.example.android.videojournal.utilities.PermissionChecker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class DailyVideoFeedActivity extends AppCompatActivity {

    private static final String TAG = DailyVideoFeedActivity.class.getSimpleName();
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private static final int CAMERA_AND_AUDIO_REQUEST_CODE = 2;
    private VideoRecyclerView mRecyclerView;
    private VideoDatabase mDb; // database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_feed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDb = VideoDatabase.getInstance(getApplicationContext());
        Log.d(TAG, "onCreate() is runnning");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PermissionChecker.checkPermission(Manifest.permission.CAMERA, DailyVideoFeedActivity.this)
                        && PermissionChecker.checkPermission(Manifest.permission.RECORD_AUDIO,
                        DailyVideoFeedActivity.this)) {

                    Intent takeVideoIntent = new Intent(DailyVideoFeedActivity.this, RecordVideoActivity.class);
                    startActivity(takeVideoIntent);

                } else {
                    askPermission(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                            "App needs to use camera and microphone to record videos",
                            CAMERA_AND_AUDIO_REQUEST_CODE);
                }
            }
        });

        NotificationUtils.createNotificationChannel(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setItemViewCacheSize(20);

        if (PermissionChecker.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, DailyVideoFeedActivity.this)) {
            retrieveAndSetRegularVideos();
        } else {
            askPermission(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    "App needs permission to store videos", READ_EXTERNAL_STORAGE_REQUEST_CODE);
            if (PermissionChecker.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, DailyVideoFeedActivity.this)) retrieveAndSetRegularVideos();
        }
    }

    private void retrieveAndSetRegularVideos() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        final LiveData<List<VideoEntry>> videoEntries = mDb.videoDao().loadAllNonCombinedVideos();

        videoEntries.observe(DailyVideoFeedActivity.this, new Observer<List<VideoEntry>>() {

            @Override
            public void onChanged(@Nullable List<VideoEntry> entries) {
                Log.d(TAG, "Receiving database update for non-combined videos");
                mRecyclerView.setVideoEntries((ArrayList) entries);
                VideoAdapter videoAdapter = new VideoAdapter(DailyVideoFeedActivity.this,
                        (ArrayList) entries, false);
                mRecyclerView.setAdapter(videoAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.button_weekly:
                Intent showWeeklyVideosIntent = new Intent(DailyVideoFeedActivity.this, WeeklyVideoFeedActivity.class);
                startActivity(showWeeklyVideosIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void askPermission(String[] permissions, String reason, int requestCode) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG).show();
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
    }
}
