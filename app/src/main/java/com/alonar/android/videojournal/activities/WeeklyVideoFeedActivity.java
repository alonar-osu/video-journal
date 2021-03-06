package com.alonar.android.videojournal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.alonar.android.videojournal.R;
import com.alonar.android.videojournal.recyclerview.VideoAdapter;
import com.alonar.android.videojournal.recyclerview.VideoRecyclerView;
import com.alonar.android.videojournal.data.VideoDatabase;
import com.alonar.android.videojournal.data.VideoEntry;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Shows a feed of all combined weekly videos
 * Videos autoplay on scrolling
 */
public class WeeklyVideoFeedActivity extends AppCompatActivity {

    private static final String TAG = WeeklyVideoFeedActivity.class.getSimpleName();

    private VideoDatabase mDb;
    private VideoRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "WeeklyVideoFeedActivity onCreate() is running");

        setContentView(R.layout.activity_weekly_feed);
        mDb = VideoDatabase.getInstance(getApplicationContext());
        prepareRecyclerView();
        retrieveAndSetWeeklyVideos();
    }

    /**
     * Gets all combined videos from db via LiveData and sets them to recyclerview
     * for the feed
     */
    private void retrieveAndSetWeeklyVideos() {

        final LiveData<List<VideoEntry>> videoEntries = mDb.videoDao().loadAllCombinedVideos();

        videoEntries.observe(WeeklyVideoFeedActivity.this, new Observer<List<VideoEntry>>() {

            @Override
            public void onChanged(@Nullable List<VideoEntry> entries) {
                Log.d(TAG, "Receiving database update for weekly videos");
                mRecyclerView.setVideoEntries((ArrayList) entries);
                VideoAdapter videoAdapter = new VideoAdapter(WeeklyVideoFeedActivity.this,
                        (ArrayList) entries, true);
                mRecyclerView.setAdapter(videoAdapter);
            }
        });
    }

    private void prepareRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView_weekly);
        mRecyclerView.setItemViewCacheSize(20);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goToHomeActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(this, DailyVideoFeedActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
    }

}
