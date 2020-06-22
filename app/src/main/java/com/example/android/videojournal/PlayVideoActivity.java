package com.example.android.videojournal;

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


public class PlayVideoActivity extends AppCompatActivity {

    private static final String TAG = PlayVideoActivity.class.getSimpleName();
    private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3;
    SimpleExoPlayer mVideoPlayer;
    String mVideoPath;
    String mThumbnailPath;
    int mPosition;
    Context context;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_play_video);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mDb = AppDatabase.getInstance(getApplicationContext());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.buttonDelete:

                VideoDeleter vidDeleter = new VideoDeleter(getApplicationContext(), mDb);
                if (checkWriteExternalStoragePermission()) {
                    vidDeleter.deleteVideo(mVideoPath, mThumbnailPath, mPosition);
                    finish();
                } else {
            Log.d(TAG, "NO DELETE permission");
            Toast.makeText(getApplicationContext(), "NO Delete permission", Toast.LENGTH_LONG).show();
        }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkWriteExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PlayVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(PlayVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getApplicationContext(), "App needs to delete videos", Toast.LENGTH_LONG).show();
                }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }
}