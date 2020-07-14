package com.example.android.videojournal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.videojournal.data.AppDatabase;
import com.example.android.videojournal.data.VideoEntry;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;


public class WeeklyVideoFeedActivity extends AppCompatActivity {

    private static final String TAG = WeeklyVideoFeedActivity.class.getSimpleName();

    private VideoRecyclerView mRecyclerView;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_feed);
        mDb = AppDatabase.getInstance(getApplicationContext());

        Log.d(TAG, "WeeklyVideoFeedActivity onCreate() is runnning");
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

        videoEntries.observe(WeeklyVideoFeedActivity.this, new Observer<List<VideoEntry>>() {

            @Override
            public void onChanged(@Nullable List<VideoEntry> entries) {
                Log.d(TAG, "Receiving database update for weekly videos");
                mRecyclerView.setVideoEntries((ArrayList) entries);
                VideoAdapter videoAdapter = new VideoAdapter(WeeklyVideoFeedActivity.this, (ArrayList) entries, true);
                mRecyclerView.setAdapter(videoAdapter);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                goToMainActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToMainActivity() {
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
