package com.example.android.videojournal;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private final static int CAMERA_AND_AUDIO_REQUEST_CODE = 2;
    private static int REC_NOTIF_ID = 100;
    static final String CHANNEL_ID_REC_NOTIF = "record_reminder";
    private static int DEFAULT_NOTIF_TIME_MINS = 60;

    private VideoRecyclerView mRecyclerView;
    private AppDatabase mDb; // database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDb = AppDatabase.getInstance(getApplicationContext());
        Log.d(TAG, "onCreate() is runnning");

        FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (PermissionChecker.checkPermission(Manifest.permission.CAMERA, MainActivity.this) && PermissionChecker.checkPermission(Manifest.permission.RECORD_AUDIO, MainActivity.this)) {

                        Intent takeVideoIntent = new Intent(MainActivity.this, Camera2Activity.class);
                        startActivity(takeVideoIntent);

                    } else {
                        askPermission(new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                                "App needs to use camera and microphone to record videos",
                                CAMERA_AND_AUDIO_REQUEST_CODE);
                    }
                }
            });

        createNotificationChannel();
        setupSharedPreferences();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setItemViewCacheSize(20);
        if (PermissionChecker.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MainActivity.this)) {
            retrieveAndSetRegularVideos();
        } else {
            askPermission(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    "App needs permission to store videos", READ_EXTERNAL_STORAGE_REQUEST_CODE);
            if (PermissionChecker.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, MainActivity.this)) retrieveAndSetRegularVideos();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void retrieveAndSetRegularVideos() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        final LiveData<List<VideoEntry>> videoEntries = mDb.videoDao().loadAllNonCombinedVideos();

        videoEntries.observe(MainActivity.this, new Observer<List<VideoEntry>>() {

            @Override
            public void onChanged(@Nullable List<VideoEntry> entries) {
                Log.d(TAG, "Receiving database update for non-combined videos");
                mRecyclerView.setVideoEntries((ArrayList) entries);
                VideoAdapter videoAdapter = new VideoAdapter(MainActivity.this, (ArrayList) entries, false);
                mRecyclerView.setAdapter(videoAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu - add items to the action bar if it is present.
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
                Intent showWeeklyVideosIntent = new Intent(MainActivity.this, WeeklyActivity.class);
                startActivity(showWeeklyVideosIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void createNotificationChannel() {
        // create channel only on API 26+ otherwise not in support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_REC_NOTIF, name, importance);
            channel.setDescription(description);
            //register channel with system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setUpReminderNotification(Context context, int hour, int minute, Class receiver) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Toast.makeText(context, "in Main Activity: Time set at hours= " + hour + " and min= " + minute, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, receiver);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REC_NOTIF_ID, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // activate checkmark status changed
        if (key.equals(getString(R.string.pref_activate_reminder_key))) {
            // was checked - turn on notification
            if (sharedPreferences.getBoolean(key, getResources().getBoolean(R.bool.pref_activate_reminder_default))) {
                int minutesAfterMidnight = sharedPreferences.getInt(getString(R.string.pref_reminder_time_key), DEFAULT_NOTIF_TIME_MINS);
                // get hours and mins from savedTime
                int hours = minutesAfterMidnight / 60;
                int minutes = minutesAfterMidnight % 60;
                setUpReminderNotification(MainActivity.this, hours, minutes, AlarmReceiver.class);
            } else { // was unchecked - turn off notifications
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, REC_NOTIF_ID, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean reminderActive = sharedPreferences.getBoolean(getString(R.string.pref_activate_reminder_key),
                getResources().getBoolean(R.bool.pref_activate_reminder_default));
        int minutesAfterMidnight = sharedPreferences.getInt(getString(R.string.pref_reminder_time_key), DEFAULT_NOTIF_TIME_MINS);
        // get hours and mins from savedTime
        int hours = minutesAfterMidnight / 60;
        int minutes = minutesAfterMidnight % 60;
        if (reminderActive) {
            setUpReminderNotification(MainActivity.this, hours, minutes, AlarmReceiver.class);
        }

        // register listener for preference changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void askPermission(String[] permissions, String reason, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_LONG).show();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        if (mRecyclerView != null) {
            mRecyclerView.releasePlayer();
        }
    }
}
