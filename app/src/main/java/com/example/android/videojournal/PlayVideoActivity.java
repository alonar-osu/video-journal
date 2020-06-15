package com.example.android.videojournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;


public class PlayVideoActivity extends AppCompatActivity {

    private static final String TAG = PlayVideoActivity.class.getSimpleName();
    private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    SimpleExoPlayer mVideoPlayer;
    String mVideoPath;
    String mThumbnailPath;
    int mPosition;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_play_video);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        // PlayerView
        PlayerView fullscreenPlayVideoView = findViewById(R.id.fullscreenPlayVideoView);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTracksSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTracksSelectionFactory);

        // video player
        mVideoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        fullscreenPlayVideoView.setUseController(true);
        fullscreenPlayVideoView.setPlayer(mVideoPlayer);
        mVideoPlayer.setVolume(1f); // volume ON

        fullscreenPlayVideoView.setPlayer(mVideoPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Video Journal"));

        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra("VIDEO_PATH");
        mThumbnailPath = intent.getStringExtra("THUMBNAIL_PATH");
        mPosition = intent.getIntExtra("POSITION", 0);

        if (mVideoPath != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mVideoPath));
            mVideoPlayer.prepare(videoSource);
            mVideoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_video, menu);
        return true;
    }

    public void onDeleteAction(MenuItem item) {

        /*
        boolean havePermission =  ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
        // DEBUGGING
        Toast.makeText(getApplicationContext(), "Write permission granted: " + havePermission, Toast.LENGTH_LONG).show();
        */

        if (checkWriteExternalStoragePermission()) {
            Toast.makeText(getApplicationContext(), "Delete permission was granted", Toast.LENGTH_LONG).show();

            // remove video file from storage
            File videoFile = new File(mVideoPath);
            boolean videoDeleted = videoFile.delete();
            Log.d(TAG, "video file first attempt - deleted: " + videoDeleted);
            if(!videoDeleted){
                getApplicationContext().deleteFile(videoFile.getName());
            }
            if (videoFile.exists()) {
                Log.d(TAG, "video file NOT Deleted :" + mVideoPath);
            } else {
                Log.d(TAG, "video file Deleted :" + mVideoPath);
            }

            // delete thumbnail
            File thumbnailFile = new File(mThumbnailPath);
            boolean thumbnailDeleted = thumbnailFile.delete();
            if (!thumbnailDeleted) {
                getApplicationContext().deleteFile(thumbnailFile.getName());
            }
            if (thumbnailFile.exists()) {
                Log.d(TAG, "thumbnail file NOT Deleted :" + mThumbnailPath);
            } else {
                Log.d(TAG, "thumbnail file Deleted :" + mThumbnailPath);
            }

           // Log.d(TAG, "Rem DB test- removeVideoEntry: before removing from DB");
            // remove video entry from DB
           // SQLiteDBHelper sqliteDB = new SQLiteDBHelper(PlayVideoActivity.this);
           // int totalEntries = SQLiteDBHelper.countDBItems(PlayVideoActivity.this);
           // sqliteDB.deleteFromDB(context, mVideoPath);
           // Log.d(TAG, "Rem DB test- removeVideoEntry: after removing from DB");



            //  remove from mVideoEntries
        //    Log.d(TAG, "removeVideoEntry: before removing from recyclerview");
          //  MainActivity.removeVideoEntry(mPosition);


          //  Intent restartMainAtivIntent = new Intent(this, MainActivity.class);
       //     startActivity(restartMainAtivIntent);
             finish();



        } else {
            Log.d(TAG, "NO DELETE permission");
            Toast.makeText(getApplicationContext(), "NO Delete permission", Toast.LENGTH_LONG).show();
        }



    }

    private boolean checkWriteExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PlayVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(PlayVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getApplicationContext(), "App needs to delete videos", Toast.LENGTH_LONG).show();
                }
                //  requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
                ActivityCompat.requestPermissions(PlayVideoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                Toast.makeText(getApplicationContext(), "Called requestPermissions", Toast.LENGTH_LONG).show();
                return false;

            } else {
                // already granted
                return true;
            }
        }
        // automatically granted if sdk<23
            return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      //  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {

            case WRITE_EXTERNAL_STORAGE_REQUEST_CODE:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                        && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                { Toast.makeText(getApplicationContext(), "Write Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Write Permission NOT Granted", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}