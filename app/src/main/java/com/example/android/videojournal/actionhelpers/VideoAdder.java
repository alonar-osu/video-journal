package com.example.android.videojournal.actionhelpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.example.android.videojournal.data.VideoDatabase;
import com.example.android.videojournal.data.VideoEntry;
import com.example.android.videojournal.utilities.AppExecutors;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class VideoAdder {

    private static final String TAG = VideoAdder.class.getSimpleName();
    private static final String THUMBNAIL_DIRECTORY_NAME = "thumbnails";
    private VideoDatabase mDb;
    private Context context;

    public VideoAdder(Context context, VideoDatabase db) {
        this.context = context;
        mDb = db;
    }

    public void addVideo(String videoPath, boolean isCombined) {

        String thumbnailFileName = generateThumbnailFileName();
        String thumbnailPath = generateThumbnail(videoPath, thumbnailFileName);
        Date date = Calendar.getInstance().getTime();

        // video dimensions
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        int videoWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        Log.d(TAG, "Dimension: videoWidth=" + videoWidth);
        int videoHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        Log.d(TAG, "Dimension: videoHeight=" + videoHeight);
        retriever.release();

        // save data to DB
        int combinedVideo = isCombined? 1 : 0;
        final VideoEntry videoEntry = new VideoEntry(videoPath, date, videoHeight, videoWidth, thumbnailPath, thumbnailFileName, combinedVideo);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.videoDao().insertVideo(videoEntry);
            }
        });
    }

    private String generateThumbnail(String videoPath, String thumbnailFileName) {

        Bitmap videoThumbnail = null;
        String thumbnailPath = "";
        try {
            videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
        } catch (Exception e) {
            Log.d(TAG, "Error generating thumbnail");
        }

        if (videoThumbnail == null) {
            Log.d(TAG, "Thumbnail is null");
        } else {
            // save bitmap to file
            thumbnailPath = saveVideoThumbnailToAppFolder(videoThumbnail, thumbnailFileName);
            thumbnailPath += "/" + thumbnailFileName;
        }
        return thumbnailPath;
    }

    private String saveVideoThumbnailToAppFolder(Bitmap thumbnailBitmap, String fileName) {
        ContextWrapper cw = new ContextWrapper((context));
        File directory = cw.getDir(THUMBNAIL_DIRECTORY_NAME, Context.MODE_PRIVATE);
        if (!directory.exists()) directory.mkdir();
        File thumbnailPath = new File(directory, fileName);
        compressBitmap(thumbnailPath, thumbnailBitmap, 100);
        return directory.getAbsolutePath();
    }

    private void compressBitmap(File thumbnailPath, Bitmap thumbnailBitmap, int quality) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(thumbnailPath);
            thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private String generateThumbnailFileName() {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        return "vj_thumb" + n + ".jpg";
    }

}
