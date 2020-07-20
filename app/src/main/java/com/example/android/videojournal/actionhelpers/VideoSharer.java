package com.example.android.videojournal.actionhelpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import androidx.core.content.FileProvider;

/**
 * Allows sharing a specific video using its file path
 */
public class VideoSharer extends FileProvider {

    private Context mContext;

    public VideoSharer(Context context) {
        mContext = context;
    }
    public VideoSharer() {} // zero argument constructor

    /**
     * Shares video using its file path via intent
     * Paths that are searched for video file should exist in provider_paths.xml
     * @param videoPath absolute path to video file to share
     */
    public void shareVideo(String videoPath) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File videoToShare = new File(videoPath);

        if(videoToShare.exists()) {
            intentShareFile.setType("video/*");
            File videoFile = new File(videoPath);
            Uri videoUri = FileProvider.getUriForFile(mContext,
                    mContext.getApplicationContext().getPackageName()
                            + ".provider", videoFile);

            intentShareFile.putExtra(Intent.EXTRA_STREAM, videoUri);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Video from My Video Journal");
            intentShareFile.putExtra(Intent.EXTRA_TEXT,
                    "Here is my video from My Video Journal app");
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(Intent.createChooser(intentShareFile, "Share file"));
        }
    }

}
