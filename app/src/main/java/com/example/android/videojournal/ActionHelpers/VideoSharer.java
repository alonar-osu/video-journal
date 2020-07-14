package com.example.android.videojournal.ActionHelpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

import androidx.core.content.FileProvider;

public class VideoSharer extends FileProvider {

    private static final String TAG = VideoSharer.class.getSimpleName();
    private Context context;

    public VideoSharer(Context context) {
        this.context = context;
    }
    public VideoSharer() {} // zero argument constructor

    public void shareVideo(String videoPath) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File videoToShare = new File(videoPath);

        if(videoToShare.exists()) {
            intentShareFile.setType("video/*");
            File videoFile = new File(videoPath);
            Uri videoUri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".provider", videoFile);

            intentShareFile.putExtra(Intent.EXTRA_STREAM, videoUri);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Video from My Video Journal");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Here is my video from My Video Journal app");
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intentShareFile, "Share file"));
        }
    }


}
