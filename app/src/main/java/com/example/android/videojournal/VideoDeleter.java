package com.example.android.videojournal;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;


public class VideoDeleter {

    private static final String TAG = VideoDeleter.class.getSimpleName();
    private AppDatabase mDb;
    Context context;

    public VideoDeleter(Context context, AppDatabase db) {
        mDb = db;
        this.context = context;
    }

    public void deleteJournalEntry(String videoPath, String thumbnailPath, final int position) {

        deleteVideo (videoPath);
        deleteThumbnail(thumbnailPath);
        deleteEntryFromDB(position);

    }

    public void deleteVideo (String videoPath) {

        // remove video file from storage
        File videoFile = new File(videoPath);
        boolean videoDeleted = videoFile.delete();
        Log.d(TAG, "video file first attempt - deleted: " + videoDeleted);
        if(!videoDeleted){
            context.deleteFile(videoFile.getName());
        }
        if (videoFile.exists()) {
            Log.d(TAG, "video file NOT Deleted :" + videoPath);
        } else {
            Log.d(TAG, "video file Deleted :" + videoPath);
        }
    }

    public void deleteThumbnail(String thumbnailPath) {

        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailDeleted = thumbnailFile.delete();
        if (!thumbnailDeleted) {
            context.deleteFile(thumbnailFile.getName());
        }
        if (thumbnailFile.exists()) {
            Log.d(TAG, "thumbnail file NOT Deleted :" + thumbnailPath);
        } else {
            Log.d(TAG, "thumbnail file Deleted :" + thumbnailPath);
        }
    }

    public void deleteEntryFromDB(final int position) {

        // delete from database
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // delete video entry from db
                ArrayList<VideoEntry> videoEntries = VideoAdapter.getVideos();
                mDb.videoDao().deleteVideo(videoEntries.get(position));
            }
        });

    }


}
