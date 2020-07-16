package com.example.android.videojournal.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.videojournal.R;
import com.example.android.videojournal.actionhelpers.VideoDeleter;
import com.example.android.videojournal.actionhelpers.VideoSharer;
import com.example.android.videojournal.data.VideoDatabase;
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

/**
 * Allows playing a given video in fullscreen mode using ExoPlayer
 */
public class PlayVideoActivity extends AppCompatActivity {

    private static final String TAG = PlayVideoActivity.class.getSimpleName();

    private VideoDatabase mDb;
    private SimpleExoPlayer mVideoPlayer;
    private String mVideoPath;
    private String mThumbnailPath;
    private int mPosition;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mDb = VideoDatabase.getInstance(getApplicationContext());

        setContentView(R.layout.activity_play_video);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupVideoPlayer();
    }

    /**
     * Sets the fullscreen view for playing video and initializes
     * ExoPlayer video player, sets path for video to play
     */
    private void setupVideoPlayer() {
        // PlayerView
        PlayerView fullscreenPlayVideoView = findViewById(R.id.fullscreenPlayVideoView);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTracksSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTracksSelectionFactory);

        // video player
        mVideoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

        fullscreenPlayVideoView.setUseController(true);
        mVideoPlayer.setVolume(1f); // volume ON
        fullscreenPlayVideoView.setPlayer(mVideoPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Video Journal"));

        getVideoInfo();

        if (mVideoPath != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mVideoPath));
            mVideoPlayer.prepare(videoSource);
            mVideoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Gets videopath, thumbnailpath and item position for video
     * from view's holder that launched the activity via intent
     */
    private void getVideoInfo() {
        Intent intent = getIntent();
        mVideoPath = intent.getStringExtra("VIDEO_PATH");
        mThumbnailPath = intent.getStringExtra("THUMBNAIL_PATH");
        mPosition = intent.getIntExtra("POSITION", 0);
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
                confirmDelete();
                return true;

            case R.id.buttonShare:
                VideoSharer vidSharer = new VideoSharer(PlayVideoActivity.this);
                vidSharer.shareVideo(mVideoPath);
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Delete this video?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteVideo();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteVideo() {
        VideoDeleter vidDeleter = new VideoDeleter(PlayVideoActivity.this, mDb);
        vidDeleter.deleteJournalEntryByPosition(mVideoPath, mThumbnailPath, mPosition);
        finish();
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