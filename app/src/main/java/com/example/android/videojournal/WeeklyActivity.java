package com.example.android.videojournal;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;


public class WeeklyActivity extends AppCompatActivity {

    private static final String TAG = WeeklyActivity.class.getSimpleName();

    private VideoRecyclerView mRecyclerView;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
        mDb = AppDatabase.getInstance(getApplicationContext());

        Log.d(TAG, "WeeklyActivity onCreate() is runnning");
        mRecyclerView = findViewById(R.id.recyclerView_weekly);
        mRecyclerView.setItemViewCacheSize(20);
        retrieveAndSetWeeklyVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void retrieveAndSetWeeklyVideos() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        final LiveData<List<VideoEntry>> videoEntries = mDb.videoDao().loadAllCombinedVideos();

        videoEntries.observe(WeeklyActivity.this, new Observer<List<VideoEntry>>() {

            @Override
            public void onChanged(@Nullable List<VideoEntry> entries) {
                Log.d(TAG, "Receiving database update for weekly videos");
                mRecyclerView.setVideoEntries((ArrayList) entries);
                VideoAdapter videoAdapter = new VideoAdapter(WeeklyActivity.this, (ArrayList) entries, true);
                mRecyclerView.setAdapter(videoAdapter);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
    }




}
