package com.example.android.videojournal;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.coremedia.iso.boxes.Container;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

// TESTING
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String THUMBNAIL_DIRECTORY_NAME = "thumbnails";
    private final static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1;

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static int REC_NOTIF_ID = 100;
    static final String CHANNEL_ID_REC_NOTIF = "record_reminder";
    private static int DEFAULT_NOTIF_TIME_MINS = 60;

    private VideoRecyclerView mRecyclerView;
    private AppDatabase mDb; // database using Room

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Toast.makeText(getApplicationContext(), "onCreate() is runnning", Toast.LENGTH_LONG).show();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on click action
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                takeVideoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                takeVideoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);

                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        createNotificationChannel();
        setupSharedPreferences();

        // check permissions for thumbnails
        if (checkReadExternalStoragePermission()) {
            mRecyclerView = findViewById(R.id.recyclerView);
            mRecyclerView.setItemViewCacheSize(20);

            retrieveVideos();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void retrieveVideos() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        final LiveData<List<VideoEntry>> videoEntries = mDb.videoDao().loadAllVideos();
        videoEntries.observe(MainActivity.this, new Observer<List<VideoEntry>>() {

            @Override
            public void onChanged(@Nullable List<VideoEntry> entries) {
                Log.d(TAG, "Receiving database update from LiveData");
                mRecyclerView.setVideoEntries((ArrayList) entries);
                //   if (videoEntries.size() > 0) {
                VideoAdapter videoAdapter = new VideoAdapter(MainActivity.this, (ArrayList) entries);
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

            case R.id.button_combine:
                CombineVideos combineVids = new CombineVideos(getApplicationContext());
                combineVids.mergeVideosForWeek();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            Toast.makeText(getApplicationContext(),
                    "" + videoUri,
                    Toast.LENGTH_LONG).show();

            addVideo(videoUri);
        }
    }

    private void addVideo(Uri videoUri) {
        String videoPath = getRealPathFromURI(MainActivity.this, videoUri);
        Log.d(TAG, "Merging: new videoPath= " + videoPath);
        String thumbnailFileName = generateThumbnailFileName();
        String thumbnailPath = generateThumbnail(videoPath, thumbnailFileName);
        String date = todaysDateAsString();

        // video dimensions
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        int videoWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int videoHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        retriever.release();

        // save data to DB
        final VideoEntry videoEntry = new VideoEntry(videoPath, date, videoHeight, videoWidth, thumbnailPath, thumbnailFileName);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.videoDao().insertVideo(videoEntry);
                finish();
                startActivity(getIntent());
            }
        });
    }

    private String todaysDateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String generateThumbnail(String videoPath, String thumbnailFileName) {

        Bitmap videoThumbnail = null;
        try {
            videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        } catch (Exception e) {
            Log.d(TAG, "Exception happened when making bitmap for video thumbnail");
        }
        // save bitmap to file
        String videoThumbnailsFolder = THUMBNAIL_DIRECTORY_NAME;
        String thumbnailPath = saveVideoThumbnailToAppFolder(videoThumbnail, videoThumbnailsFolder, thumbnailFileName);
        thumbnailPath += "/" + thumbnailFileName;

        return thumbnailPath;
    }

    private boolean checkReadExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(getApplicationContext(), "App needs to view thumbnails", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // call cursor loader
                    Toast.makeText(getApplicationContext(), "Read Permission Granted", Toast.LENGTH_LONG).show();
                }
                break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String generateThumbnailFileName() {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        return "Thumbnail-" + n + ".jpg";
    }

    private String saveVideoThumbnailToAppFolder(Bitmap thumbnailBitmap, String folderName, String fileName) {
        ContextWrapper cw = new ContextWrapper((getApplicationContext()));
        File directory = cw.getDir(folderName, Context.MODE_PRIVATE);
        if (!directory.exists()) directory.mkdir();
        File thumbnailPath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(thumbnailPath);
            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return directory.getAbsolutePath();
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
        // interval currently shorter for debugging
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

    private String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
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
