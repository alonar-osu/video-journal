package com.example.android.videojournal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
// getApplicationContext()
                VideoDeleter vidDeleter = new VideoDeleter(PlayVideoActivity.this, mDb);
                vidDeleter.deleteJournalEntry(mVideoPath, mThumbnailPath, mPosition);

                Intent doneDeleting = new Intent(context, MainActivity.class);
              context.startActivity(doneDeleting);
               // finish();
                    // finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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