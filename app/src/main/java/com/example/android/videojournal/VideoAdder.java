package com.example.android.videojournal;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import androidx.loader.content.CursorLoader;

public class VideoAdder {

    private static final String TAG = VideoAdder.class.getSimpleName();
    private static final String THUMBNAIL_DIRECTORY_NAME = "thumbnails";
    private AppDatabase mDb;
    Context context;

    public VideoAdder(Context context, AppDatabase db) {
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

        int combinedVideo = isCombined? 1 : 0;

        // save data to DB
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
        }

        if (videoThumbnail == null) {
            Log.d(TAG, "Thumbnail is null");
        } else {
        // save bitmap to file
        String videoThumbnailsFolder = THUMBNAIL_DIRECTORY_NAME;
        thumbnailPath = saveVideoThumbnailToAppFolder(videoThumbnail, videoThumbnailsFolder, thumbnailFileName);
        thumbnailPath += "/" + thumbnailFileName;
        }
        return thumbnailPath;
    }

    private String saveVideoThumbnailToAppFolder(Bitmap thumbnailBitmap, String folderName, String fileName) {
        ContextWrapper cw = new ContextWrapper((context));
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

    private String generateThumbnailFileName() {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        return "vj_thumb" + n + ".jpg";
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}
