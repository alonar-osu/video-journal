package com.example.android.videojournal;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.widget.VideoView;
import android.provider.MediaStore;
import android.widget.MediaController;

import java.io.File;

import androidx.core.app.NavUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PlayVideoActivity extends AppCompatActivity {

    MediaController mMediaControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        VideoView mPlayVideoView = findViewById(R.id.playVideoView);

        if (mMediaControls == null) {
            mMediaControls = new MediaController(PlayVideoActivity.this);
            mMediaControls.setAnchorView(mPlayVideoView);
        }
        mPlayVideoView.setMediaController(mMediaControls);
        Intent intent = getIntent();
        String videoPath = intent.getStringExtra("VIDEO_PATH");
        Uri videoUri = Uri.fromFile(new File(videoPath));
        mPlayVideoView.setVideoURI(videoUri);

        mPlayVideoView.start();
    }
}