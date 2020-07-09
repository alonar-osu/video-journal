package com.example.android.videojournal;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WeeklyActivity extends AppCompatActivity {

    private static final String TAG = WeeklyActivity.class.getSimpleName();

    private VideoRecyclerView mRecyclerView;
    private AppDatabase mDb; // database using Room

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);
     //   Toolbar toolbar = findViewById(R.id.toolbar_weekly);
      //  setSupportActionBar(toolbar);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Toast.makeText(getApplicationContext(), "WeeklyActivity onCreate() is runnning", Toast.LENGTH_SHORT).show();

        mRecyclerView = findViewById(R.id.recyclerView_weekly);
        mRecyclerView.setItemViewCacheSize(20);
        retrieveAndSetWeeklyVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void retrieveAndSetWeeklyVideos() {

    //

    }






}
