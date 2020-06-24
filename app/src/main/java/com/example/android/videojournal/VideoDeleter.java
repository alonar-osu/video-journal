package com.example.android.videojournal;

import android.content.Context;

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

    public void deleteJournalEntry(final String videoPath, final String thumbnailPath, final int position) {

        deleteVideo (videoPath);
        deleteThumbnail(thumbnailPath);
        deleteEntryFromDB(position);
    }

    public void deleteVideo (String videoPath) {

        File videoFile = new File(videoPath);
        boolean videoDeleted = videoFile.delete();
        if(!videoDeleted){
            context.deleteFile(videoFile.getName());
        }
    }

    public void deleteThumbnail(String thumbnailPath) {

        File thumbnailFile = new File(thumbnailPath);
        boolean thumbnailDeleted = thumbnailFile.delete();
        if (!thumbnailDeleted) {
            context.deleteFile(thumbnailFile.getName());
        }
    }

    public void deleteEntryFromDB(final int position) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<VideoEntry> videoEntries = VideoAdapter.getVideos();
                mDb.videoDao().deleteVideo(videoEntries.get(position));
            }
        });
    }


}
