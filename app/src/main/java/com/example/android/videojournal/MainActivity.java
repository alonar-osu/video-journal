package com.example.android.videojournal;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String THUMBNAIL_DIRECTORY_NAME = "thumbnails";
    private final static int READ_EXTERNAL_STORAGE_PERMISSION_RESULT = 0;
    private static final float THUMBNAIL_ROW_HEIGHT = 220; // in dp

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static int REC_NOTIF_ID = 100;
    static final String CHANNEL_ID_REC_NOTIF = "record_reminder";

    ArrayList mVideoEntries;
    private int mRequiredHeight;
    private int mRequiredWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // check permissions for thumbnails
        if (checkReadExternalStoragePermission()) {
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setItemViewCacheSize(20);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            mVideoEntries = SQLiteDBHelper.getVideoEntriesFromDB(MainActivity.this);
            if (mVideoEntries != null) {
                VideoAdapter videoAdapter = new VideoAdapter(MainActivity.this, mVideoEntries);
                recyclerView.setAdapter(videoAdapter);
            }
        }

        // get dimensions for video thumbnails
        findRequiredThumbnailDimensions();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on click action
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        // create channel for notifications
        createNotificationChannel();

       // int hour = 23, minute = 0;

        //TODO: get hour and minute from TimePickerFragment
      //  setUpReminderNotification(MainActivity.this, hour, minute, AlarmReceiver.class);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // call cursor loader
                    Toast.makeText(getApplicationContext(), "Permission granted to use thumbnails", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_reminder) {
            Intent startReminderActivity = new Intent(this, ReminderActivity.class);
            startActivity(startReminderActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();
            Toast.makeText(getApplicationContext(),
                    "" + videoUri,
                    Toast.LENGTH_LONG).show();
            String videoRealPath = getRealPathFromURI(MainActivity.this, videoUri);
            Log.d(TAG, "video path: " + videoRealPath);

            // generate bitmap from video
            Bitmap videoThumbnail = null;
            try {
                videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoRealPath, MediaStore.Images.Thumbnails.MINI_KIND);
            } catch (Exception e) {
                Log.d(TAG, "Exception happened when making bitmap for video thumbnail");
            }

            // DEBUGGING
            Log.d(TAG, "thumbnail byte size 1: " + videoThumbnail.getByteCount());

            if (videoThumbnail != null) {
                videoThumbnail = ThumbnailUtils.extractThumbnail(videoThumbnail, mRequiredWidth, mRequiredHeight,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            } else {
                Log.d(TAG, "Video thumbnail does not exist");
            }

            // save bitmap to file, get path
            String videoThumbnailName = generateThumbnailFileName();
            String videoThumbnailsFolder = THUMBNAIL_DIRECTORY_NAME;
            String videoThumbnailPath = saveVideoThumbnailToAppFolder(videoThumbnail, videoThumbnailsFolder, videoThumbnailName);

            // save video info and bitmap to database
            SQLiteDBHelper sqliteDB = new SQLiteDBHelper(MainActivity.this);
            if (videoUri != null) {
                // TODO: see if need the bitmapPath or not
                // see if need to save thumbnailFolder in database
                sqliteDB.saveToDB(MainActivity.this, videoUri, videoThumbnailPath, videoThumbnailName);
            }

            // restart activity to show new video thumbnail
            finish();
            startActivity(getIntent());
            // showVideo(videoUri);
        }
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

    private boolean checkReadExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(getApplicationContext(), "App needs to view thumbnails", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        }
            return true;
    }

    private void findRequiredThumbnailDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mRequiredWidth = size.x;
        final float scale = getResources().getDisplayMetrics().density;
        mRequiredHeight = (int) (THUMBNAIL_ROW_HEIGHT * scale);

        Log.d("reqwidth :", String.valueOf(mRequiredWidth));
        Log.d("reqheight :", String.valueOf(mRequiredHeight));
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

        // possibly no need to save this path
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
        // interval currently 15min for debugging
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }





}
