package com.example.android.videojournal.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.android.videojournal.R;
import com.example.android.videojournal.recyclerview.VideoAdapter;
import com.example.android.videojournal.recyclerview.VideoRecyclerView;
import com.example.android.videojournal.data.VideoDatabase;
import com.example.android.videojournal.data.VideoEntry;
import com.example.android.videojournal.notifications.NotificationUtils;
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

import static com.example.android.videojournal.utilities.Constants.READ_EXTERNAL_STORAGE_REQUEST_CODE;
import static com.example.android.videojournal.utilities.Constants.CAMERA_AND_AUDIO_REQUEST_CODE;

/**
 * Shows a feed of all regular (non-combined) videos recorded in the app
 * Videos autoplay on scrolling
 */
public class DailyVideoFeedActivity extends AppCompatActivity {

    private static final String TAG = DailyVideoFeedActivity.class.getSimpleName();

    private VideoDatabase mDb;
    private VideoRecyclerView mRecyclerView;

    /**
     * Initializes feed layout elements, db, action button for launching
     * video recording, notification channel, sets videos on recyclerview feed
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_feed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDb = VideoDatabase.getInstance(getApplicationContext());
        initActionButton();
        NotificationUtils.createNotificationChannel(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setItemViewCacheSize(20);

        if (PermissionChecker.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                DailyVideoFeedActivity.this)) {
            retrieveAndSetRegularVideos();
        } else {
            askPermission(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    getString(R.string.permiss_reason_storage), READ_EXTERNAL_STORAGE_REQUEST_CODE);
            if (PermissionChecker.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    DailyVideoFeedActivity.this)) retrieveAndSetRegularVideos();
        }
    }

    /**
     * On tap of "+" action button launches video recording implemented using Camera2 API
     * Checks permissions for Camera and Microphone
     */
    private void initActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PermissionChecker.checkPermission(Manifest.permission.CAMERA, DailyVideoFeedActivity.this)
                        && PermissionChecker.checkPermission(Manifest.permission.RECORD_AUDIO,
                        DailyVideoFeedActivity.this)) {

                    Intent takeVideoIntent = new Intent(DailyVideoFeedActivity.this,
                            RecordVideoActivity.class);
                    startActivity(takeVideoIntent);

                } else {
                    askPermission(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                            getString(R.string.permiss_reason_camera_audio),
                            CAMERA_AND_AUDIO_REQUEST_CODE);
                }
            }
        });
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
                Intent startSettingsActivity = new Intent(this,
                        SettingsActivity.class);
                startActivity(startSettingsActivity);
                return true;
            case R.id.button_weekly:
                Intent showWeeklyVideosIntent = new Intent(DailyVideoFeedActivity.this,
                        WeeklyVideoFeedActivity.class);
                startActivity(showWeeklyVideosIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void askPermission(String[] permissions, String reason, int requestCode) {
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
